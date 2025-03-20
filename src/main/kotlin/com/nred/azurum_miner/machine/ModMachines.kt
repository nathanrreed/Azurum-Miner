package com.nred.azurum_miner.machine

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.generator.Generator
import com.nred.azurum_miner.machine.infuser.Infuser
import com.nred.azurum_miner.machine.liquifier.Liquifier
import com.nred.azurum_miner.machine.miner.Miner
import com.nred.azurum_miner.machine.simple_generator.SimpleGenerator
import com.nred.azurum_miner.machine.transmogrifier.Transmogrifier
import com.nred.azurum_miner.util.Helpers
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler
import net.neoforged.neoforge.registries.DeferredRegister

object ModMachines {
    val MACHINES: DeferredRegister.Blocks = DeferredRegister.createBlocks(AzurumMiner.ID)
    val BASE_PROPERTIES: BlockBehaviour.Properties = BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1200.0F).lightLevel { state -> if (state.getValue(AbstractMachine.MACHINE_ON)) 7 else 0 }.isValidSpawn(Blocks::never).isRedstoneConductor({ _, _, _ -> false })

    val MINER_BLOCK_TIERS = (0..<5).toList().map { tier ->
        Helpers.registerBlock("miner_block_tier_${tier + 1}", MACHINES) { ->
            Miner(tier, BASE_PROPERTIES)
        }
    }

    val LIQUIFIER = Helpers.registerBlock("liquifier_block", MACHINES) { -> Liquifier(BASE_PROPERTIES) }
    val INFUSER = Helpers.registerBlock("infuser_block", MACHINES) { -> Infuser(BASE_PROPERTIES) }
    val TRANSMOGRIFIER = Helpers.registerBlock("transmogrifier_block", MACHINES) { -> Transmogrifier(BASE_PROPERTIES) }
    val SIMPLE_GENERATOR = Helpers.registerBlock("simple_generator_block", MACHINES) { -> SimpleGenerator(BASE_PROPERTIES) }
    val GENERATOR = Helpers.registerBlock("generator_block", MACHINES) { -> Generator(BASE_PROPERTIES.noOcclusion().isViewBlocking({ _, _, _ -> false })) }

    fun register(eventBus: IEventBus) {
        MACHINES.register(eventBus)
    }

    open class SlotItemHandlerWithPickup(itemHandler: IItemHandler, slot: Int, x: Int, y: Int, val mayPickup: Boolean = true) : SlotItemHandler(itemHandler, slot, x, y) {
        override fun mayPickup(playerIn: Player): Boolean {
            return mayPickup
        }
    }
}