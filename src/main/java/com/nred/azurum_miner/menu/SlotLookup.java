package com.nred.azurum_miner.menu;

import com.nred.azurum_miner.menu.SlotsInfo.ItemSlotInfo;
import net.minecraft.client.gui.navigation.ScreenRectangle;

import static com.nred.azurum_miner.menu.SlotsInfo.SLOTS_INFO;

public enum SlotLookup {
    TANK, UPGRADE_TABLE;

    public ItemSlotInfo getItemSlot(int index) {
        return SLOTS_INFO.get(this).itemSlots().get(index);
    }

    public ScreenRectangle getFluidRect(int index) {
        return SLOTS_INFO.get(this).fluidSlots().get(index);
    }

    public int getFluidSize() {
        return SLOTS_INFO.get(this).fluidSlots().size();
    }

    public int invX(){
        return SLOTS_INFO.get(this).inventoryPos().x();
    }
    public int invY(){
        return SLOTS_INFO.get(this).inventoryPos().y();
    }
}