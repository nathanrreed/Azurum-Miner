package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner.ID
import com.nred.azurum_miner.util.FluidHelper
import com.nred.azurum_miner.util.Ore
import com.nred.azurum_miner.util.OreHelper
import com.nred.azurum_miner.util.OreHelper.Companion.get
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
        val oreTierTag = listOf(
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier1_tag")), ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier2_tag")), ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier3_tag")), ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier4_tag")), ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier5_tag"))
        )
    }

    override fun addTags(provider: HolderLookup.Provider) {
        tag(Tags.Items.BUCKETS).add(*FluidHelper.FLUIDS.map { fluid -> fluid.bucket.get() }.toTypedArray())

        for (ore in OreHelper.ORES) {
            Ore.setItemTags(::tag, ore)
        }

        tag(oreTierTag[0]).addTags(Tags.Items.ORES_COAL, Tags.Items.ORES_COPPER, OreHelper.ORES["azurum"].ore_tag)
        tag(oreTierTag[1]).addTags(Tags.Items.ORES_IRON, Tags.Items.ORES_REDSTONE, Tags.Items.ORES_LAPIS, OreHelper.ORES["galibium"].ore_tag)
        tag(oreTierTag[2]).addTags(Tags.Items.ORES_GOLD, OreHelper.ORES["thelxium"].ore_tag)
        tag(oreTierTag[3]).addTags(Tags.Items.ORES_QUARTZ, OreHelper.ORES["palestium"].ore_tag)
        tag(oreTierTag[4]).addTags(Tags.Items.ORES_DIAMOND, Tags.Items.ORES_EMERALD, Tags.Items.ORES_NETHERITE_SCRAP)
    }
}