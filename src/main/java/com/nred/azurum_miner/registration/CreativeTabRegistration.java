package com.nred.azurum_miner.registration;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Arrays;
import java.util.Comparator;

import static com.nred.azurum_miner.registration.Registries.CREATIVE_MODE_TABS;
import static com.nred.azurum_miner.registration.Registries.ITEMS;

public class CreativeTabRegistration {
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AZURUM_MINER_TAB = CREATIVE_MODE_TABS.register("azurum_miner_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.azurum_miner"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
//            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance()) TODO
            .displayItems((parameters, output) -> {
//                acceptAll(output, SIMPLE_VOID_PROCESSOR, VOID_PROCESSOR, ELABORATE_VOID_PROCESSOR, COMPLEX_VOID_PROCESSOR, CONGLOMERATE_OF_ORE_SHARD, NETHER_DIAMOND, ENDER_DIAMOND, ENERGIZED_SHARD, VOID_SHARD, EMPTY_DIMENSIONAL_MATRIX, DIMENSIONAL_MATRIX, SEED_CRYSTAL);
//                acceptAll(output, BlockRegistration.CONGLOMERATE_OF_ORE, BlockRegistration.CONGLOMERATE_OF_ORE_BLOCK, BlockRegistration.ENERGIZED_OBSIDIAN);
                acceptAll(output, ITEMS.getEntries().toArray(ItemLike[]::new)); // TODO
            }).build());

    private static void acceptAll(CreativeModeTab.Output output, ItemLike... items) {
        Arrays.stream(items).forEach(output::accept);
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}