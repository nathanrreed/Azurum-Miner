package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.render.item.TankSpecialRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

import static com.nred.azurum_miner.util.Helpers.azLoc;

@EventBusSubscriber(value = Dist.CLIENT)
public class SpecialRendererRegistration {
    @SubscribeEvent
    public static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(azLoc("tank_special"), TankSpecialRenderer.Unbaked.MAP_CODEC);
    }
}