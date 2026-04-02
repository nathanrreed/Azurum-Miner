package com.nred.azurum_miner.handler;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.apache.commons.lang3.IntegerRange;

public class RangedItemStacksResourceHandler extends ItemStacksResourceHandler {
    private final IntegerRange inputRange;
    private final IntegerRange outputRange;
    private final BlockEntity blockEntity;

    public RangedItemStacksResourceHandler(int size, IntegerRange inputRange, IntegerRange outputRange, BlockEntity blockEntity) {
        super(size);
        this.inputRange = inputRange;
        this.outputRange = outputRange;
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents) {
        super.onContentsChanged(index, previousContents);
        blockEntity.setChanged();

        if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide()) {
            blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public int insertInternal(int index, ItemResource resource, int amount, TransactionContext transaction) {
        return super.insert(index, resource, amount, transaction);
    }

    public int extractInternal(int index, ItemResource resource, int amount, TransactionContext transaction) {
        return super.extract(index, resource, amount, transaction);
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (inputRange.contains(index)) {
            return super.insert(index, resource, amount, transaction);
        }
        return 0;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (outputRange.contains(index)) {
            return super.extract(index, resource, amount, transaction);
        }
        return 0;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) { // TODO add or remove
        return super.isValid(index, resource);
    }
}