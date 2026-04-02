package com.nred.azurum_miner.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.neoforged.neoforge.common.ModConfigSpec;

import static com.nred.azurum_miner.AzurumMiner.MODID;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Integer> SPEED_COLOUR = BUILDER
            .translation(MODID + ".configuration.speed_colour")
            .define("colours.speed_colour", CommonColors.RED);

    public static final ModConfigSpec.ConfigValue<Integer> ENERGY_COLOUR = BUILDER
            .translation(MODID + ".configuration.energy_colour")
            .define("colours.energy_colour", CommonColors.YELLOW);

    public static final ModConfigSpec.ConfigValue<Integer> FLUID_COLOUR = BUILDER
            .translation(MODID + ".configuration.fluid_colour")
            .define("colours.fluid_colour", 0XFF4ED5ED);

    public static final ModConfigSpec.ConfigValue<Integer> CAPACITY_COLOUR = BUILDER
            .translation(MODID + ".configuration.capacity_colour")
            .define("colours.capacity_colour", 0XFFAA4EED);

    public static final ModConfigSpec.BooleanValue USE_BUCKETS = BUILDER
            .translation(MODID + ".configuration.use_buckets")
            .define("units.use_buckets", false);


    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(Identifier.parse(itemName));
    }
}