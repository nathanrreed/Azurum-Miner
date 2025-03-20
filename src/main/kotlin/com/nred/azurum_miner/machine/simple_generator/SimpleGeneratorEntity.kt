package com.nred.azurum_miner.machine.simple_generator

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.machine.AbstractMachineBlockEntity
import com.nred.azurum_miner.machine.ExtendedItemStackHandler
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorEntity.Companion.SimpleGeneratorEnum.PROCESSING_TIME
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorEntity.Companion.SimpleGeneratorEnum.PROGRESS
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.client.extensions.IMenuProviderExtension
import net.neoforged.neoforge.energy.IEnergyStorage

open class SimpleGeneratorEntity(pos: BlockPos, blockState: BlockState) : AbstractMachineBlockEntity(ModBlockEntities.SIMPLE_GENERATOR_ENTITY.get(), "simple_generator", pos, blockState), IMenuProviderExtension {
    override var variables = IntArray(SimpleGeneratorEnum.entries.size)
    override var variablesSize = SimpleGeneratorEnum.entries.size

    override var data: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return this@SimpleGeneratorEntity.variables[index]
        }

        override fun set(index: Int, value: Int) {
            this@SimpleGeneratorEntity.variables[index] = value
        }

        override fun getCount(): Int {
            return variables.size
        }
    }

    override fun getProgress(): Float {
        return data[PROGRESS].toFloat() / data[PROCESSING_TIME].toFloat()
    }

    init {
        data[PROGRESS] = 0
        data[PROCESSING_TIME] = 0
    }

    override val itemStackHandler = object : ExtendedItemStackHandler(6) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
            if (!level!!.isClientSide()) {
                level!!.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return stack.getBurnTime(null) > 0
        }
    }

    companion object {
        enum class SimpleGeneratorEnum() {
            PROGRESS, PROCESSING_TIME
        }

        operator fun ContainerData.get(e: Enum<*>): Int {
            return this.get(e.ordinal)
        }

        operator fun ContainerData.set(e: Enum<*>, value: Int) {
            this.set(e.ordinal, value)
        }
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): SimpleGeneratorMenu {
        return SimpleGeneratorMenu(containerId, playerInventory, ContainerLevelAccess.create(level!!, blockPos), blockPos, this.data)
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
        if (!this.loaded) return

        for (dir in Direction.entries) {
            val storage: IEnergyStorage? = level.getCapability(Capabilities.EnergyStorage.BLOCK, blockPos.relative(dir), dir.opposite)
            if (storage != null && storage.canReceive()) {
                val extracted = energyHandler.energyStored
                energyHandler.extractEnergy(extracted, false)
                val actual = storage.receiveEnergy(extracted, false)
                if (extracted != actual) {
                    energyHandler.internalInsertEnergy(extracted - actual, false)
                }
            }
        }

        if (data[PROCESSING_TIME] == 0) {
            if (!itemStackHandler.getStackInSlot(0).isEmpty) {
                data[PROCESSING_TIME] = itemStackHandler.getStackInSlot(0).getBurnTime(null) / 8
                itemStackHandler.decrement(0)
            }
        } else if (data[PROGRESS] >= data[PROCESSING_TIME]) {
            data[PROGRESS] = 0
            data[PROCESSING_TIME] = 0
        } else {
            energyHandler.internalInsertEnergy(CONFIG.getIntOrElse("$machineName.energyProduction", 50), false)
            data[PROGRESS] += 1
        }

        setChanged()
    }
}