package com.nred.azurum_miner.compat.cct

import com.nred.azurum_miner.entity.ModBlockEntities.MINER_ENTITY_TIERS
import com.nred.azurum_miner.machine.miner.MinerEntity
import dan200.computercraft.api.peripheral.PeripheralCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

object RegisterPeripherals {
    fun registerPeripherals(event: RegisterCapabilitiesEvent) {
        for (i in 0..4)
            event.registerBlockEntity(PeripheralCapability.get(), MINER_ENTITY_TIERS[i].get(), { myBlockEntity: MinerEntity, _ -> MinerPeripheral(myBlockEntity) })
    }
}