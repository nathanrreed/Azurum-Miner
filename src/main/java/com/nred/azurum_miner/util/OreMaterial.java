package com.nred.azurum_miner.util;

import com.nred.azurum_miner.registration.BlockRegistration;
import com.nred.azurum_miner.registration.ModTags;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.codehaus.plexus.util.StringUtils;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.nred.azurum_miner.registration.Registries.ITEMS;

public class OreMaterial {
    protected static final UnaryOperator<BlockBehaviour.Properties> PROPERTIES = p -> p.requiresCorrectToolForDrops().strength(5.0F, 1200.0F);

    public final DeferredBlock<Block> ore;
    public final DeferredBlock<Block> deepslate_ore;
    public final DeferredBlock<Block> ingot_block;

    public final DeferredItem<Item> ingot;
    public final DeferredItem<Item> gear;
    public final DeferredItem<Item> nugget;

    public final TagKey<Item> ore_tag;
    public final TagKey<Item> block_tag;

    public final TagKey<Item> ingot_tag;
    public final TagKey<Item> nugget_tag;

    protected final String capitalized_name;
    public final String name;
    private final boolean found_in_ground;

    public OreMaterial(String name, boolean found_in_ground) {
        ore = BlockRegistration.registerBlock(name + "_ore", PROPERTIES);
        deepslate_ore = BlockRegistration.registerBlock(name + "_deepslate_ore", PROPERTIES);
        ingot_block = BlockRegistration.registerBlock(name + "_block", PROPERTIES);

        ingot = ITEMS.registerSimpleItem(name + "_ingot");
        gear = ITEMS.registerSimpleItem(name + "_gear");
        nugget = ITEMS.registerSimpleItem(name + "_nugget");

        ore_tag = ItemTags.create(Tags.Items.ORES.location().withSuffix("/" + name));
        block_tag = ItemTags.create(Tags.Items.STORAGE_BLOCKS.location().withSuffix("/" + name));

        ingot_tag = ItemTags.create(Tags.Items.INGOTS.location().withSuffix("/" + name));
        nugget_tag = ItemTags.create(Tags.Items.NUGGETS.location().withSuffix("/" + name));
        this.capitalized_name = StringUtils.capitaliseAllWords(name);
        this.name = name;
        this.found_in_ground = found_in_ground;
    }

