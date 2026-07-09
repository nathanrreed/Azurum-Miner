package com.nred.azurum_miner.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

import static com.nred.azurum_miner.registration.DataMapRegistration.*;
import static com.nred.azurum_miner.registration.ItemRegistration.*;

public class ModDataMapProvider extends DataMapProvider {
    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void gather(HolderLookup.Provider provider) {
        this.builder(CARD_TYPE_DATA)
                .add(SIMPLE_UPGRADE.getId(), new CardTypeData(8), false)
                .add(UPGRADE.getId(), new CardTypeData(16), false)
                .add(ELABORATE_UPGRADE.getId(), new CardTypeData(64), false)
                .add(COMPLEX_UPGRADE.getId(), new CardTypeData(256), false)
        ;

        this.builder(COOLING_TYPE_DATA)
                .add(Fluids.EMPTY.builtInRegistryHolder(), new CoolantTypeData(48, 0), false)
                .add(Fluids.WATER.builtInRegistryHolder(), new CoolantTypeData(128, 1000), false)
                .add(Fluids.LAVA.builtInRegistryHolder(), new CoolantTypeData(16, 4.0, -0.5, 250), false)
        ;
        this.builder(MODIFIER_TYPE_DATA)
                .add(Items.REDSTONE.builtInRegistryHolder(), new ModifierTypeData(1, 3, 0.1, 0.12), false)
                .add(Items.GLOWSTONE_DUST.builtInRegistryHolder(), new ModifierTypeData(2, 4, 0.0, -0.15), false)
                .add(Items.LAPIS_LAZULI.builtInRegistryHolder(), new ModifierTypeData(4, 0, 0.2, 0.2), false)
                .add(Items.ENDER_PEARL.builtInRegistryHolder(), new ModifierTypeData(5, 2, -0.2, -0.5), false)
                .add(Items.BLAZE_POWDER.builtInRegistryHolder(), new ModifierTypeData(8, 32, 2.0, 1.5), false)
                .add(Items.DIAMOND.builtInRegistryHolder(), new ModifierTypeData(10, 12, 1.0, 1.5), false)
                .add(Items.EMERALD.builtInRegistryHolder(), new ModifierTypeData(20, 16, 1.5, 0.5), false)

        // TODO add uniques like chunk loading or silk touch for miner?

        ;
    }
}