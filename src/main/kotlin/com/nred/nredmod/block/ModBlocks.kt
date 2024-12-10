package com.nred.nredmod.block

import com.nred.nredmod.NredMod
import com.nred.nredmod.util.Helpers
import com.nred.nredmod.util.OreHelper
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object ModBlocks {
    val BLOCKS = DeferredRegister.createBlocks(NredMod.ID)
    val BLOCK_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, NredMod.ID)

    val CONGLOMERATE_OF_ORE = Helpers.registerBlock("conglomerate_of_ore", BLOCKS) { Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1200.0F).noLootTable()) }
    val CONGLOMERATE_OF_ORE_BLOCK = Helpers.registerBlock("conglomerate_of_ore_block", BLOCKS) { Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1200.0F)) }

    init {
        OreHelper("azurum", true)
        OreHelper("thelxium")
        OreHelper("galibium")
        OreHelper("palestium")
    }

    fun register(eventBus: IEventBus) {
        BLOCK_TYPES.register(eventBus)
        BLOCKS.register(eventBus)
    }
}
