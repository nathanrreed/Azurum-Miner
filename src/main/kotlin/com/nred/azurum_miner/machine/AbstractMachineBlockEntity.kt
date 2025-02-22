@file:Suppress("UsePropertyAccessSyntax")

package com.nred.azurum_miner.machine

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.BlockCapabilityCache
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler

open class ExtendedEnergyStorage(capacity: Int) : EnergyStorage(capacity) {
    open fun addEnergy(toReceive: Int, simulate: Boolean): Int {
        return 0
    }
}

open class ExtendedItemStackHandler(capacity: Int) : ItemStackHandler(capacity) {
    open fun decrement(slot: Int, amount: Int = 1) {
        super.extractItem(slot, amount, false)
    }
}

open class ExtendedFluidTank(capacity: Int, function: (FluidStack) -> Boolean) : FluidTank(capacity, function) {
    fun internalDrain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        return super.drain(maxDrain, action)
    }

    fun internalDrain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        return if (!resource.isEmpty() && FluidStack.isSameFluidSameComponents(resource, this.fluid)) this.internalDrain(resource.getAmount(), action) else FluidStack.EMPTY
    }
}

@Suppress("UsePropertyAccessSyntax")
abstract class AbstractMachineBlockEntity(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) : BlockEntity(type, pos, blockState), MenuProvider {
    var loaded = false
    protected var capCache: BlockCapabilityCache<IItemHandler, Direction>? = null
    abstract var variables: IntArray
    abstract var variablesSize: Int
    abstract val itemStackHandler: ExtendedItemStackHandler
    abstract val energyHandler: ExtendedEnergyStorage
    abstract var data: ContainerData
    abstract fun getProgress(): Float


    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? {
        return null
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        saveAdditional(tag, registries)
        return tag
    }

    override fun onLoad() {
        super.onLoad()

        if (variables.size < variablesSize) {
            val temp = IntArray(variablesSize) { _ -> 0 }
            variables = variables.copyInto(temp)
        }

        if (!level!!.isClientSide) {
            capCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, level as ServerLevel, blockPos, Direction.NORTH)
        }
    }

    override fun collectImplicitComponents(components: DataComponentMap.Builder) {
        val tag = CompoundTag()
        saveAdditional(tag, level!!.registryAccess())
        components.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
        super.collectImplicitComponents(components)
    }

    override fun applyImplicitComponents(componentInput: DataComponentInput) {
        componentInput.get(DataComponents.CUSTOM_DATA)?.loadInto(this, level!!.registryAccess())
        super.applyImplicitComponents(componentInput)
    }

    open fun drops() {
        val inventory = SimpleContainer(itemStackHandler.getSlots())
        for (i in 0..<itemStackHandler.getSlots()) {
            inventory.setItem(i, itemStackHandler.getStackInSlot(i))
        }
    }
}