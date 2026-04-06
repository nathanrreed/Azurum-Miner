package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.handler.SidedEnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

public interface IEnergyBlockEntity extends ISidedBlockEntity {
    SimpleEnergyHandler getEnergyHandler();

    BlockPos getBlockPos();

    default EnergyHandler getEnergyHandler(Direction side) {
        if (side == null) {
            return getEnergyHandler();
        } else if (getSideEnergyMode(side).isBlocked()) {
            return null;
        } else {
            return new SidedEnergyHandler(getEnergyHandler(), this, side);
        }
    }
}