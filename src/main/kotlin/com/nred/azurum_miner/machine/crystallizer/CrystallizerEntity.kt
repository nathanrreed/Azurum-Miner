package com.nred.azurum_miner.machine.crystallizer

import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.machine.AbstractMachine
import com.nred.azurum_miner.machine.AbstractMachineBlockEntity
import com.nred.azurum_miner.machine.ExtendedItemStackHandler
import com.nred.azurum_miner.machine.crystallizer.CrystallizerEntity.Companion.CrystallizerEnum.*
import com.nred.azurum_miner.recipe.CrystallizerInput
import com.nred.azurum_miner.recipe.ModRecipe
import com.nred.azurum_miner.util.TRUE
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.extensions.IMenuProviderExtension
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import kotlin.jvm.optionals.getOrNull

open class CrystallizerEntity(pos: BlockPos, blockState: BlockState) : AbstractMachineBlockEntity(ModBlockEntities.CRYSTALLIZER_ENTITY.get(), "crystallizer", pos, blockState), IMenuProviderExtension {
    override var variables = IntArray(CrystallizerEnum.entries.size)
    override var variablesSize = CrystallizerEnum.entries.size

    override var data: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return this@CrystallizerEntity.variables[index]
        }

        override fun set(index: Int, value: Int) {
            this@CrystallizerEntity.variables[index] = value
        }

        override fun getCount(): Int {
            return variables.size
        }
    }

    fun updateEnumData(index: Int, value: Int) {
        this.data.set(index, value)
    }

    override fun validFluidSlot(tank: Int, stack: FluidStack): Boolean {
        return level!!.recipeManager.getAllRecipesFor(ModRecipe.CRYSTALLIZER_RECIPE_TYPE.get()).any { it.value.inputFluid.`is`(stack.fluid) }
    }

    init {
        data[IS_ON] = TRUE
        data[PROGRESS] = 0
        data[PROCESSING_TIME] = 0
    }

    override val itemStackHandler = object : ExtendedItemStackHandler(2) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
            if (!level!!.isClientSide()) {
                level!!.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return when (slot) {
                0 -> {
                    level!!.recipeManager.getAllRecipesFor(ModRecipe.CRYSTALLIZER_RECIPE_TYPE.get()).flatMap { it.value.inputItem.items.toList() }.any { it.`is`(stack.item) }
                }

                1 -> {
                    level!!.recipeManager.getAllRecipesFor(ModRecipe.CRYSTALLIZER_RECIPE_TYPE.get()).map { it.value.result }.any { it.`is`(stack.item) }
                }

                else -> false
            }
        }

        override fun itemOutput(slot: Int): Boolean {
            return slot == 1
        }
    }

    override fun getProgress(): Float {
        return data[PROGRESS].toFloat() / data[PROCESSING_TIME].toFloat()
    }

    companion object {
        const val FLUID_SIZE = 50000

        enum class CrystallizerEnum() {
            IS_ON, PROGRESS, PROCESSING_TIME
        }

        operator fun ContainerData.get(e: Enum<*>): Int {
            return this.get(e.ordinal)
        }

        operator fun ContainerData.set(e: Enum<*>, value: Int) {
            this.set(e.ordinal, value)
        }
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): CrystallizerMenu {
        return CrystallizerMenu(containerId, playerInventory, ContainerLevelAccess.create(level!!, blockPos), blockPos, this.data)
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
        if (!this.loaded) return
        val recipe = level.recipeManager.getRecipeFor(ModRecipe.CRYSTALLIZER_RECIPE_TYPE.get(), CrystallizerInput(state, itemStackHandler.getStackInSlot(0), fluidHandler.getFluidInTank(0)), level).getOrNull()?.value
        if (recipe != null) {
            val power = Mth.ceil(recipe.powerMult * baseEnergy)
            data[PROCESSING_TIME] = recipe.processingTime
            if (energyHandler.energyStored >= power && data[IS_ON] == TRUE && !itemStackHandler.getStackInSlot(0).isEmpty && this.fluidHandler.internalExtractFluid(recipe.inputFluid, IFluidHandler.FluidAction.SIMULATE).amount == recipe.inputFluid.amount) {
                level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, true))
                if (data[PROGRESS] < recipe.processingTime) {
                    energyHandler.internalExtractEnergy(power / recipe.processingTime, false)
                    data[PROGRESS]++
                } else {
                    this.fluidHandler.internalExtractFluid(recipe.inputFluid, IFluidHandler.FluidAction.EXECUTE)

                    if (level.random.nextIntBetweenInclusive(0, 100) < Mth.floor(100 * recipe.rate)) // Chance to be used
                        this.itemStackHandler.decrement(0)
                    this.itemStackHandler.internalInsertItem(1, recipe.result.copy(), false)
                    data[PROGRESS] = 0
                }
            } else {
                level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, false))
                data[PROGRESS] = 0
            }
        } else {
            level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, false))
            data[PROGRESS] = 0
        }
    }
}