package com.nred.azurum_miner.handler;

import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SidedFluidStacksResourceHandler extends FluidStacksResourceHandler {
    public final RangedFluidStacksResourceHandler handler;
    private final ISidedBlockEntity blockEntity;
    private final Direction side;

    public SidedFluidStacksResourceHandler(RangedFluidStacksResourceHandler handler, ISidedBlockEntity blockEntity, Direction side) {
        super(handler.size(), handler.getCapacityAsInt(0, FluidResource.EMPTY));
        this.handler = handler;
        this.blockEntity = blockEntity;
        this.side = side;
    }

    @Override
    public void set(int index, FluidResource resource, int amount) {
        handler.set(index, resource, amount);
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return handler.isValid(index, resource);
    }

    @Override
    public FluidResource getResource(int index) {
        return handler.getResource(index);
    }

    @Override
    public long getAmountAsLong(int index) {
        return handler.getAmountAsLong(index);
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return handler.getCapacityAsLong(index, resource);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (blockEntity.getSideFluidMode(side).allowInput()) {
            return handler.insert(index, resource, amount, transaction);
        }
        return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (blockEntity.getSideFluidMode(side).allowOutput()) {
            return handler.extract(index, resource, amount, transaction);
        }
        return 0;
    }
}