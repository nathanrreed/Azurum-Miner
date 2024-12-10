package com.nred.nredmod.screen

import com.nred.nredmod.NredMod
import com.nred.nredmod.machine.infuser.InfuserMenu
import com.nred.nredmod.machine.liquifier.LiquifierMenu
import com.nred.nredmod.machine.miner.MinerMenu
import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.MenuType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

class ModMenuTypes {
    companion object {
        val MENUS = DeferredRegister.create(Registries.MENU, NredMod.ID)

        val MINER_MENU: DeferredHolder<MenuType<*>, MenuType<MinerMenu>> = MENUS.register("miner_menu", { -> IMenuTypeExtension.create(::MinerMenu) })
        val LIQUIFIER_MENU: DeferredHolder<MenuType<*>, MenuType<LiquifierMenu>> = MENUS.register("liquifier_menu", { -> IMenuTypeExtension.create(::LiquifierMenu) })
        val INFUSER_MENU: DeferredHolder<MenuType<*>, MenuType<InfuserMenu>> = MENUS.register("infuser_menu", { -> IMenuTypeExtension.create(::InfuserMenu) })

        fun register(eventBus: IEventBus) {
            MENUS.register(eventBus)
        }
    }
}