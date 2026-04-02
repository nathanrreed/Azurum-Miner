package com.nred.azurum_miner.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ITickingBlockEntity {
    static <T extends BlockEntity & ITickingBlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (blockEntity instanceof IFluidBlockEntity fluidBlockEntity) {
            fluidBlockEntity.pushFluidToSides(level, pos);
        }
    }
}