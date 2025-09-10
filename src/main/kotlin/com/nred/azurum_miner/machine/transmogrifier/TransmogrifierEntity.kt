package com.nred.azurum_miner.machine.transmogrifier

import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.machine.AbstractMachine
import com.nred.azurum_miner.machine.AbstractMachineBlockEntity
import com.nred.azurum_miner.machine.ExtendedItemStackHandler
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierEntity.Companion.TransmogrifierEnum.*
import com.nred.azurum_miner.recipe.ModRecipe
import com.nred.azurum_miner.recipe.TransmogrifierInput
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
import kotlin.jvm.optionals.getOrNull

open class TransmogrifierEntity(pos: BlockPos, blockState: BlockState) : AbstractMachineBlockEntity(ModBlockEntities.TRANSMOGRIFIER_ENTITY.get(), "transmogrifier", pos, blockState), IMenuProviderExtension {
    override var variables = IntArray(TransmogrifierEnum.entries.size)
    override var variablesSize = TransmogrifierEnum.entries.size

    override var data: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return this@TransmogrifierEntity.variables[index]
        }

        override fun set(index: Int, value: Int) {
            this@TransmogrifierEntity.variables[index] = value
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

    override val itemStackHandler = object : ExtendedItemStackHandler(2) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
            if (!level!!.isClientSide()) {
                level!!.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            if (slot == 0) {
                return level!!.recipeManager.getAllRecipesFor(ModRecipe.TRANSMOGRIFIER_RECIPE_TYPE.get()).flatMap { it.value.inputItem.items.toList() }.any { it.`is`(stack.item) }
            } else if (slot == 1) {
                return level!!.recipeManager.getAllRecipesFor(ModRecipe.TRANSMOGRIFIER_RECIPE_TYPE.get()).map { it.value.result }.any { it.`is`(stack.item) }
            }
            return false
        }

        override fun itemOutput(slot: Int): Boolean {
            return slot == 1
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (slot == 1 || !simulate) {
                return super.extractItem(slot, amount, simulate)
            }
            return ItemStack.EMPTY
        }
    }

    override fun getProgress(): Float {
        return data[PROGRESS].toFloat() / data[PROCESSING_TIME].toFloat()
    }

    companion object {
        enum class TransmogrifierEnum() {
            IS_ON, PROGRESS, PROCESSING_TIME
        }

        operator fun ContainerData.get(e: Enum<*>): Int {
            return this.get(e.ordinal)
        }

        operator fun ContainerData.set(e: Enum<*>, value: Int) {
            this.set(e.ordinal, value)
        }
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): TransmogrifierMenu {
        return TransmogrifierMenu(containerId, playerInventory, ContainerLevelAccess.create(level!!, blockPos), blockPos, this.data)
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
        if (!this.loaded) return
        val recipe = level.recipeManager.getRecipeFor(ModRecipe.TRANSMOGRIFIER_RECIPE_TYPE.get(), TransmogrifierInput(state, itemStackHandler.getStackInSlot(0)), level).getOrNull()?.value
        if (recipe != null) {
            val power = Mth.ceil(recipe.powerMult * baseEnergy)
            level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, true))
            data[PROCESSING_TIME] = recipe.processingTime
            if (energyHandler.energyStored >= power && data[IS_ON] == TRUE && !itemStackHandler.getStackInSlot(0).isEmpty && this.itemStackHandler.internalInsertItem(1, recipe.result, true).isEmpty) {
                if (data[PROGRESS] < recipe.processingTime) {
                    energyHandler.internalExtractEnergy(power / recipe.processingTime, false)
                    data[PROGRESS]++
                } else {
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