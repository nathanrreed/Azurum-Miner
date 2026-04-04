package com.nred.azurum_miner.network;

import com.nred.azurum_miner.screen.TankScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.nred.azurum_miner.util.Helpers.azLoc;

public record AckSideModeAllPayload(boolean isFluid) implements CustomPacketPayload {
    public static final Type<AckSideModeAllPayload> TYPE = new Type<>(azLoc("ack_side_mode_all"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AckSideModeAllPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, AckSideModeAllPayload::isFluid,
            AckSideModeAllPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnClient(final AckSideModeAllPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().screen instanceof TankScreen screen) {
                (data.isFluid ? screen.sideModeWidgetFluid : screen.sideModeWidgetItem).save_button.editMode = false;
            }
        });
    }
}