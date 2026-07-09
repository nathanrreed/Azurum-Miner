package com.nred.azurum_miner.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import oshi.util.tuples.Pair;

import java.util.Comparator;
import java.util.List;

public class DataMapUtil {
    public static <T> List<Pair<Item, T>> itemDataMap(DataMapType<Item, T> dataMapType) {
        return itemDataMap(dataMapType, Comparator.comparing(Object::hashCode));
    }

    public static <T> List<Pair<Item, T>> itemDataMap(DataMapType<Item, T> dataMapType, Comparator<Pair<Item, T>> comparator) {
        return BuiltInRegistries.ITEM.getDataMap(dataMapType).entrySet().stream().map(e -> new Pair<>(BuiltInRegistries.ITEM.getValue(e.getKey()), e.getValue())).sorted(comparator).toList();
    }

    public static <T> List<Pair<Fluid, T>> fluidDataMap(DataMapType<Fluid, T> dataMapType) {
        return fluidDataMap(dataMapType, Comparator.comparing(Object::hashCode));
    }

    public static <T> List<Pair<Fluid, T>> fluidDataMap(DataMapType<Fluid, T> dataMapType, Comparator<Pair<Fluid, T>> comparator) {
        return BuiltInRegistries.FLUID.getDataMap(dataMapType).entrySet().stream().map(e -> new Pair<>(BuiltInRegistries.FLUID.getValue(e.getKey()), e.getValue())).sorted(comparator).toList();
    }

    public static List<Item> dataMapItems(DataMapType<Item, ?> dataMapType) {
        return BuiltInRegistries.ITEM.getDataMap(dataMapType).keySet().stream().map(BuiltInRegistries.ITEM::getValue).toList();
    }

    public static <T> T dataMapItem(DataMapType<Item, T> dataMapType, Item item) {
        return BuiltInRegistries.ITEM.getDataMap(dataMapType).get(item.builtInRegistryHolder().getKey());
    }

    public static List<Fluid> dataMapFluids(DataMapType<Fluid, ?> dataMapType) {
        return BuiltInRegistries.FLUID.getDataMap(dataMapType).keySet().stream().map(BuiltInRegistries.FLUID::getValue).toList();
    }

    public static <T> T dataMapFluid(DataMapType<Fluid, T> dataMapType, Fluid fluid) {
        return BuiltInRegistries.FLUID.getDataMap(dataMapType).get(fluid.builtInRegistryHolder().getKey());
    }
}