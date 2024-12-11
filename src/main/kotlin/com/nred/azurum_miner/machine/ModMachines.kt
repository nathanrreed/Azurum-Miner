package com.nred.azurum_miner.machine

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.infuser.Infuser
import com.nred.azurum_miner.machine.liquifier.Liquifier
import com.nred.azurum_miner.machine.miner.Miner
import com.nred.azurum_miner.util.Helpers
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object ModMachines {
    val MACHINES = DeferredRegister.createBlocks(AzurumMiner.ID)

    val MINER_BLOCK_TIERS = (0..<5).toList().map { tier ->
        Helpers.registerBlock("miner_block_tier_${tier + 1}", MACHINES) { ->
            Miner(tier, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1200.0F))
        }
    }

    val LIQUIFIER = Helpers.registerBlock("liquifier_block", MACHINES) {-> Liquifier(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1200.0F))}
    val INFUSER = Helpers.registerBlock("infuser_block", MACHINES) {-> Infuser(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1200.0F)) }

    fun register(eventBus: IEventBus) {
        MACHINES.register(eventBus)
    }
}