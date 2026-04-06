package com.nred.azurum_miner.handler;

import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import com.nred.azurum_miner.widget.side_mode.SideModeType;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SidedDelegatingResourceHandler<T extends Resource> extends DelegatingResourceHandler<T> {
    private final ISidedBlockEntity blockEntity;
    private final Direction side;
    private final SideModeType sideModeType;

    public SidedDelegatingResourceHandler(ResourceHandler<T> delegate, ISidedBlockEntity blockEntity, Direction side, SideModeType sideModeType) {
        super(delegate);
        this.blockEntity = blockEntity;
        this.side = side;
        this.sideModeType = sideModeType;
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        if (blockEntity.getSideMode(side, sideModeType).allowInput()) {
            return super.insert(index, resource, amount, transaction);
        }
        return 0;
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        if (blockEntity.getSideMode(side, sideModeType).allowOutput()) {
            return super.extract(index, resource, amount, transaction);
        }
        return 0;
    }
}