package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.menu.TankMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import java.util.function.Supplier;

import static com.nred.azurum_miner.registration.Registries.MENU_TYPES;

public class MenuRegistration {
    public static final Supplier<MenuType<TankMenu>> TANK_MENU = MENU_TYPES.register("tank_menu", () -> IMenuTypeExtension.create(TankMenu::new));

    public static void register(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}