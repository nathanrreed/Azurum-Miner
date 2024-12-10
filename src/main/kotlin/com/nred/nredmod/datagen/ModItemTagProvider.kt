package com.nred.nredmod.datagen

import com.nred.nredmod.NredMod.ID
import com.nred.nredmod.util.FluidHelper
import com.nred.nredmod.util.Ore
import com.nred.nredmod.util.OreHelper
import com.nred.nredmod.util.OreHelper.Companion.get
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModItemTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    blockTags: CompletableFuture<TagLookup<Block>>,
    existingFileHelper: ExistingFileHelper?
) : ItemTagsProvider(output, lookupProvider, blockTags, ID, existingFileHelper) {
    companion object {
        val oreTier1Tag = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier1_tag"))
        val oreTier2Tag = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier2_tag"))
        val oreTier3Tag = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier3_tag"))
        val oreTier4Tag = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier4_tag"))
        val oreTier5Tag = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier5_tag"))
    }

    override fun addTags(provider: HolderLookup.Provider) {
        tag(Tags.Items.BUCKETS).add(*FluidHelper.FLUIDS.map { fluid -> fluid.bucket.get() }.toTypedArray())

        for (ore in OreHelper.ORES) {
            Ore.setItemTags(::tag, ore)
        }

        tag(oreTier1Tag).addTags(Tags.Items.ORES_COAL, Tags.Items.ORES_COPPER, OreHelper.ORES["azurum"].ore_tag)
        tag(oreTier2Tag).addTags(Tags.Items.ORES_IRON, Tags.Items.ORES_REDSTONE, Tags.Items.ORES_LAPIS, OreHelper.ORES["galibium"].ore_tag)
        tag(oreTier3Tag).addTags(Tags.Items.ORES_GOLD, OreHelper.ORES["thelxium"].ore_tag)
        tag(oreTier4Tag).addTags(Tags.Items.ORES_QUARTZ, OreHelper.ORES["palestium"].ore_tag)
        tag(oreTier5Tag).addTags(Tags.Items.ORES_DIAMOND, Tags.Items.ORES_EMERALD, Tags.Items.ORES_NETHERITE_SCRAP)
    }
}