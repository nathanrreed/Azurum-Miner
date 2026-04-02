package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.render.block_entity.TankBlockEntityRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static com.nred.azurum_miner.registration.BlockEntityRegistration.TANK_BLOCK_ENTITY;

@EventBusSubscriber
public class EntityRendererRegistration {
    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TANK_BLOCK_ENTITY.get(), TankBlockEntityRenderer::new); // TODO check
    }
}