    public void setItemTags(Function<TagKey<Item>, TagAppender<Item, Item>> itemTag) {
        itemTag.apply(Tags.Items.ORES).add(ore.asItem(), deepslate_ore.asItem());
        itemTag.apply(Tags.Items.ORE_RATES_SINGULAR).add(ore.asItem(), deepslate_ore.asItem());

        if (found_in_ground) {
            itemTag.apply(Tags.Items.ORES_IN_GROUND_STONE).add(ore.asItem());
            itemTag.apply(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(deepslate_ore.asItem());
        }

        itemTag.apply(Tags.Items.STORAGE_BLOCKS).add(ingot_block.asItem());
        itemTag.apply(block_tag).add(ore.asItem());
        itemTag.apply(ore_tag).add(ore.asItem(), deepslate_ore.asItem());

        itemTag.apply(Tags.Items.INGOTS).add(ingot.get());
        itemTag.apply(Tags.Items.NUGGETS).add(nugget.get());
        itemTag.apply(ingot_tag).add(ingot.get());
        itemTag.apply(nugget_tag).add(nugget.get());
    }

    public void setBlockTags(Function<TagKey<Block>, TagAppender<Block, Block>> blockTag) {
        blockTag.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(ore.get(), deepslate_ore.get(), ingot_block.get());
        blockTag.apply(BlockTags.NEEDS_DIAMOND_TOOL).add(ore.get(), deepslate_ore.get());
        blockTag.apply(BlockTags.NEEDS_IRON_TOOL).add(ingot_block.get());
    }

    public void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(ore.get());
        blockModels.createTrivialCube(deepslate_ore.get());
        blockModels.createTrivialCube(ingot_block.get());

        itemModels.generateFlatItem(ingot.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(gear.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(nugget.get(), ModelTemplates.FLAT_ITEM);
    }

    public void addTranslations(BiConsumer<Supplier<? extends Block>, String> addBlock, BiConsumer<Supplier<? extends Item>, String> addItem) {
        addBlock.accept(ore, capitalized_name + " Ore");
        addBlock.accept(deepslate_ore, "Deepslate " + capitalized_name + " Ore");
        addBlock.accept(ingot_block, "Block of " + capitalized_name);

        addItem.accept(ingot, capitalized_name + " Ingot");
        addItem.accept(gear, capitalized_name + " Gear");
        addItem.accept(nugget, capitalized_name + " Nugget");
    }

    public static class OreMaterialHasShard extends OreMaterial {
        public final DeferredItem<Item> shard;

        public final TagKey<Item> shard_tag;

        public OreMaterialHasShard(String name, boolean found_in_ground) {
            super(name, found_in_ground);

            shard = ITEMS.registerSimpleItem(name + "_shard");

            shard_tag = ItemTags.create(ModTags.Items.SHARD.location().withSuffix("/" + name));
        }

        @Override
        public void setItemTags(Function<TagKey<Item>, TagAppender<Item, Item>> itemTag) {
            super.setItemTags(itemTag);

            itemTag.apply(ModTags.Items.SHARD).add(shard.get());
            itemTag.apply(shard_tag).add(shard.get());
        }

        @Override
        public void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
            super.registerModels(blockModels, itemModels);

            itemModels.generateFlatItem(shard.get(), ModelTemplates.FLAT_ITEM);
        }

        @Override
        public void addTranslations(BiConsumer<Supplier<? extends Block>, String> addBlock, BiConsumer<Supplier<? extends Item>, String> addItem) {
            super.addTranslations(addBlock, addItem);

            addItem.accept(shard, capitalized_name + " Shard");
        }
    }

    public static class OreMaterialHasRaw extends OreMaterial {
        public final DeferredBlock<Block> raw_block;
        public final DeferredItem<Item> raw;

        public final TagKey<Item> raw_block_tag;
        public final TagKey<Item> raw_tag;


        public OreMaterialHasRaw(String name, boolean found_in_ground) {
            super(name, found_in_ground);

            raw_block = BlockRegistration.registerBlock("raw_" + name + "_block", PROPERTIES);

            raw = ITEMS.registerSimpleItem("raw_" + name);

            raw_block_tag = ItemTags.create(Tags.Items.STORAGE_BLOCKS.location().withSuffix("/raw_" + name + "_block"));
            raw_tag = ItemTags.create(Tags.Items.RAW_MATERIALS.location().withSuffix("/" + name));
        }

        @Override
        public void setItemTags(Function<TagKey<Item>, TagAppender<Item, Item>> itemTag) {
            super.setItemTags(itemTag);

            itemTag.apply(Tags.Items.RAW_MATERIALS).add(raw.get());
            itemTag.apply(Tags.Items.STORAGE_BLOCKS).add(raw_block.asItem());
            itemTag.apply(raw_tag).add(raw.get());
            itemTag.apply(raw_block_tag).add(raw_block.asItem());
        }

        @Override
        public void setBlockTags(Function<TagKey<Block>, TagAppender<Block, Block>> blockTag) {
            super.setBlockTags(blockTag);
            blockTag.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(raw_block.get());
            blockTag.apply(BlockTags.NEEDS_IRON_TOOL).add(raw_block.get());
        }

        @Override
        public void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
            super.registerModels(blockModels, itemModels);

            blockModels.createTrivialCube(raw_block.get());

            itemModels.generateFlatItem(raw.get(), ModelTemplates.FLAT_ITEM);
        }

        @Override
        public void addTranslations(BiConsumer<Supplier<? extends Block>, String> addBlock, BiConsumer<Supplier<? extends Item>, String> addItem) {
            super.addTranslations(addBlock, addItem);

            addBlock.accept(raw_block, "Block of Raw " + capitalized_name);

            addItem.accept(raw, "Raw " + capitalized_name);
        }
    }
}