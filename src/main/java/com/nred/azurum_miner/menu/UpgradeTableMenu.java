package com.nred.azurum_miner.menu;

import com.nred.azurum_miner.block_entity.UpgradeTableBlockEntity;
import com.nred.azurum_miner.handler.ResourceHandlerTypedSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.lang3.IntegerRange;

import static com.nred.azurum_miner.registration.MenuRegistration.UPDATE_TABLE_MENU;

public class UpgradeTableMenu extends BlockEntityMenu<UpgradeTableBlockEntity> {
    public UpgradeTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, (UpgradeTableBlockEntity) playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public UpgradeTableMenu(int containerId, Inventory playerInventory, UpgradeTableBlockEntity blockEntity) { // Server
        super(UPDATE_TABLE_MENU.get(), SlotLookup.UPGRADE_TABLE, containerId, playerInventory, blockEntity);

        itemInputSlots = IntegerRange.of(0, 1);
        itemOutputSlots = IntegerRange.of(1, 2);
        this.addSlot(new ResourceHandlerTypedSlot.ResourceHandlerOutputSlot(blockEntity.itemHandler, blockEntity.itemHandler::set, OUTPUT_SLOT, SlotLookup.UPGRADE_TABLE.getItemSlot(OUTPUT_SLOT)));
    }

    public static final int OUTPUT_SLOT = 0;
}