package com.nred.azurum_miner.machine.infuser

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.machine.*
import com.nred.azurum_miner.machine.infuser.InfuserEntity.Companion.InfuserEnum.*
import com.nred.azurum_miner.recipe.InfuserInput
import com.nred.azurum_miner.recipe.ModRecipe
import com.nred.azurum_miner.util.TRUE
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
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

open class InfuserEntity(pos: BlockPos, blockState: BlockState) : AbstractMachineBlockEntity(ModBlockEntities.INFUSER_ENTITY.get(), pos, blockState), IMenuProviderExtension {
    override var variables = IntArray(InfuserEnum.entries.size)
    override var variablesSize = InfuserEnum.entries.size

    override var data: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return this@InfuserEntity.variables[index]
        }

        override fun set(index: Int, value: Int) {
            this@InfuserEntity.variables[index] = value
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
        data[ENERGY_CAPACITY] = CONFIG.getInt("infuser.energyCapacity")
        data[PROCESSING_TIME] = 0
    }

    override val energyHandler = object : ExtendedEnergyStorage(data[ENERGY_CAPACITY]) {
        override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
            if (simulate) {
                return super.receiveEnergy(toReceive, simulate)
            }
            setChanged()
            val received = super.receiveEnergy(toReceive, simulate)
            data[ENERGY_LEVEL] = this.energy

            return received
        }

        override fun extractEnergy(toExtract: Int, simulate: Boolean): Int {
            if (simulate) {
                return super.extractEnergy(toExtract, simulate)
            }
            setChanged()
            val extracted = super.extractEnergy(toExtract, simulate)
            data[ENERGY_LEVEL] = this.energy
            return extracted
        }
    }

    val fluidHandler = object : ExtendedFluidTank(FLUID_SIZE, { true }) {
        override fun onContentsChanged() {
            setChanged()
            if (!level!!.isClientSide()) {
                level!!.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }

            super.onContentsChanged()
        }

        override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
            return FluidStack.EMPTY
        }
    }

    override val itemStackHandler = object : ExtendedItemStackHandler(3) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
            if (!level!!.isClientSide()) {
                level!!.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            if (slot == 0) {
                return level!!.recipeManager.getAllRecipesFor(ModRecipe.INFUSER_RECIPE_TYPE.get()).flatMap { it.value.inputItem.items.toList() }.any { it.`is`(stack.item) }
            } else if (slot == 1) {
                return level!!.recipeManager.getAllRecipesFor(ModRecipe.INFUSER_RECIPE_TYPE.get()).flatMap { it.value.catalyst.items.toList() }.any { it.`is`(stack.item) }
            } else if (slot == 2) {
                return level!!.recipeManager.getAllRecipesFor(ModRecipe.INFUSER_RECIPE_TYPE.get()).map { it.value.result }.any { it.`is`(stack.item) }
            }
            return false
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (slot == 2 || !simulate) {
                return super.extractItem(slot, amount, simulate)
            }
            return ItemStack.EMPTY
        }
    }

    override fun getProgress(): Float {
        return data[PROGRESS].toFloat() / data[PROCESSING_TIME].toFloat()
    }

    companion object {
        const val FLUID_SIZE = 50000

        enum class InfuserEnum() {
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

        data[ENERGY_CAPACITY] = CONFIG.getInt("infuser.energyCapacity")
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): InfuserMenu {
        return InfuserMenu(containerId, playerInventory, ContainerLevelAccess.create(level!!, blockPos), blockPos, this.data)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("menu.title.azurum_miner.infuser")
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
        if (!this.loaded) return
        var found = false
        for (recipe in level.recipeManager.getRecipesFor(ModRecipe.INFUSER_RECIPE_TYPE.get(), InfuserInput(state, itemStackHandler.getStackInSlot(0), itemStackHandler.getStackInSlot(1)), level).map { it.value }) {
            if (recipe != null) {
                level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, true))
                data[PROCESSING_TIME] = recipe.processingTime

                if (energyHandler.energyStored > recipe.power && data[IS_ON] == TRUE && !itemStackHandler.getStackInSlot(0).isEmpty && FluidStack.isSameFluidSameComponents(this.fluidHandler.internalDrain(recipe.inputFluid, IFluidHandler.FluidAction.SIMULATE), recipe.inputFluid)) {
                    found = true
                    if (data[PROGRESS] < recipe.processingTime) {
                        energyHandler.extractEnergy(recipe.power / recipe.processingTime, false)
                        data[PROGRESS]++
                    } else {
                        this.fluidHandler.internalDrain(recipe.inputFluid, IFluidHandler.FluidAction.EXECUTE)
                        this.itemStackHandler.decrement(0)
                        this.itemStackHandler.decrement(1)
                        this.itemStackHandler.insertItem(2, recipe.result.copy(), false)
                        data[PROGRESS] = 0
                    }
                    break
                }
            }
        }

        if (!found) {
            level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, false))
            data[PROGRESS] = 0
        }
    }
}