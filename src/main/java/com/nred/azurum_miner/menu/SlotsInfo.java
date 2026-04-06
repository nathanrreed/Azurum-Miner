package com.nred.azurum_miner.menu;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nred.azurum_miner.menu.SlotsInfo.ItemSlotInfo.iSlot;
import static com.nred.azurum_miner.util.Helpers.azLoc;

public record SlotsInfo(List<ItemSlotInfo> itemSlots, List<ScreenRectangle> fluidSlots) {
    public static final Map<SlotLookup, SlotsInfo> SLOTS_INFO = createSlotInfo();

    private static Map<SlotLookup, SlotsInfo> createSlotInfo() {
        Map<SlotLookup, SlotsInfo> map = new HashMap<>();
        map.put(SlotLookup.TANK, new SlotsInfo(
                List.of(iSlot(18, 17, azLoc("widget/tank/full_bucket")), iSlot(142, 17, azLoc("widget/tank/empty_bucket")), iSlot(18, 53), iSlot(142, 53)),
                List.of(new ScreenRectangle(63, 16, 50, 54))));
        return map;
    }

    public record ItemSlotInfo(int x, int y, Identifier background) {
        public static ItemSlotInfo iSlot(int x, int y) {
            return new ItemSlotInfo(x, y, null);
        }

        public static ItemSlotInfo iSlot(int x, int y, Identifier background) {
            return new ItemSlotInfo(x, y, background);
        }
    }
}