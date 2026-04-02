package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.block.TankBlock;
import com.nred.azurum_miner.item.TankItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.nred.azurum_miner.registration.Registries.BLOCKS;
import static com.nred.azurum_miner.registration.Registries.ITEMS;

public class BlockRegistration {
    public static final DeferredBlock<Block> ENERGIZED_OBSIDIAN = registerBlock("energized_obsidian_block", _ -> BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).lightLevel(_ -> 15));
    public static final DeferredBlock<Block> CONGLOMERATE_OF_ORE = registerBlock("conglomerate_of_ore", p -> p.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(50.0F, 1200.0F));
    public static final DeferredBlock<Block> CONGLOMERATE_OF_ORE_BLOCK = registerBlock("conglomerate_of_ore_block", p -> p.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(5.0F, 1200.0F));

    public static final DeferredBlock<Block> TANK_BLOCK = registerBlock("tank", TankBlock::new, TankItem::new, p -> p.mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1F), p -> p.stacksTo(1));

    public static DeferredBlock<Block> registerBlock(String name, UnaryOperator<BlockBehaviour.Properties> properties) {
        DeferredBlock<Block> block = BLOCKS.registerSimpleBlock(name, properties);
        ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }

    public static <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> block_func, UnaryOperator<BlockBehaviour.Properties> properties) {
        DeferredBlock<B> block = BLOCKS.registerBlock(name, block_func, properties);
        ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }

    public static <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> block_func, BiFunction<Block, Item.Properties, ? extends BlockItem> item_func, UnaryOperator<BlockBehaviour.Properties> block_properties, UnaryOperator<Item.Properties> item_properties) {
        DeferredBlock<B> block = BLOCKS.registerBlock(name, block_func, block_properties);
        ITEMS.registerItem(name, props -> item_func.apply(block.get(), props), p -> item_properties.apply(p).useBlockDescriptionPrefix());
        return block;
    }

    public static void register(IEventBus modEventBus) {
        OreRegistration.init();
        BLOCKS.register(modEventBus);
    }
}