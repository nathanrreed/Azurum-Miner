package com.nred.azurum_miner;

import com.mojang.logging.LogUtils;
import com.nred.azurum_miner.config.ClientConfig;
import com.nred.azurum_miner.config.ServerConfig;
import com.nred.azurum_miner.registration.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(AzurumMiner.MODID)
public class AzurumMiner {
    public static final String MODID = "azurum_miner";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AzurumMiner(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        Registries.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}