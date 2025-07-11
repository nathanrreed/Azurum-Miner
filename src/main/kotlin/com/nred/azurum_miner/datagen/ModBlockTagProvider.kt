package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.block.ModBlocks
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.util.Ore
import com.nred.azurum_miner.util.OreHelper
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModBlockTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper?,
) : BlockTagsProvider(output, lookupProvider, AzurumMiner.ID, existingFileHelper) {

    companion object {
        val INCORRECT_FOR_PALESTIUM_TOOL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "incorrect_for_palestium_tool"))
    }

    override fun addTags(provider: HolderLookup.Provider) {
        for (ore in OreHelper.ORES) {
            Ore.setBlockTags(::tag, ore)
        }

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModMachines.MINER_BLOCK_TIERS[0].get(), ModMachines.MINER_BLOCK_TIERS[1].get(), ModMachines.MINER_BLOCK_TIERS[2].get(), ModMachines.MINER_BLOCK_TIERS[3].get(), ModMachines.MINER_BLOCK_TIERS[4].get())
            .add(ModBlocks.CONGLOMERATE_OF_ORE.get(), ModBlocks.CONGLOMERATE_OF_ORE_BLOCK.get(), ModBlocks.ENERGIZED_OBSIDIAN.get(), ModMachines.INFUSER.get(), ModMachines.LIQUIFIER.get(), ModMachines.CRYSTALLIZER.get(), ModMachines.TRANSMOGRIFIER.get(), ModMachines.GENERATOR.get())

        tag(BlockTags.NEEDS_IRON_TOOL)
            .add(ModMachines.MINER_BLOCK_TIERS[0].get(), ModMachines.MINER_BLOCK_TIERS[1].get(), ModMachines.MINER_BLOCK_TIERS[2].get(), ModMachines.MINER_BLOCK_TIERS[3].get(), ModMachines.MINER_BLOCK_TIERS[4].get())
            .add(ModBlocks.CONGLOMERATE_OF_ORE.get(), ModBlocks.CONGLOMERATE_OF_ORE_BLOCK.get(), ModMachines.INFUSER.get(), ModMachines.LIQUIFIER.get(), ModMachines.CRYSTALLIZER.get(), ModMachines.TRANSMOGRIFIER.get(), ModMachines.GENERATOR.get())

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .add(ModBlocks.ENERGIZED_OBSIDIAN.get())
    }
}