package com.nred.azurum_miner.handler;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class AwareItemStacksResourceHandler extends ItemStacksResourceHandler {
    private final BlockEntity blockEntity;

    public AwareItemStacksResourceHandler(int size, BlockEntity blockEntity) {
        super(size);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents) {
        super.onContentsChanged(index, previousContents);
        blockEntity.setChanged();
    }
}