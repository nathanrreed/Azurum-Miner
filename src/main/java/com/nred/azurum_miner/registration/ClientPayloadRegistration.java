package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.network.AckSideModeAllPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@EventBusSubscriber
public class ClientPayloadRegistration {
    @SubscribeEvent
    public static void register(RegisterClientPayloadHandlersEvent event) {
        event.register(AckSideModeAllPayload.TYPE, AckSideModeAllPayload::handleOnClient);
    }
}