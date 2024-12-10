package com.nred.nredmod.entity

import com.nred.nredmod.NredMod
import com.nred.nredmod.machine.ModMachines
import com.nred.nredmod.machine.infuser.InfuserEntity
import com.nred.nredmod.machine.liquifier.LiquifierEntity
import com.nred.nredmod.machine.miner.*
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModBlockEntities {
    val BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NredMod.ID)

    val MINER_ENTITY_TIERS = listOf(::MinerEntityTier1, ::MinerEntityTier2, ::MinerEntityTier3, ::MinerEntityTier4, ::MinerEntityTier5).mapIndexed { tier, entity ->

        BLOCK_ENTITY_TYPES.register(
            "miner_entity_tier_${tier + 1}",
            { ->
                BlockEntityType.Builder.of(entity, ModMachines.MINER_BLOCK_TIERS[tier].get()).build(null)
            })
    }

    val LIQUIFIER_ENTITY: DeferredHolder<BlockEntityType<*>, BlockEntityType<LiquifierEntity>> = BLOCK_ENTITY_TYPES.register("liquifier_entity", { -> BlockEntityType.Builder.of(::LiquifierEntity, ModMachines.LIQUIFIER.get()).build(null) })
    val INFUSER_ENTITY: DeferredHolder<BlockEntityType<*>, BlockEntityType<InfuserEntity>> = BLOCK_ENTITY_TYPES.register("infuser_entity", { -> BlockEntityType.Builder.of(::InfuserEntity, ModMachines.INFUSER.get()).build(null) })


    fun register(eventBus: IEventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus)
    }
}