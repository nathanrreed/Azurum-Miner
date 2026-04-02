package com.nred.azurum_miner.datagen;

import com.nred.azurum_miner.AzurumMiner;
import com.nred.azurum_miner.util.OreMaterial;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

import static com.nred.azurum_miner.registration.BlockRegistration.*;
import static com.nred.azurum_miner.registration.OreRegistration.ORE_MATERIALS;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, AzurumMiner.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        for (OreMaterial oreMaterial : ORE_MATERIALS) {
            oreMaterial.setBlockTags(this::tag);
        }

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(CONGLOMERATE_OF_ORE.get(), CONGLOMERATE_OF_ORE_BLOCK.get(), ENERGIZED_OBSIDIAN.get())
/*              .add(INFUSER.get(), LIQUIFIER.get(), CRYSTALLIZER.get(), TRANSMOGRIFIER.get(), GENERATOR.get()); TODO
                .add(MINER_BLOCK_TIERS[0].get(), MINER_BLOCK_TIERS[1].get(), MINER_BLOCK_TIERS[2].get(), MINER_BLOCK_TIERS[3].get(), MINER_BLOCK_TIERS[4].get()) */;

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(CONGLOMERATE_OF_ORE.get(), CONGLOMERATE_OF_ORE_BLOCK.get())
/*              .add(INFUSER.get(), LIQUIFIER.get(), CRYSTALLIZER.get(), TRANSMOGRIFIER.get(), GENERATOR.get()); TODO
                .add(MINER_BLOCK_TIERS[0].get(), MINER_BLOCK_TIERS[1].get(), MINER_BLOCK_TIERS[2].get(), MINER_BLOCK_TIERS[3].get(), MINER_BLOCK_TIERS[4].get()) */;

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ENERGIZED_OBSIDIAN.get());

    }
}