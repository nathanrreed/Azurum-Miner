package com.nred.azurum_miner.handler;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public class AwareFluidStacksResourceHandler extends FluidStacksResourceHandler {
    private final BlockEntity blockEntity;

    public AwareFluidStacksResourceHandler(int size, int capacity, BlockEntity blockEntity) {
        super(size, capacity);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int index, FluidStack previousContents) {
        super.onContentsChanged(index, previousContents);
        blockEntity.setChanged();
    }
}
