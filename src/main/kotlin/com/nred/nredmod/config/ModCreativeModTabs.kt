package com.nred.nredmod.config

import com.nred.nredmod.NredMod
import com.nred.nredmod.machine.ModMachines
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object ModCreativeModTabs {
    val CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NredMod.ID)

    //.withTabsBefore(ResourceLocation.fromNamespaceAndPath(NredMod.ID, "nred_items_tab"))
    val NRED_MOD_TAB = CREATIVE_TABS.register("nred_items_tab", { -> CreativeModeTab.builder().icon({ -> ItemStack(ModMachines.MINER_BLOCK_TIERS[0].asItem()) }).title(Component.translatable("creativetab.nredmod.nred_items_tab")).build() })

    fun register(eventBus: IEventBus) {
        CREATIVE_TABS.register(eventBus)
    }
}