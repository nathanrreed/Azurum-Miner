package com.nred.azurum_miner.util;

import com.nred.azurum_miner.AzurumMiner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.fluid.FluidTintSource;

import java.text.NumberFormat;

import static com.nred.azurum_miner.config.Config.USE_BUCKETS;

public class Helpers {
    public static Identifier azLoc(String path) {
        return Identifier.fromNamespaceAndPath(AzurumMiner.MODID, path);
    }

    public static int getColorTint(Fluid fluid, int fallback_colour) {
        Minecraft minecraft = Minecraft.getInstance();
        ModelManager modelManager = minecraft.getModelManager();
        FluidStateModelSet fluidStateModelSet = modelManager.getFluidStateModelSet();
        FluidModel fluidModel = fluidStateModelSet.get(fluid.defaultFluidState());
        FluidTintSource tintSource = fluidModel.fluidTintSource();
        return tintSource == null ? fallback_colour : tintSource.color(fluid.defaultFluidState());
    }

    public static int getColorTint(Fluid fluid) {
        return getColorTint(fluid, -1);
    }


    public static Component getFluidAmount(int amount) {
        NumberFormat numberFormater = NumberFormat.getInstance();
        if (USE_BUCKETS.get()) {
            numberFormater.setMinimumFractionDigits(1);
            return Component.literal(numberFormater.format(amount / 1000) + " B").withStyle(ChatFormatting.WHITE);
        }
        return Component.literal(numberFormater.format(amount) + " mB").withStyle(ChatFormatting.WHITE);
    }
}