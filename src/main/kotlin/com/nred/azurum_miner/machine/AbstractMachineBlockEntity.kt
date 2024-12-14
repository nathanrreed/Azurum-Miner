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
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.BlockCapabilityCache
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler

abstract class AbstractMachineBlockEntity(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) : BlockEntity(type, pos, blockState), MenuProvider {

    var loaded = false
    protected var capCache: BlockCapabilityCache<IItemHandler, Direction>? = null
    abstract val itemStackHandler: ItemStackHandler
    abstract val energyHandler: EnergyStorage

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

    fun drops() {
        val inventory = SimpleContainer(itemStackHandler.getSlots())
        for (i in 0..<itemStackHandler.getSlots()) {
            inventory.setItem(i, itemStackHandler.getStackInSlot(i))
        }

        Containers.dropContents(this.level!!, this.worldPosition, inventory)
    }
}