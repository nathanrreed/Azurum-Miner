package com.nred.azurum_miner.config;

import net.minecraft.util.CommonColors;
import net.neoforged.neoforge.common.ModConfigSpec;

import static com.nred.azurum_miner.AzurumMiner.MODID;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Tooltip Colours
    public static final ModConfigSpec.ConfigValue<Integer> SPEED_COLOUR = BUILDER
            .translation(MODID + ".configuration.speed_colour")
            .define("colours.speed_colour", CommonColors.RED);

    public static final ModConfigSpec.ConfigValue<Integer> ENERGY_COLOUR = BUILDER
            .translation(MODID + ".configuration.energy_colour")
            .define("colours.energy_colour", CommonColors.YELLOW);

    public static final ModConfigSpec.ConfigValue<Integer> COMPLEXITY_COLOUR = BUILDER
            .translation(MODID + ".configuration.complexity_colour")
            .define("colours.complexity_colour", CommonColors.COSMOS_PINK);

    public static final ModConfigSpec.ConfigValue<Integer> HEAT_COLOUR = BUILDER
            .translation(MODID + ".configuration.heat_colour")
            .define("colours.heat_colour", CommonColors.SOFT_RED);

    public static final ModConfigSpec.ConfigValue<Integer> FLUID_COLOUR = BUILDER
            .translation(MODID + ".configuration.fluid_colour")
            .define("colours.fluid_colour", 0XFF4ED5ED);

    public static final ModConfigSpec.ConfigValue<Integer> CAPACITY_COLOUR = BUILDER
            .translation(MODID + ".configuration.capacity_colour")
            .define("colours.capacity_colour", 0XFFAA4EED);

    // TODO add colours

    public static final ModConfigSpec.BooleanValue USE_BUCKETS = BUILDER
            .translation(MODID + ".configuration.use_buckets")
            .define("units.use_buckets", false);

    public static final ModConfigSpec.BooleanValue USE_PERCENTAGE = BUILDER
            .translation(MODID + ".configuration.use_percentage")
            .define("units.use_percentage", false);


    public static final ModConfigSpec.BooleanValue USE_RIGHT_SIDE = BUILDER
            .translation(MODID + ".configuration.use_right_side")
            .define("side_bar.use_right_side", false);

    public static final ModConfigSpec.BooleanValue ALLOW_MULTIPLE_OPEN = BUILDER
            .translation(MODID + ".configuration.allow_multiple_open")
            .define("side_bar.allow_multiple_open", false);

    public static final ModConfigSpec.BooleanValue SHOW_TRANSFER_RATES = BUILDER
            .translation(MODID + ".configuration.show_transfer_rates")
            .define("side_bar.show_transfer_rates", false);

    public static final ModConfigSpec SPEC = BUILDER.build();
}