package com.nred.azurum_miner.machine.liquifier

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.machine.AbstractMachine
import com.nred.azurum_miner.machine.AbstractMachineBlockEntity
import com.nred.azurum_miner.machine.liquifier.LiquifierEntity.Companion.LiquifierEnum.*
import com.nred.azurum_miner.recipe.LiquifierInput
import com.nred.azurum_miner.recipe.ModRecipe
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
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
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import net.neoforged.neoforge.items.ItemStackHandler
import kotlin.jvm.optionals.getOrNull

open class LiquifierEntity(pos: BlockPos, blockState: BlockState) : AbstractMachineBlockEntity(ModBlockEntities.LIQUIFIER_ENTITY.get(), pos, blockState), IMenuProviderExtension {
    private var variables = IntArray(LiquifierEnum.entries.size)

    private var data: ContainerData = object : ContainerData {
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
        data[IS_ON] = 1
        data[ENERGY_LEVEL] = 0
        data[PROGRESS] = 0
        data[ENERGY_CAPACITY] = CONFIG.getInt("liquifier.energyCapacity")
        data[PROCESSING_TIME] = 0
    }

    override val energyHandler = object : EnergyStorage(data[ENERGY_CAPACITY]) {
        override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
            setChanged()
            val rtn = super.receiveEnergy(toReceive, simulate)
            data[ENERGY_LEVEL] = this.energy

            return rtn
        }

        override fun extractEnergy(toExtract: Int, simulate: Boolean): Int {
            setChanged()
            data[ENERGY_LEVEL] = this.energy
            return super.extractEnergy(toExtract, simulate)
        }
    }

    val fluidHandler = object : FluidTank(FLUID_SIZE, { true }) {
        override fun onContentsChanged() {
            setChanged()
            if (!level!!.isClientSide()) {
                level!!.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }

            super.onContentsChanged()
        }
    }

    override val itemStackHandler = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
            if (!level!!.isClientSide()) {
                level!!.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return level!!.recipeManager.getAllRecipesFor(ModRecipe.LIQUIFIER_RECIPE_TYPE.get()).flatMap { it.value.inputItem.items.toList() }.any { it.`is`(stack.item) }
        }
    }

    companion object {
        const val FLUID_SIZE = 50000

        enum class LiquifierEnum() {
            IS_ON, ENERGY_LEVEL, PROGRESS, ENERGY_CAPACITY, PROCESSING_TIME
        }

        operator fun ContainerData.get(e: Enum<*>): Int {
            return this.get(e.ordinal)
        }

        operator fun ContainerData.set(e: Enum<*>, value: Int) {
            this.set(e.ordinal, value)
        }
    }

    override fun onLoad() {
        super.onLoad()

        this.loaded = true
    }

    override fun handleUpdateTag(tag: CompoundTag, lookupProvider: HolderLookup.Provider) {
        super.handleUpdateTag(tag, lookupProvider)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.put("inventory", itemStackHandler.serializeNBT(registries))

        tag.putIntArray("vars", variables)

        tag.put("energy", energyHandler.serializeNBT(registries))
        fluidHandler.writeToNBT(registries, tag)

        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        itemStackHandler.deserializeNBT(registries, tag.getCompound("inventory"))

        variables = tag.getIntArray("vars")

        energyHandler.deserializeNBT(registries, tag.get("energy")!!)
        fluidHandler.readFromNBT(registries, tag)
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): LiquifierMenu {
        return LiquifierMenu(containerId, playerInventory, ContainerLevelAccess.create(level!!, blockPos), blockPos, this.data)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("menu.title.azurum_miner.liquifier")
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
        if (!this.loaded) return
        val recipe = level.recipeManager.getRecipeFor(ModRecipe.LIQUIFIER_RECIPE_TYPE.get(), LiquifierInput(state, itemStackHandler.getStackInSlot(0)), level).getOrNull()?.value
        if (recipe != null) {
            data[PROCESSING_TIME] = recipe.processingTime
            if (energyHandler.energyStored > recipe.power / recipe.processingTime && data[IS_ON] == 1 && !itemStackHandler.getStackInSlot(0).isEmpty && this.fluidHandler.fill(recipe.result, IFluidHandler.FluidAction.SIMULATE) == recipe.result.amount) {
                level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, true))
                if (data[PROGRESS] < recipe.processingTime) {
                    energyHandler.extractEnergy(recipe.power / recipe.processingTime, false)
                    data[PROGRESS]++
                } else {
                    this.fluidHandler.fill(recipe.result, IFluidHandler.FluidAction.EXECUTE)
                    this.itemStackHandler.extractItem(0, 1, false)
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