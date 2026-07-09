package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.render.block_entity.TankBlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static com.nred.azurum_miner.registration.BlockEntityRegistration.TANK_BLOCK_ENTITY;
import static com.nred.azurum_miner.registration.EntityTypeRegistration.EMPTY_DIMENSIONAL_MATRIX_TYPE;

@EventBusSubscriber(value = Dist.CLIENT)
public class EntityRendererRegistration {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TANK_BLOCK_ENTITY.get(), TankBlockEntityRenderer::new);

        EntityRenderers.register(EMPTY_DIMENSIONAL_MATRIX_TYPE.get(), ItemEntityRenderer::new);
    }
}