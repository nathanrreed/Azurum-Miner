package com.nred.azurum_miner;

import com.nred.azurum_miner.config.ModConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = AzurumMiner.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AzurumMiner.MODID, value = Dist.CLIENT)
public class ModClient {
    public ModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (_, parent) -> new ConfigurationScreen(container, parent, ModConfigScreen::new));
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    }
}