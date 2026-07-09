package com.nred.azurum_miner.block;

import com.mojang.serialization.MapCodec;
import com.nred.azurum_miner.block_entity.ITickingBlockEntity;
import com.nred.azurum_miner.block_entity.UpgradeTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

import static com.nred.azurum_miner.registration.BlockEntityRegistration.UPGRADE_TABLE_BLOCK_ENTITY;

public class UpgradeSchematicTableBlock extends BaseEntityBlock {
    public UpgradeSchematicTableBlock(Properties properties) {
        super(properties);
    }

    public static final MapCodec<UpgradeSchematicTableBlock> CODEC = simpleCodec(UpgradeSchematicTableBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new UpgradeTableBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <Q extends BlockEntity> BlockEntityTicker<Q> getTicker(Level level, BlockState blockState, BlockEntityType<Q> type) {
        return createTickerHelper(type, UPGRADE_TABLE_BLOCK_ENTITY.get(), ITickingBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(state.getMenuProvider(level, pos), b -> b.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}