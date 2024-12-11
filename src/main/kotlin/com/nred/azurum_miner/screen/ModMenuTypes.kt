package com.nred.azurum_miner.screen

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.infuser.InfuserMenu
import com.nred.azurum_miner.machine.liquifier.LiquifierMenu
import com.nred.azurum_miner.machine.miner.MinerMenu
import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.MenuType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

class ModMenuTypes {
    companion object {
        val MENUS = DeferredRegister.create(Registries.MENU, AzurumMiner.ID)

        val MINER_MENU: DeferredHolder<MenuType<*>, MenuType<MinerMenu>> = MENUS.register("miner_menu", { -> IMenuTypeExtension.create(::MinerMenu) })
        val LIQUIFIER_MENU: DeferredHolder<MenuType<*>, MenuType<LiquifierMenu>> = MENUS.register("liquifier_menu", { -> IMenuTypeExtension.create(::LiquifierMenu) })
        val INFUSER_MENU: DeferredHolder<MenuType<*>, MenuType<InfuserMenu>> = MENUS.register("infuser_menu", { -> IMenuTypeExtension.create(::InfuserMenu) })

        fun register(eventBus: IEventBus) {
            MENUS.register(eventBus)
        }
    }
}