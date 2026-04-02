package com.nred.azurum_miner.datagen;

import com.nred.azurum_miner.AzurumMiner;
import com.nred.azurum_miner.util.OreMaterial;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

import static com.nred.azurum_miner.registration.BlockRegistration.ENERGIZED_OBSIDIAN;
import static com.nred.azurum_miner.registration.OreRegistration.ORE_MATERIALS;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, AzurumMiner.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        for (OreMaterial oreMaterial : ORE_MATERIALS) {
            oreMaterial.setItemTags(this::tag);
        }

//        tag(Tags.Items.BUCKETS); TODO add

        tag(Tags.Items.OBSIDIANS).add(ENERGIZED_OBSIDIAN.asItem());

        // TODO add miner tags
    }
}