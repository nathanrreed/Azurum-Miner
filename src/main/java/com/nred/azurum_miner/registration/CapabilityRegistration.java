package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.block_entity.TankBlockEntity;
import com.nred.azurum_miner.item.PipetteItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.registration.BlockEntityRegistration.TANK_BLOCK_ENTITY;
import static com.nred.azurum_miner.registration.BlockRegistration.TANK_BLOCK;
import static com.nred.azurum_miner.registration.DataComponentRegistration.SIMPLE_FLUID_COMPONENT;
import static com.nred.azurum_miner.registration.ItemRegistration.PIPETTE;

@EventBusSubscriber(modid = MODID)
public class CapabilityRegistration {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, TANK_BLOCK_ENTITY.get(), TankBlockEntity::getFluidHandler);
        event.registerItem(Capabilities.Fluid.ITEM, (_, access) -> new ItemAccessFluidHandler(access, SIMPLE_FLUID_COMPONENT.get(), TankBlockEntity.CAPACITY), TANK_BLOCK);
        event.registerItem(Capabilities.Fluid.ITEM, (_, access) -> new ItemAccessFluidHandler(access, SIMPLE_FLUID_COMPONENT.get(), PipetteItem.CAPACITY), PIPETTE);
    }
}