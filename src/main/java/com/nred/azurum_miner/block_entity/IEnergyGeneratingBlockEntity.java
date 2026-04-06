package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;

public interface IEnergyGeneratingBlockEntity extends IEnergyBlockEntity {
    default void autoOutputEnergyToSides(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (getSideEnergyMode(direction) == ResourceHandlerSideMode.ENERGY_UNBLOCKED) {
                EnergyHandler external = level.getCapability(Capabilities.Energy.BLOCK, pos.relative(direction), direction.getOpposite());
                EnergyHandler internal = getEnergyHandler(direction);
                if (internal != null && external != null) {
                    EnergyHandlerUtil.move(internal, external, 10000, null); // TODO customize amount?
                }
            }
        }
    }
}