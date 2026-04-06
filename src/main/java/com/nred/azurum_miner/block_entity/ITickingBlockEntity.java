package com.nred.azurum_miner.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ITickingBlockEntity {
    default void serverTick() {
    }

    static <T extends BlockEntity & ITickingBlockEntity> void serverTick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (level.isClientSide()) return;
        if (blockEntity instanceof IItemBlockEntity itemBlockEntity) {
            itemBlockEntity.autoOutputItemToSides(level, pos); // TODO add auto input?
        }
        if (blockEntity instanceof IFluidBlockEntity fluidBlockEntity) {
            fluidBlockEntity.autoOutputFluidToSides(level, pos); // TODO add auto input?
        }

        if (blockEntity instanceof IEnergyGeneratingBlockEntity energyBlockEntity) {
            energyBlockEntity.autoOutputEnergyToSides(level, pos);
        }

        blockEntity.serverTick();
    }
}