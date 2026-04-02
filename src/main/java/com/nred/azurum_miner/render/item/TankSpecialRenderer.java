package com.nred.azurum_miner.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.nred.azurum_miner.render.block_entity.TankBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.joml.Vector3fc;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static com.nred.azurum_miner.registration.DataComponentRegistration.SIMPLE_FLUID_COMPONENT;

public record TankSpecialRenderer(TankBlockEntityRenderer tankRenderer) implements SpecialModelRenderer<SimpleFluidContent> {
    @Nullable
    public SimpleFluidContent extractArgument(ItemStack stack) {
        return stack.get(SIMPLE_FLUID_COMPONENT);
    }

    @Override
    public void submit(SimpleFluidContent fluidContent, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        tankRenderer.submitSpecial(fluidContent, poseStack, collector, lightCoords, overlayCoords);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<SimpleFluidContent> {
        public static final MapCodec<TankSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new TankSpecialRenderer.Unbaked());

        @Override
        public MapCodec<TankSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        public TankSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new TankSpecialRenderer(new TankBlockEntityRenderer(context));
        }
    }
}