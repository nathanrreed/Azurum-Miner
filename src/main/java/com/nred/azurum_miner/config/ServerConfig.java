package com.nred.azurum_miner.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

//    public static final ModConfigSpec.IntValue UPGRADE_MB_PER_TICK = BUILDER
//            .translation(MODID + ".configuration.coolant_mb_per_tick")
//            .defineInRange("upgrades.coolant_mb_per_tick", 10, 1, Integer.MAX_VALUE);


    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(Identifier.parse(itemName));
    }
}