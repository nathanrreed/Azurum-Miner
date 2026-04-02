package com.nred.azurum_miner.handler;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.apache.commons.lang3.IntegerRange;

public class RangedFluidStacksResourceHandler extends FluidStacksResourceHandler {
    private final IntegerRange inputRange;
    private final IntegerRange outputRange;
    private final BlockEntity blockEntity;

    public RangedFluidStacksResourceHandler(int size, int capacity, IntegerRange inputRange, IntegerRange outputRange, BlockEntity blockEntity) {
        super(size, capacity);
        this.inputRange = inputRange;
        this.outputRange = outputRange;
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int index, FluidStack previousContents) {
        super.onContentsChanged(index, previousContents);
        blockEntity.setChanged();

        if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide()) {
            blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public int insertInternal(int index, FluidResource resource, int amount, TransactionContext transaction) {
        return super.insert(index, resource, amount, transaction);
    }

    public int extractInternal(int index, FluidResource resource, int amount, TransactionContext transaction) {
        return super.extract(index, resource, amount, transaction);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (inputRange.contains(index)) {
            return super.insert(index, resource, amount, transaction);
        }
        return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (outputRange.contains(index)) {
            return super.extract(index, resource, amount, transaction);
        }
        return 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) { // TODO add or remove
        return super.isValid(index, resource);
    }
}
