package com.nred.azurum_miner.menu;

import com.nred.azurum_miner.block_entity.TankBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

import static com.nred.azurum_miner.registration.MenuRegistration.TANK_MENU;

public class TankMenu extends BlockEntityMenu<TankBlockEntity> { // TODO is this needed?
    public TankMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(TANK_MENU.get(), SlotLookup.TANK, containerId, playerInventory, extraData);
    }

    public TankMenu(int containerId, Inventory playerInventory, TankBlockEntity blockEntity) {
        super(TANK_MENU.get(), SlotLookup.TANK, containerId, playerInventory, blockEntity);
    }
}