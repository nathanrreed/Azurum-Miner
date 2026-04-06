package com.nred.azurum_miner.menu;

import com.nred.azurum_miner.block_entity.IItemBlockEntity;
import com.nred.azurum_miner.handler.AwareItemStacksResourceHandler;
import com.nred.azurum_miner.handler.ResourceHandlerTypedSlot.ResourceHandlerInputSlot;
import com.nred.azurum_miner.handler.ResourceHandlerTypedSlot.ResourceHandlerOutputSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.IntegerRange;

import static com.nred.azurum_miner.registration.MenuRegistration.TANK_MENU;

public class BlockEntityMenu<T extends BlockEntity> extends AbstractContainerMenu {
    public T blockEntity;
    public Player player;
    private IntegerRange itemInputSlots = IntegerRange.of(0, 0);
    private IntegerRange itemOutputSlots = IntegerRange.of(0, 0);

    public BlockEntityMenu(MenuType<?> menuType, SlotLookup slotLookup, int containerId, Inventory playerInventory, FriendlyByteBuf extraData) { // Client
        this(menuType, slotLookup, containerId, playerInventory, (T) playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BlockEntityMenu(MenuType<?> menuType, SlotLookup slotLookup, int containerId, Inventory playerInventory, T blockEntity) { // Server
        super(menuType, containerId);
        this.blockEntity = blockEntity;
        this.player = playerInventory.player;

        this.addStandardInventorySlots(playerInventory, 8, 84);

        if (blockEntity instanceof IItemBlockEntity itemBlockEntity) {
            AwareItemStacksResourceHandler itemHandler = itemBlockEntity.getInternalItemHandler();

            this.itemInputSlots = itemBlockEntity.getItemInputRange();
            this.itemOutputSlots = itemBlockEntity.getItemOutputRange();

            for (int i = 0; i < itemHandler.size(); i++) {
                if (itemInputSlots.contains(i)) {
                    this.addSlot(new ResourceHandlerInputSlot(itemHandler, itemHandler::set, i, slotLookup.getItemSlot(i)));
                } else if (itemOutputSlots.contains(i)) {
                    this.addSlot(new ResourceHandlerOutputSlot(itemHandler, itemHandler::set, i, slotLookup.getItemSlot(i)));
                }
            }
        }
    }

    private static final int VANILLA_SLOT_COUNT = 36;
    private static final int VANILLA_HOTBAR_SLOT_START = 27;

    @Override
    public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
        ItemStack quickMovedStack = ItemStack.EMPTY;
        Slot quickMovedSlot = this.slots.get(quickMovedSlotIndex);

        if (quickMovedSlot.hasItem()) {
            ItemStack rawStack = quickMovedSlot.getItem();
            quickMovedStack = rawStack.copy();

            if (itemOutputSlots.contains(quickMovedSlotIndex - VANILLA_SLOT_COUNT)) {
                if (!this.moveItemStackTo(rawStack, 0, VANILLA_SLOT_COUNT, true)) {
                    return ItemStack.EMPTY;
                }

                quickMovedSlot.onQuickCraft(rawStack, quickMovedStack);
            } else if (quickMovedSlotIndex >= 0 && quickMovedSlotIndex < VANILLA_SLOT_COUNT) {
                if (!this.moveItemStackTo(rawStack, VANILLA_SLOT_COUNT + itemInputSlots.getMinimum(), VANILLA_SLOT_COUNT + itemInputSlots.getMaximum() + 1, false)) {
                    if (quickMovedSlotIndex < VANILLA_HOTBAR_SLOT_START) {
                        if (!this.moveItemStackTo(rawStack, VANILLA_HOTBAR_SLOT_START, VANILLA_SLOT_COUNT, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(rawStack, 0, VANILLA_HOTBAR_SLOT_START, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(rawStack, 0, VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }

            if (rawStack.isEmpty()) {
                quickMovedSlot.setByPlayer(ItemStack.EMPTY);
            } else {
                quickMovedSlot.setChanged();
            }

            if (rawStack.getCount() == quickMovedStack.getCount()) {
                return ItemStack.EMPTY;
            }
            quickMovedSlot.onTake(player, rawStack);
        }

        return quickMovedStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isWithinBlockInteractionRange(blockEntity.getBlockPos(), 4.0);
    }
}