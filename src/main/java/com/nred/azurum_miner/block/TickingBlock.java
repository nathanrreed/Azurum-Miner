package com.nred.azurum_miner.block;

import com.nred.azurum_miner.block_entity.ITickingBlockEntity;
import com.nred.azurum_miner.block_entity.TickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class TickingBlock<T extends TickingBlockEntity> extends BaseEntityBlock {
    private final BiFunction<BlockPos, BlockState, T> blockEntityFunc;
    private final Supplier<BlockEntityType<T>> entityType;

    public TickingBlock(BiFunction<BlockPos, BlockState, T> blockEntityFunc, Supplier<BlockEntityType<T>> entityType, Properties properties) {
        super(properties);
        this.blockEntityFunc = blockEntityFunc;
        this.entityType = entityType;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return blockEntityFunc.apply(blockPos, blockState);
    }

    @Override
    public @Nullable <Q extends BlockEntity> BlockEntityTicker<Q> getTicker(Level level, BlockState blockState, BlockEntityType<Q> type) {
        return createTickerHelper(type, entityType.get(), ITickingBlockEntity::tick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, null)) { // TODO check if should be contained
            return InteractionResult.SUCCESS;
        } else {
            return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
        }
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