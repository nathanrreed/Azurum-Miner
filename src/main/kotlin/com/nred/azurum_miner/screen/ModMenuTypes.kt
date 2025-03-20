package com.nred.azurum_miner.screen

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.generator.GeneratorMenu
import com.nred.azurum_miner.machine.infuser.InfuserMenu
import com.nred.azurum_miner.machine.liquifier.LiquifierMenu
import com.nred.azurum_miner.machine.miner.MinerMenu
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorMenu
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierMenu
import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.MenuType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister

class ModMenuTypes {
    companion object {
        val MENUS: DeferredRegister<MenuType<*>?> = DeferredRegister.create(Registries.MENU, AzurumMiner.ID)

        val MINER_MENU = MENUS.register("miner_menu", { -> IMenuTypeExtension.create(::MinerMenu) })
        val LIQUIFIER_MENU = MENUS.register("liquifier_menu", { -> IMenuTypeExtension.create(::LiquifierMenu) })
        val INFUSER_MENU = MENUS.register("infuser_menu", { -> IMenuTypeExtension.create(::InfuserMenu) })
        val TRANSMOGRIFIER_MENU = MENUS.register("transmogrifier_menu", { -> IMenuTypeExtension.create(::TransmogrifierMenu) })
        val GENERATOR_MENU = MENUS.register("generator_menu", { -> IMenuTypeExtension.create(::GeneratorMenu) })
        val SIMPLE_GENERATOR_MENU = MENUS.register("simple_generator_menu", { -> IMenuTypeExtension.create(::SimpleGeneratorMenu) })

        fun register(eventBus: IEventBus) {
            MENUS.register(eventBus)
        }
    }
}