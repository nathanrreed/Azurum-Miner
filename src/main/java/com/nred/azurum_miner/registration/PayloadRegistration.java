package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.network.AckSideModeAllPayload;
import com.nred.azurum_miner.network.FluidTankTransferPayload;
import com.nred.azurum_miner.network.SideModeAllPayload;
import com.nred.azurum_miner.network.SideModePayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class PayloadRegistration {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(SideModePayload.TYPE, SideModePayload.STREAM_CODEC, SideModePayload::handleOnServer);
        registrar.playToServer(SideModeAllPayload.TYPE, SideModeAllPayload.STREAM_CODEC, SideModeAllPayload::handleOnServer);
        registrar.playToServer(FluidTankTransferPayload.TYPE, FluidTankTransferPayload.STREAM_CODEC, FluidTankTransferPayload::handleOnServer);

        registrar.playToClient(AckSideModeAllPayload.TYPE, AckSideModeAllPayload.STREAM_CODEC);
    }
}