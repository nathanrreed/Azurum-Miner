package com.nred.azurum_miner.network;

import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.nred.azurum_miner.util.Helpers.azLoc;

public record SideModePayload(Direction direction, BlockPos blockPos, ResourceHandlerSideMode newMode, boolean isFluid) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SideModePayload> TYPE = new CustomPacketPayload.Type<>(azLoc("side_mode"));

    public static final StreamCodec<ByteBuf, SideModePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Direction.CODEC), SideModePayload::direction,
            ByteBufCodecs.fromCodec(BlockPos.CODEC), SideModePayload::blockPos,
            ByteBufCodecs.fromCodec(ResourceHandlerSideMode.CODEC), SideModePayload::newMode,
            ByteBufCodecs.BOOL, SideModePayload::isFluid,
            SideModePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(final SideModePayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(data.blockPos) instanceof ISidedBlockEntity sidedBlockEntity) {
                sidedBlockEntity.setSideMode(data.direction, data.newMode, data.isFluid);
            }
        });
    }
}