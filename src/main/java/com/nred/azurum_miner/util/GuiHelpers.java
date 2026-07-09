package com.nred.azurum_miner.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import static net.neoforged.neoforge.common.NeoForgeMod.MILK;

public class GuiHelpers {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final ModelManager modelManager = minecraft.getModelManager();
    private static final FluidStateModelSet fluidStateModelSet = modelManager.getFluidStateModelSet(); // TODO check this works

    public static void blitFluid(GuiGraphicsExtractor graphics, RenderPipeline renderPipeline, FluidResource fluidResource, long amount, long capacity, int x, int y, int width, int height) {
        if (!fluidResource.isEmpty()) {
            blitFluid(graphics, renderPipeline, fluidResource.getFluid(), amount, capacity, x, y, width, height);
        }
    }

    public static void blitFluid(GuiGraphicsExtractor graphics, RenderPipeline renderPipeline, Fluid fluid, long amount, long capacity, int x, int y, int width, int height) {
        if (!fluid.getFluidType().isAir() && amount > 0) {
            FluidState fluidState = fluid.defaultFluidState();
            FluidModel fluidModel = fluidStateModelSet.get(fluidState);
            FluidTintSource tintSource = fluidModel.fluidTintSource();
            int colour = tintSource == null ? -1 : tintSource.color(fluidState);

            blitFluid(graphics, renderPipeline, amount, capacity, x, y, width, height, fluidModel.stillMaterial().sprite(), colour);
        }
    }

    public static void blitFluid(GuiGraphicsExtractor graphics, RenderPipeline renderPipeline, long amount, long capacity, int x, int y, int width, int height, TextureAtlasSprite sprite, int colour) {
        if (amount > 0) {
            int fluidHeight = Mth.lerpInt(((float) amount / (float) capacity), 1, height);
            graphics.blitTiledSprite(renderPipeline, sprite, x, y + height - fluidHeight, width, fluidHeight, 0, 0, sprite.contents().width(), sprite.contents().height(), sprite.contents().width(), sprite.contents().height(), colour);
        }
    }

    public static void blitFluid(GuiGraphicsExtractor graphics, RenderPipeline renderPipeline, Fluid fluid, int x, int y, int width, int height, boolean allowAir) {
        if (allowAir && fluid.getFluidType().isAir()) {
            blitFluid(graphics, renderPipeline, 1000, 1000, x, y, width, height, fluidStateModelSet.get(Fluids.WATER.defaultFluidState()).stillMaterial().sprite(), 0x882BABBB);
        } else {
            blitFluid(graphics, renderPipeline, fluid, 1000, 1000, x, y, width, height);
        }
    }
}
