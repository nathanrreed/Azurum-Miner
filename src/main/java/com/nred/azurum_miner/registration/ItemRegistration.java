package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.item.PipetteItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.nred.azurum_miner.registration.Registries.ITEMS;

public class ItemRegistration {
    public static final DeferredItem<Item> SIMPLE_VOID_PROCESSOR = ITEMS.registerSimpleItem("simple_void_processor");
    public static final DeferredItem<Item> VOID_PROCESSOR = ITEMS.registerSimpleItem("void_processor");
    public static final DeferredItem<Item> ELABORATE_VOID_PROCESSOR = ITEMS.registerSimpleItem("elaborate_void_processor");
    public static final DeferredItem<Item> COMPLEX_VOID_PROCESSOR = ITEMS.registerSimpleItem("complex_void_processor");

    public static final DeferredItem<Item> CONGLOMERATE_OF_ORE_SHARD = ITEMS.registerSimpleItem("conglomerate_of_ore_shard");
    public static final DeferredItem<Item> NETHER_DIAMOND = ITEMS.registerSimpleItem("nether_diamond");
    public static final DeferredItem<Item> ENDER_DIAMOND = ITEMS.registerSimpleItem("ender_diamond");
    public static final DeferredItem<Item> ENERGIZED_SHARD = ITEMS.registerSimpleItem("energy_shard");
    public static final DeferredItem<Item> VOID_SHARD = ITEMS.registerSimpleItem("void_crystal");

    public static final DeferredItem<Item> DIMENSIONAL_MATRIX = ITEMS.registerSimpleItem("dimensional_matrix", p -> p.durability(1).setNoCombineRepair());
    public static final DeferredItem<Item> EMPTY_DIMENSIONAL_MATRIX = ITEMS.registerSimpleItem("empty_dimensional_matrix");
    public static final DeferredItem<Item> SEED_CRYSTAL = ITEMS.registerSimpleItem("seed_crystal");


    public static final DeferredItem<Item> PIPETTE = ITEMS.registerItem("pipette", PipetteItem::new, p -> p.stacksTo(1));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}