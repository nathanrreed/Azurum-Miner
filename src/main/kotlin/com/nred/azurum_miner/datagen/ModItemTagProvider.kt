package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner.ID
import com.nred.azurum_miner.block.ModBlocks
import com.nred.azurum_miner.util.FluidHelper
import com.nred.azurum_miner.util.Ore
import com.nred.azurum_miner.util.OreHelper
import com.nred.azurum_miner.util.OreHelper.Companion.get
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items.*
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.Tags.Items.*
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModItemTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    blockTags: CompletableFuture<TagLookup<Block>>,
    existingFileHelper: ExistingFileHelper,
) : ItemTagsProvider(output, lookupProvider, blockTags, ID, existingFileHelper) {
    companion object {
        val oreTierTag = listOf(
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier1_tag")), ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier2_tag")), ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier3_tag")), ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier4_tag")), ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_ore_tier5_tag"))
        )
        val materialTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ID, "miner_material_tag"))
    }

    override fun addTags(provider: HolderLookup.Provider) {
        tag(BUCKETS).add(*FluidHelper.FLUIDS.map { fluid -> fluid.bucket.get() }.toTypedArray())

        for (ore in OreHelper.ORES) {
            Ore.setItemTags(::tag, ore)
        }

        fun addOptionalOres(vararg locations: String): Array<TagKey<Item>> {
            val list = ArrayList<TagKey<Item>>()
            for (name in locations) {
                list += ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ores/$name"))
            }
            return list.toTypedArray()
        }

        tag(OBSIDIANS).add(ModBlocks.ENERGIZED_OBSIDIAN.asItem())

        tag(oreTierTag[0]).addTags(OreHelper.ORES["azurum"].ore_tag, ORES_COAL, ORES_COPPER).addOptionalTags(*addOptionalOres("tin", "bauxite", "aluminium", "lignite_coal"))
        tag(oreTierTag[1]).addTags(OreHelper.ORES["galibium"].ore_tag, ORES_IRON, ORES_REDSTONE, ORES_LAPIS).addOptionalTags(*addOptionalOres("salt", "nickel", "osmium", "lead"))
        tag(oreTierTag[2]).addTags(OreHelper.ORES["thelxium"].ore_tag, ORES_GOLD).addOptionalTags(*addOptionalOres("fluorite", "silver", "black_quartz"))
        tag(oreTierTag[3]).addTags(OreHelper.ORES["palestium"].ore_tag, ORES_QUARTZ).addOptionalTags(*addOptionalOres("uranium", "yellorite", "tungsten", "antimony", "montazite", "titanium"))
        tag(oreTierTag[4]).addTags(ORES_DIAMOND, ORES_EMERALD, ORES_NETHERITE_SCRAP).addOptionalTags(*addOptionalOres("mithril", "iridium", "platinum", "anglesite", "benitoite"))

        tag(materialTag).addTags(STONES, COBBLESTONES, COBBLESTONES_DEEPSLATE, GRAVELS, OBSIDIANS_NORMAL, SANDS).add(DIRT, GRASS_BLOCK, COARSE_DIRT, ROOTED_DIRT, BASALT, SMOOTH_BASALT, BLACKSTONE, CALCITE, CLAY, MUD, MUDDY_MANGROVE_ROOTS, PACKED_MUD)
    }

}


//private fun IntrinsicTagAppender.addOptionalTag(location: ResourceLocation): TagAppender<Item> {
//    this.builder.addOptionalTag(location)
//}

//
//fun <T : Item> IntrinsicTagAppender.addOptionalTag(location: ResourceLocation): TagAppender<T> {
//    builder.addOptionalTag(location)
//    return this
//}

