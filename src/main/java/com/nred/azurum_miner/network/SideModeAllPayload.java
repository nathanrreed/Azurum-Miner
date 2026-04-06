package com.nred.azurum_miner.network;

import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import com.nred.azurum_miner.widget.side_mode.SideModeType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

import static com.nred.azurum_miner.util.Helpers.azLoc;

public record SideModeAllPayload(Map<Direction, ResourceHandlerSideMode> modeMap, BlockPos blockPos, SideModeType sideModeType) implements CustomPacketPayload {
    public static final Type<SideModeAllPayload> TYPE = new Type<>(azLoc("side_mode_all"));

    public static final StreamCodec<ByteBuf, SideModeAllPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(ISidedBlockEntity.CODEC.codec()), SideModeAllPayload::modeMap,
            ByteBufCodecs.fromCodec(BlockPos.CODEC), SideModeAllPayload::blockPos,
            ByteBufCodecs.fromCodec(SideModeType.CODEC), SideModeAllPayload::sideModeType,
            SideModeAllPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(final SideModeAllPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(data.blockPos) instanceof ISidedBlockEntity sidedBlockEntity) {
                sidedBlockEntity.setSideModes(data.modeMap, data.sideModeType);
            }
        }).thenRun(()->{
           context.reply(new AckSideModeAllPayload(data.sideModeType));
        });
    }
}