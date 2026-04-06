package com.nred.azurum_miner.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class GuiHelpers {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final ModelManager modelManager = minecraft.getModelManager();
    private static final FluidStateModelSet fluidStateModelSet = modelManager.getFluidStateModelSet(); // TODO check this works

    public static void blitFluid(GuiGraphicsExtractor graphics, RenderPipeline renderPipeline, FluidResource fluidResource, long amount, long capacity, int x, int y, int width, int height) {
        if (!fluidResource.isEmpty() && amount > 0) {
            FluidState fluidState = fluidResource.getFluid().defaultFluidState();
            FluidModel fluidModel = fluidStateModelSet.get(fluidState);
            FluidTintSource tintSource = fluidModel.fluidTintSource();
            int colour = tintSource == null ? -1 : tintSource.color(fluidState);
            int fluidHeight = Mth.lerpInt(((float) amount / (float) capacity), 1, height);

            TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();
            graphics.blitTiledSprite(renderPipeline, sprite, x, y + height - fluidHeight, width, fluidHeight, 0, 0, sprite.contents().width(), sprite.contents().height(), sprite.contents().width(), sprite.contents().height(), colour);
        }
    }
}
