package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.tooltip.FluidTooltipComponent;
import com.nred.azurum_miner.tooltip.UpgradeTooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

import java.util.function.Function;

@EventBusSubscriber(Dist.CLIENT)
public class TooltipComponentRegistration {
    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(FluidTooltipComponent.class, Function.identity());
        event.register(UpgradeTooltipComponent.class, Function.identity());
    }
}