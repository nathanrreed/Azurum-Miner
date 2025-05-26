package com.nred.azurum_miner.machine.liquifier

import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.machine.AbstractMachine
import com.nred.azurum_miner.machine.AbstractMachineBlockEntity
import com.nred.azurum_miner.machine.ExtendedItemStackHandler
import com.nred.azurum_miner.machine.liquifier.LiquifierEntity.Companion.LiquifierEnum.*
import com.nred.azurum_miner.recipe.LiquifierInput
import com.nred.azurum_miner.recipe.ModRecipe
import com.nred.azurum_miner.util.TRUE
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.client.extensions.IMenuProviderExtension
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import kotlin.jvm.optionals.getOrNull

open class LiquifierEntity(pos: BlockPos, blockState: BlockState) : AbstractMachineBlockEntity(ModBlockEntities.LIQUIFIER_ENTITY.get(), "liquifier", pos, blockState), IMenuProviderExtension {
    override var variables = IntArray(LiquifierEnum.entries.size)
    override var variablesSize = LiquifierEnum.entries.size

    override var data: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return this@LiquifierEntity.variables[index]
        }

        override fun set(index: Int, value: Int) {
            this@LiquifierEntity.variables[index] = value
        }

        override fun getCount(): Int {
            return variables.size
        }
    }

    fun updateEnumData(index: Int, value: Int) {
        this.data.set(index, value)
    }

    init {
        data[IS_ON] = TRUE
        data[PROGRESS] = 0
        data[PROCESSING_TIME] = 0
    }

    override val itemStackHandler = object : ExtendedItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
            if (!level!!.isClientSide()) {
                level!!.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return level!!.recipeManager.getAllRecipesFor(ModRecipe.LIQUIFIER_RECIPE_TYPE.get()).flatMap { it.value.inputItem.items.toList() }.any { it.`is`(stack.item) }
        }

        override fun itemOutput(slot: Int): Boolean {
            return false
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (!simulate) {
                return super.extractItem(slot, amount, simulate)
            }
            return ItemStack.EMPTY
        }
    }

    override fun validFluidSlot(tank: Int, stack: FluidStack): Boolean {
        if (tank == 0)
            return stack.fluid.isSame(Fluids.WATER)
        return true
    }

    override fun canOutputSlot(tank: Int): Boolean {
        return tank == 1
    }

    override fun canInputSlot(tank: Int): Boolean {
        return tank == 0
    }

    override fun getProgress(): Float {
        return data[PROGRESS].toFloat() / data[PROCESSING_TIME].toFloat()
    }

    companion object {
        const val FLUID_SIZE = 50000

        enum class LiquifierEnum() {
            IS_ON, PROGRESS, PROCESSING_TIME
        }

        operator fun ContainerData.get(e: Enum<*>): Int {
            return this.get(e.ordinal)
        }

        operator fun ContainerData.set(e: Enum<*>, value: Int) {
            this.set(e.ordinal, value)
        }
    }


    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): LiquifierMenu {
        return LiquifierMenu(containerId, playerInventory, ContainerLevelAccess.create(level!!, blockPos), blockPos, this.data)
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
        if (!this.loaded) return
        val recipe = level.recipeManager.getRecipeFor(ModRecipe.LIQUIFIER_RECIPE_TYPE.get(), LiquifierInput(state, itemStackHandler.getStackInSlot(0), fluidHandler.getFluidInTank(0)), level).getOrNull()?.value
        if (recipe != null) {
            data[PROCESSING_TIME] = recipe.processingTime
            if (energyHandler.energyStored >= recipe.power && data[IS_ON] == TRUE && !itemStackHandler.getStackInSlot(0).isEmpty && this.fluidHandler.internalExtractFluid(recipe.inputFluid, IFluidHandler.FluidAction.SIMULATE).amount == recipe.inputFluid.amount && this.fluidHandler.internalInsertFluid(recipe.result, IFluidHandler.FluidAction.SIMULATE) == recipe.result.amount) {
                level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, true))
                if (data[PROGRESS] < recipe.processingTime) {
                    energyHandler.extractEnergy(recipe.power / recipe.processingTime, false)
                    data[PROGRESS]++
                } else {
                    this.fluidHandler.internalInsertFluid(recipe.result, IFluidHandler.FluidAction.EXECUTE)
                    this.fluidHandler.internalExtractFluid(recipe.inputFluid, IFluidHandler.FluidAction.EXECUTE)
                    this.itemStackHandler.decrement(0)
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