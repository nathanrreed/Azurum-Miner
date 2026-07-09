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

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        for (OreMaterial oreMaterial : ORE_MATERIALS) {
            oreMaterial.setBlockTags(this::tag);
        }

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(CONGLOMERATE_OF_ORE.getKey(), CONGLOMERATE_OF_ORE_BLOCK.getKey(), ENERGIZED_OBSIDIAN.getKey(), UPGRADE_TABLE_BLOCK.getKey())
/*              .add(INFUSER.getKey(), LIQUIFIER.getKey(), CRYSTALLIZER.getKey(), TRANSMOGRIFIER.getKey(), GENERATOR.getKey()); TODO
                .add(MINER_BLOCK_TIERS[0].getKey(), MINER_BLOCK_TIERS[1].getKey(), MINER_BLOCK_TIERS[2].getKey(), MINER_BLOCK_TIERS[3].getKey(), MINER_BLOCK_TIERS[4].getKey()) */;

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(CONGLOMERATE_OF_ORE.getKey(), CONGLOMERATE_OF_ORE_BLOCK.getKey())
/*              .add(INFUSER.getKey(), LIQUIFIER.getKey(), CRYSTALLIZER.getKey(), TRANSMOGRIFIER.getKey(), GENERATOR.getKey()); TODO
                .add(MINER_BLOCK_TIERS[0].getKey(), MINER_BLOCK_TIERS[1].getKey(), MINER_BLOCK_TIERS[2].getKey(), MINER_BLOCK_TIERS[3].getKey(), MINER_BLOCK_TIERS[4].getKey()) */;

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ENERGIZED_OBSIDIAN.getKey());

    }
}