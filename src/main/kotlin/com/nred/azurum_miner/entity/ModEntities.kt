package com.nred.azurum_miner.entity

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.item.VoidBullet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModEntities {
    val ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, AzurumMiner.ID)

    val VOID_BULLET: Supplier<EntityType<VoidBullet>> = ENTITY_TYPES.register("void_bullet") { -> EntityType.Builder.of(::VoidBullet, MobCategory.MISC).sized(0.25f, 0.25f).build("void_bullet") }

    fun register(eventBus: IEventBus) {
        ENTITY_TYPES.register(eventBus)
    }
}