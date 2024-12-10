package com.nred.nredmod.util

import com.nred.nredmod.ModItems.ITEMS
import com.nred.nredmod.block.ModBlocks.BLOCKS
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem

class OreHelper(name: String, isGem: Boolean = false, isOre: Boolean = true) {
    companion object {
        val ORES = ArrayList<Ore>()

        operator fun ArrayList<Ore>.get(name: String): Ore {
            return ORES.filter { ore -> ore.ore_name == name }[0]
        }
    }

    init {
        ORES.add(Ore(name, isGem, isOre))
    }
}

class Ore(name: String, val isGem: Boolean, val isOre: Boolean) {
    val ore_name: String
    val ore: DeferredBlock<Block>
    val ore_tag: TagKey<Item>
    val deepslate_ore: DeferredBlock<Block>
    val block: DeferredBlock<Block>
    val block_tag: TagKey<Item>

    var ingot: DeferredItem<Item>? = null
    var gear: DeferredItem<Item>? = null
    var ingot_tag: TagKey<Item>? = null
    var nugget: DeferredItem<Item>? = null
    var nugget_tag: TagKey<Item>? = null
    var raw: DeferredItem<Item>? = null
    var raw_tag: TagKey<Item>? = null
    var raw_block: DeferredBlock<Block>? = null
    var raw_block_tag: TagKey<Item>? = null
    var gem: DeferredItem<Item>? = null
    var gem_tag: TagKey<Item>? = null

    init {
        this.ore_name = name
        this.ore = Helpers.registerBlock(name + "_ore", BLOCKS) { Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1200.0F)) }
        this.deepslate_ore = Helpers.registerBlock(name + "_deepslate_ore", BLOCKS) { Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1200.0F)) }
        this.ore_tag = ItemTags.create(Tags.Items.ORES.location.withSuffix("/" + name))
        this.block = Helpers.registerBlock(name + "_block", BLOCKS) { Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1200.0F)) }
        this.block_tag = ItemTags.create(Tags.Items.STORAGE_BLOCKS.location.withSuffix("/" + name))

        if (isOre) {
            this.ingot = ITEMS.register(name + "_ingot") { -> Item(Properties()) }
            this.gear = ITEMS.register(name + "_gear") { -> Item(Properties()) }
            this.ingot_tag = ItemTags.create(Tags.Items.INGOTS.location.withSuffix("/" + name))
            this.nugget = ITEMS.register(name + "_nugget") { -> Item(Properties()) }
            this.nugget_tag = ItemTags.create(Tags.Items.NUGGETS.location.withSuffix("/" + name))
            this.raw = ITEMS.register("raw_$name") { -> Item(Properties()) }
            this.raw_tag = ItemTags.create(Tags.Items.RAW_MATERIALS.location.withSuffix("/" + name))
            this.raw_block = Helpers.registerBlock("raw_" + name + "_block", BLOCKS) { Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1200.0F)) }
            this.raw_block_tag = ItemTags.create(Tags.Items.STORAGE_BLOCKS.location.withSuffix("/raw_" + name + "_block"))
        }

        if (isGem) {
            this.gem = ITEMS.register(name) { -> Item(Properties()) }
            this.gem_tag = ItemTags.create(Tags.Items.GEMS.location.withSuffix("/" + name))
        }
    }

    companion object {
        fun setItemTags(tag: (TagKey<Item>) -> IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item>, ore: Ore) {
            tag(Tags.Items.ORES).add(ore.ore.asItem(), ore.deepslate_ore.asItem())
            tag(Tags.Items.ORE_RATES_SINGULAR).add(ore.ore.asItem(), ore.deepslate_ore.asItem())
            tag(Tags.Items.ORES_IN_GROUND_STONE).add(ore.ore.asItem())
            tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(ore.deepslate_ore.asItem())
            tag(ore.block_tag).add(ore.block.asItem())

            if (ore.isOre) {
                tag(Tags.Items.INGOTS).add(ore.ingot!!.get())
                tag(Tags.Items.NUGGETS).add(ore.nugget!!.get())
                tag(Tags.Items.RAW_MATERIALS).add(ore.raw!!.get())
                tag(Tags.Items.STORAGE_BLOCKS).add(ore.block.asItem(), ore.raw_block!!.asItem())

                tag(ore.ore_tag).add(ore.ore.asItem(), ore.deepslate_ore.asItem())
                tag(ore.ingot_tag!!).add(ore.ingot!!.get())
                tag(ore.nugget_tag!!).add(ore.nugget!!.get())
                tag(ore.raw_tag!!).add(ore.raw!!.get())
                tag(ore.raw_block_tag!!).add(ore.raw_block!!.asItem())
            }

            if (ore.isGem) {
                tag(Tags.Items.GEMS).add(ore.gem!!.get())
                tag(ore.gem_tag!!).add(ore.gem!!.get())
            }
        }

        fun setBlockTags(tag: (TagKey<Block>) -> IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>, ore: Ore) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ore.ore.get(), ore.deepslate_ore.get(), ore.block.get(), ore.raw_block!!.get())
            tag(BlockTags.NEEDS_DIAMOND_TOOL).add(ore.ore.get(), ore.deepslate_ore.get())
            tag(BlockTags.NEEDS_IRON_TOOL).add(ore.block.get(), ore.raw_block!!.get())
        }
    }
}