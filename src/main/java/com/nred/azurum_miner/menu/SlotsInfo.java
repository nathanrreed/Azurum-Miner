package com.nred.azurum_miner.menu;

import net.minecraft.client.gui.navigation.ScreenPosition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SlotsInfo(List<ScreenPosition> itemSlotPositons) {
    public static final Map<SlotLookup, SlotsInfo> SLOTS_INFO = createSlotInfo();

    private static Map<SlotLookup, SlotsInfo> createSlotInfo() {
        Map<SlotLookup, SlotsInfo> map = new HashMap<>();
        map.put(SlotLookup.TANK, new SlotsInfo(List.of(new ScreenPosition(50, 50), new ScreenPosition(100, 50))));
        return map;
    }
}