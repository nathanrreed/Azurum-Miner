package com.nred.azurum_miner.compat.jade

import com.nred.azurum_miner.machine.AbstractMachineBlockEntity
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.infuser.Infuser
import com.nred.azurum_miner.machine.liquifier.Liquifier
import com.nred.azurum_miner.machine.miner.Miner
import com.nred.azurum_miner.machine.miner.MinerEntity
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.get
import com.nred.azurum_miner.machine.transmogrifier.Transmogrifier
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import snownee.jade.api.*
import snownee.jade.api.config.IPluginConfig
import snownee.jade.api.view.*

@WailaPlugin
class JadePlugin : IWailaPlugin {
    override fun register(registration: IWailaCommonRegistration) {
        registration.registerItemStorage(MinerItemHider.INSTANCE, Miner::class.java)
        registration.registerBlockDataProvider(MachineProgressTime.INSTANCE, Miner::class.java)
        registration.registerBlockDataProvider(MachineProgressTime.INSTANCE, Infuser::class.java)
        registration.registerBlockDataProvider(MachineProgressTime.INSTANCE, Liquifier::class.java)
        registration.registerBlockDataProvider(MachineProgressTime.INSTANCE, Transmogrifier::class.java)
    }

    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerItemStorageClient(MinerItemHider.INSTANCE)
        registration.registerBlockComponent(MachineProgressTime.INSTANCE, Miner::class.java)
        registration.registerBlockComponent(MachineProgressTime.INSTANCE, Infuser::class.java)
        registration.registerBlockComponent(MachineProgressTime.INSTANCE, Liquifier::class.java)
        registration.registerBlockComponent(MachineProgressTime.INSTANCE, Transmogrifier::class.java)
    }
}

enum class MinerItemHider : IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
    INSTANCE {
        override fun getClientGroups(accessor: Accessor<*>, groups: List<ViewGroup<ItemStack>>): List<ClientViewGroup<ItemView>> {
            return emptyList()
        }

        override fun getUid(): ResourceLocation {
            return ModMachines.MINER_BLOCK_TIERS[0].id
        }

        override fun getGroups(accessor: Accessor<*>): List<ViewGroup<ItemStack>> {
            return emptyList()
        }
    }
}

enum class MachineProgressTime : IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE {
        override fun getUid(): ResourceLocation {
            return ModMachines.MINER_BLOCK_TIERS[0].id
        }

        override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
            tooltip.add(Component.translatable("jade.azurum_miner.machine.progress", getTime(accessor.serverData.getInt("progress"))))
        }

        override fun appendServerData(data: CompoundTag, accessor: BlockAccessor) {
            val entity = accessor.blockEntity
            if (entity is MinerEntity) {
                data.putInt("progress", entity.getTicks() - entity.data[entity.EXTERN_PROGRESS])
            } else if (entity is AbstractMachineBlockEntity) {
                data.putInt("progress", entity.getTicks() - entity.data[entity.EXTERN_PROGRESS.ordinal])
            }
        }
    }
}