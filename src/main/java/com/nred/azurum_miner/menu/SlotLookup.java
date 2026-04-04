package com.nred.azurum_miner.menu;

import static com.nred.azurum_miner.menu.SlotsInfo.SLOTS_INFO;

public enum SlotLookup {
    TANK;

    public int getItemX(int index) {
        return SLOTS_INFO.get(this).itemSlotPositons().get(index).x();
    }

    public int getItemY(int index) {
        return SLOTS_INFO.get(this).itemSlotPositons().get(index).y();
    }
}