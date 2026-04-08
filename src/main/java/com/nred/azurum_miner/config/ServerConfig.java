package com.nred.azurum_miner.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

import static com.nred.azurum_miner.AzurumMiner.MODID;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

//    public static final ModConfigSpec.ConfigValue<Integer> FLUID_COLOUR = BUILDER
//            .translation(MODID + ".configuration.fluid_colour")
//            .define("saa.sss", 0XFF4ED5ED);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(Identifier.parse(itemName));
    }
}