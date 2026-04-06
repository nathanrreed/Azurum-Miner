package com.nred.azurum_miner.handler;

import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.energy.DelegatingEnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SidedEnergyHandler extends DelegatingEnergyHandler {
    private final ISidedBlockEntity blockEntity;
    private final Direction side;

    public SidedEnergyHandler(EnergyHandler delegate, ISidedBlockEntity blockEntity, Direction side) {
        super(delegate);
        this.blockEntity = blockEntity;
        this.side = side;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (!blockEntity.getSideEnergyMode(side).isBlocked()) {
            return super.insert(amount, transaction);
        }
        return 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        if (!blockEntity.getSideEnergyMode(side).isBlocked()) {
            return super.extract(amount, transaction);
        }
        return 0;
    }
}