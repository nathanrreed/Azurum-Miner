package com.nred.azurum_miner.util;

import com.nred.azurum_miner.AzurumMiner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.fluid.FluidTintSource;

import java.text.NumberFormat;

import static com.nred.azurum_miner.config.ClientConfig.USE_BUCKETS;

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

    public static NumberFormat numberFormater = NumberFormat.getInstance();
    public static NumberFormat decimalFormater = makeDecimalFormater();

    private static NumberFormat makeDecimalFormater() {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(1);
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat;
    }

    public static Component getFluidAmount(int amount) {
        if (USE_BUCKETS.get()) {

            return Component.literal(decimalFormater.format(amount / 1000) + " B").withStyle(ChatFormatting.WHITE);
        }
        return Component.literal(numberFormater.format(amount) + " mB").withStyle(ChatFormatting.WHITE);
    }

    public static Component getEnergyAmount(double amount) {
        String string;
        if ((amount / 1000000000.0) >= 0.999999) {
            string = String.format("%.1f GFE", amount / 1000000000.0);
        } else if ((amount / 1000000.0) >= 0.999999) {
            string = String.format("%.1f MFE", amount / 1000000.0);
        } else if ((amount / 1000.0) >= 0.999999) {
            string = String.format("%.1f kFE", amount / 1000.0);
        } else {
            string = String.format("%.1f FE", amount);
        }

        return Component.literal(string).withStyle(ChatFormatting.WHITE);
    }

    public static String getTime(double ticks) {
        if (ticks <= 0) {
            return "0s";
        }

        double time = Math.max(ticks / 20.0, 0.0);
        double hours = time / 3600.0;
        time %= 3600.0;
        double mins = time / 60.0;
        time %= 60.0;

        var str = "";
        if (hours >= 1) {
            str += String.format("%.1fh ", hours);
        }
        if (mins >= 1) {
            str += String.format("%.1fm ", mins);
        }
        if (time >= 0) {
            str += String.format("%.1fs", time);
        }

        return str.trim();
    }

    public static Direction getRelative(Direction facing, Direction direction) {
        return switch (direction) {
            case UP, DOWN -> direction;
            case NORTH -> facing;
            case EAST -> facing.getCounterClockWise();
            case SOUTH -> facing.getOpposite();
            case WEST -> facing.getClockWise();
        };
    }
}