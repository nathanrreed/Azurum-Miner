package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.tooltip.FluidTooltipComponent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

import java.util.function.Function;

@EventBusSubscriber
public class TooltipComponentRegistration {
    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(FluidTooltipComponent.class, Function.identity());
    }
}