package com.nred.azurum_miner.handler;

import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SidedItemStacksResourceHandler extends ItemStacksResourceHandler {
    public final RangedItemStacksResourceHandler handler;
    private final ISidedBlockEntity blockEntity;
    private final Direction side;

    public SidedItemStacksResourceHandler(RangedItemStacksResourceHandler handler, ISidedBlockEntity blockEntity, Direction side) {
        super(handler.size());
        this.handler = handler;
        this.blockEntity = blockEntity;
        this.side = side;
    }

    @Override
    public void set(int index, ItemResource resource, int amount) {
        handler.set(index, resource, amount);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return handler.isValid(index, resource);
    }

    @Override
    public ItemResource getResource(int index) {
        return handler.getResource(index);
    }

    @Override
    public long getAmountAsLong(int index) {
        return handler.getAmountAsLong(index);
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return handler.getCapacityAsLong(index, resource);
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (blockEntity.getSideItemMode(side).allowInput()) {
            return handler.insert(index, resource, amount, transaction);
        }
        return 0;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (blockEntity.getSideItemMode(side).allowOutput()) {
            return handler.extract(index, resource, amount, transaction);
        }
        return 0;
    }
}