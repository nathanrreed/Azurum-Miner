package com.nred.azurum_miner.compat.jade

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.AbstractMachineBlockEntity
import com.nred.azurum_miner.machine.generator.GeneratorEntity
import com.nred.azurum_miner.machine.infuser.InfuserEntity
import com.nred.azurum_miner.machine.liquifier.LiquifierEntity
import com.nred.azurum_miner.machine.miner.Miner
import com.nred.azurum_miner.machine.miner.MinerEntity
import com.nred.azurum_miner.machine.miner.OUTPUT
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import snownee.jade.api.*
import snownee.jade.api.view.*

@WailaPlugin
class JadePlugin : IWailaPlugin {
    override fun register(registration: IWailaCommonRegistration) {
        registration.registerItemStorage(MinerItemHider.INSTANCE, Miner::class.java)
        registration.registerProgress(MachineProgressTime.INSTANCE, MinerEntity::class.java)
        registration.registerProgress(MachineProgressTime.INSTANCE, InfuserEntity::class.java)
        registration.registerProgress(MachineProgressTime.INSTANCE, LiquifierEntity::class.java)
        registration.registerProgress(MachineProgressTime.INSTANCE, TransmogrifierEntity::class.java)
        registration.registerEnergyStorage(MachineEnergy.INSTANCE, MinerEntity::class.java)
        registration.registerEnergyStorage(MachineEnergy.INSTANCE, InfuserEntity::class.java)
        registration.registerEnergyStorage(MachineEnergy.INSTANCE, LiquifierEntity::class.java)
        registration.registerEnergyStorage(MachineEnergy.INSTANCE, TransmogrifierEntity::class.java)
    }

    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerItemStorageClient(MinerItemHider.INSTANCE)
        registration.registerProgressClient(MachineProgressTime.INSTANCE)
        registration.registerEnergyStorageClient(MachineEnergy.INSTANCE)
    }
}

enum class MinerItemHider : IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
    INSTANCE {
        override fun getUid(): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner.hide_filter")
        }

        override fun getClientGroups(accessor: Accessor<*>, groups: List<ViewGroup<ItemStack>>): List<ClientViewGroup<ItemView>> {
            return ClientViewGroup.map(groups, ::ItemView, null)
        }

        override fun getGroups(accessor: Accessor<*>): List<ViewGroup<ItemStack>> {
            val entity = accessor.target as MinerEntity
            if (!entity.itemStackHandler.getStackInSlot(OUTPUT).isEmpty) {
                return listOf(ViewGroup(listOf(entity.itemStackHandler.getStackInSlot(OUTPUT))))
            }
            return emptyList()
        }
    }
}

enum class MachineProgressTime : IClientExtensionProvider<CompoundTag, ProgressView>, IServerExtensionProvider<CompoundTag> {
    INSTANCE {
        override fun getUid(): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "machine.progress")
        }

        override fun getClientGroups(accessor: Accessor<*>, groups: List<ViewGroup<CompoundTag>>): List<ClientViewGroup<ProgressView>> {
            return ClientViewGroup.map(groups, ProgressView::read, null)
        }

        override fun getGroups(accessor: Accessor<*>): List<ViewGroup<CompoundTag>>? {
            val entity = accessor.target
            return if (entity is AbstractMachineBlockEntity && entity !is GeneratorEntity) {
                listOf(ViewGroup(listOf(ProgressView.create(entity.getProgress()))))
            } else {
                null
            }
        }
    }
}

enum class MachineEnergy : IClientExtensionProvider<CompoundTag, EnergyView>, IServerExtensionProvider<CompoundTag> {
    INSTANCE {
        override fun getUid(): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "machine.energy")
        }

        override fun getClientGroups(accessor: Accessor<*>, groups: List<ViewGroup<CompoundTag>>): List<ClientViewGroup<EnergyView>> {
            return groups.map { data ->
                val unit = data.getExtraData().getString("Unit")
                return@map ClientViewGroup(data.views.map { tag -> EnergyView.read(tag, unit) })
            }
        }

        override fun getGroups(accessor: Accessor<*>): List<ViewGroup<CompoundTag>>? {
            val entity = accessor.target
            if (entity is AbstractMachineBlockEntity) {
                val stored = entity.energyHandler.energyStored.toLong()
                val cap = entity.energyHandler.maxEnergyStored.toLong()
                val group = if (stored.toDouble() / cap.toDouble() < 0.999999) { // Remove annoying flickering while in use
                    ViewGroup(listOf(EnergyView.of(stored, cap)))
                } else {
                    ViewGroup(listOf(EnergyView.of(cap, cap)))
                }
                group.getExtraData().putString("Unit", "FE")
                return listOf(group)
            }
            return null
        }
    }
}