package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.screen.TankScreen;
import com.nred.azurum_miner.screen.UpgradeSchematicTableScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import static com.nred.azurum_miner.registration.MenuRegistration.TANK_MENU;
import static com.nred.azurum_miner.registration.MenuRegistration.UPDATE_TABLE_MENU;

@EventBusSubscriber(value = Dist.CLIENT)
public class ScreenRegistration {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(TANK_MENU.get(), TankScreen::new);
        event.register(UPDATE_TABLE_MENU.get(), UpgradeSchematicTableScreen::new);
    }
}