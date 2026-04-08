package com.nred.azurum_miner.block;

import com.nred.azurum_miner.block_entity.ITickingBlockEntity;
import com.nred.azurum_miner.block_entity.TickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.InfiniteResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.VoidingResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public abstract class TickingBlock<T extends TickingBlockEntity> extends BaseEntityBlock {
    private final BiFunction<BlockPos, BlockState, T> blockEntityFunc;
    private final Supplier<BlockEntityType<T>> entityType;

    public TickingBlock(BiFunction<BlockPos, BlockState, T> blockEntityFunc, Supplier<BlockEntityType<T>> entityType, Properties properties) {
        super(properties);
        this.blockEntityFunc = blockEntityFunc;
        this.entityType = entityType;

        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return blockEntityFunc.apply(blockPos, blockState);
    }

    @Override
    public @Nullable <Q extends BlockEntity> BlockEntityTicker<Q> getTicker(Level level, BlockState blockState, BlockEntityType<Q> type) {
        return createTickerHelper(type, entityType.get(), ITickingBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.hasInfiniteMaterials() && !itemStack.isEmpty()) { // Creative
            ItemAccess itemAccess = ItemAccess.forPlayerInteraction(player, hand).oneByOne();
            ResourceHandler<FluidResource> handHandler = itemAccess.getCapability(Capabilities.Fluid.ITEM);
            if (handHandler != null) {
                ResourceHandler<FluidResource> creativeHandler;
                boolean pickup = false;
                FluidResource fluid = ResourceHandlerUtil.findExtractableResource(handHandler, _ -> true, null);
                if (fluid == null || fluid.isEmpty()) {
                    creativeHandler = new VoidingResourceHandler<>(FluidResource.EMPTY);
                    pickup = true;
                } else {
                    creativeHandler = new InfiniteResourceHandler<>(fluid);
                }

                ResourceHandler<FluidResource> fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, pos, null);
                ResourceStack<FluidResource> rtn = ResourceHandlerUtil.moveFirst(pickup ? fluidHandler : creativeHandler, pickup ? creativeHandler : fluidHandler, _ -> true, Integer.MAX_VALUE, null);
                if (rtn != null) {
                    FluidUtil.triggerSoundAndGameEvent(rtn.resource(), level, pos.getCenter(), player, pickup);
                    return InteractionResult.SUCCESS;
                } else {
                    return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
                }
            }
        }

        if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, null)) { // TODO fix block fake placing
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(state.getMenuProvider(level, pos), b -> b.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}