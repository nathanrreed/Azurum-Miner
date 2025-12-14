@file:Suppress("UsePropertyAccessSyntax")

package com.nred.azurum_miner.machine

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.machine.infuser.InfuserEntity.Companion.FLUID_SIZE
import com.nred.azurum_miner.util.CustomEnergyHandler
import com.nred.azurum_miner.util.CustomFluidStackHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.BlockCapabilityCache
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler

abstract class ExtendedItemStackHandler(capacity: Int) : ItemStackHandler(capacity) {
    open fun decrement(slot: Int, amount: Int = 1) {
        super.extractItem(slot, amount, false)
    }

    abstract fun itemOutput(slot: Int): Boolean

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (itemOutput(slot) || !simulate)
            return super.extractItem(slot, amount, simulate)
        return ItemStack.EMPTY
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (!itemOutput(slot))
            return super.insertItem(slot, stack, simulate)
        return stack
    }

    fun internalInsertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return super.insertItem(slot, stack, simulate)
    }
}

@Suppress("UsePropertyAccessSyntax")
abstract class AbstractMachineBlockEntity(type: BlockEntityType<*>, val machineName: String, pos: BlockPos, blockState: BlockState) : BlockEntity(type, pos, blockState), MenuProvider {
    var loaded = false
    protected var capCache: BlockCapabilityCache<IItemHandler?, Direction?>? = null
    abstract var variables: IntArray
    abstract var variablesSize: Int
    abstract val itemStackHandler: ExtendedItemStackHandler
    abstract var data: ContainerData
    abstract fun getProgress(): Float

    val baseEnergy = CONFIG.getIntOrElse("$machineName.baseEnergyRequired", 0)

    override fun onLoad() {
        super.onLoad()

        if (!level!!.isClientSide) {
            capCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, level as ServerLevel, blockPos, Direction.NORTH)
        }

        this.loaded = true
    }

    open val energyHandler = object : CustomEnergyHandler(CONFIG.getIntOrElse("$machineName.energyCapacity", 0), MachineInfo.data[machineName]!!.allowEnergyInput, MachineInfo.data[machineName]!!.allowEnergyOutput) {
        override fun onContentsChanged() {
            setChanged()
            if (level != null && !level!!.isClientSide()) {
                level!!.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }
        }
    }

    open val fluidHandler = object : CustomFluidStackHandler(FLUID_SIZE, MachineInfo.data[machineName]!!.numTanks, MachineInfo.data[machineName]!!.allowFluidInput, MachineInfo.data[machineName]!!.allowFluidOutput) {
        override fun onContentsChanged() {
            setChanged()
            if (level != null && !level!!.isClientSide()) {
                level!!.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }
        }

        override fun isFluidValid(tank: Int, stack: FluidStack): Boolean {
            return validFluidSlot(tank, stack)
        }

        override fun canOutput(tank: Int): Boolean {
            return canOutputSlot(tank)
        }

        override fun canInput(tank: Int): Boolean {
            return canInputSlot(tank)
        }
    }

    open fun validFluidSlot(tank: Int, stack: FluidStack): Boolean {
        return true
    }

    open fun canOutputSlot(tank: Int): Boolean {
        return true
    }

    open fun canInputSlot(tank: Int): Boolean {
        return true
    }

    override fun getDisplayName(): Component {
        return Component.translatable("menu.title.azurum_miner.$machineName")
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.put("inventory", itemStackHandler.serializeNBT(registries))
        tag.put("fluids", fluidHandler.serializeNBT(registries))
        tag.put("energy", energyHandler.serializeNBT(registries))

        tag.putIntArray("vars", variables)
        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        itemStackHandler.deserializeNBT(registries, tag.getCompound("inventory"))
        fluidHandler.deserializeNBT(registries, tag.getCompound("fluids"))
        energyHandler.deserializeNBT(registries, tag.get("energy")!!)

        if (tag.getIntArray("vars").size == variablesSize) {
            variables = tag.getIntArray("vars")
        }
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        saveAdditional(tag, registries)
        return tag
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun collectImplicitComponents(components: DataComponentMap.Builder) {
        val tag = CompoundTag()
        saveAdditional(tag, level!!.registryAccess())
        components.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
        super.collectImplicitComponents(components)
    }

    override fun applyImplicitComponents(componentInput: DataComponentInput) {
        componentInput.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).loadInto(this, level!!.registryAccess())
        super.applyImplicitComponents(componentInput)
    }

    open fun drops() {
        val inventory = SimpleContainer(itemStackHandler.getSlots())
        for (i in 0..<itemStackHandler.getSlots()) {
            inventory.setItem(i, itemStackHandler.getStackInSlot(i))
        }
    }
}