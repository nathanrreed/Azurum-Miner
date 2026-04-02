package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.screen.TankScreen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import static com.nred.azurum_miner.registration.MenuRegistration.TANK_MENU;

@EventBusSubscriber
public class ScreenRegistration {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(TANK_MENU.get(), TankScreen::new);
    }
}