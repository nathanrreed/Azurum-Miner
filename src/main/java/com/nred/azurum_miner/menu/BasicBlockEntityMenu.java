package com.nred.azurum_miner.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BasicBlockEntityMenu<T extends BlockEntity> extends AbstractContainerMenu {
    public T blockEntity;
    public Player player;

    public BasicBlockEntityMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, T blockEntity) { // Server
        super(menuType, containerId);
        this.blockEntity = blockEntity;
        this.player = playerInventory.player;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isWithinBlockInteractionRange(blockEntity.getBlockPos(), 4.0);
    }
}