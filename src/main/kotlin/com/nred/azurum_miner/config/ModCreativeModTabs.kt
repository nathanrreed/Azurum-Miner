package com.nred.azurum_miner.config

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.ModMachines
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModCreativeModTabs {
    val CREATIVE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AzurumMiner.ID)

    //.withTabsBefore(ResourceLocation.fromNamespaceAndPath(AzurumMod.ID, "azurum_miner_items_tab"))
    val MOD_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> = CREATIVE_TABS.register("azurum_miner_items_tab") { -> CreativeModeTab.builder().icon { -> ItemStack(ModMachines.MINER_BLOCK_TIERS[0].asItem()) }.title(Component.translatable("creative_tab.azurum_miner.azurum_miner_items_tab")).build() }

    fun register(eventBus: IEventBus) {
        CREATIVE_TABS.register(eventBus)
    }
}