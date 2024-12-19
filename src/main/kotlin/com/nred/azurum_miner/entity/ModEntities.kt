package com.nred.azurum_miner.entity

import com.nred.azurum_miner.AzurumMiner
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object ModEntities {
    val ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, AzurumMiner.ID)

    fun register(eventBus: IEventBus) {
        ENTITY_TYPES.register(eventBus)
    }
}