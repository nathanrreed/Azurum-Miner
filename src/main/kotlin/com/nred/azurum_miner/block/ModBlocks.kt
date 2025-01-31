package com.nred.azurum_miner.block

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.util.Helpers
import com.nred.azurum_miner.util.OreHelper
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object ModBlocks {
    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(AzurumMiner.ID)

    val ENERGIZED_OBSIDIAN = Helpers.registerBlock("energized_obsidian_block", BLOCKS) { Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(50.0F, 1200.0F).lightLevel { 15 }) }
    val CONGLOMERATE_OF_ORE = Helpers.registerBlock("conglomerate_of_ore", BLOCKS) { Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(50.0F, 1200.0F)) }
    val CONGLOMERATE_OF_ORE_BLOCK = Helpers.registerBlock("conglomerate_of_ore_block", BLOCKS) { Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1200.0F)) }

    init {
        OreHelper("azurum", true)
        OreHelper("thelxium")
        OreHelper("galibium")
        OreHelper("palestium")
    }

    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
    }
}
