package com.nred.azurum_miner.block;

import com.mojang.serialization.MapCodec;
import com.nred.azurum_miner.block_entity.TankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.nred.azurum_miner.registration.BlockEntityRegistration.TANK_BLOCK_ENTITY;

public class TankBlock extends TickingBlock<TankBlockEntity> {
    public TankBlock(Properties properties) {
        super(TankBlockEntity::new, TANK_BLOCK_ENTITY, properties);
    }

    public static final MapCodec<TankBlock> CODEC = simpleCodec(TankBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.box(1.0 / 16.0, 0.0, 1.0 / 16.0, 15.0 / 16.0, 1.0, 15.0 / 16.0);
    }
}