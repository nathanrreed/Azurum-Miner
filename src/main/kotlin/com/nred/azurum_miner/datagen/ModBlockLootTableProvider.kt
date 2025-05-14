package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.block.ModBlocks.BLOCKS
import com.nred.azurum_miner.block.ModBlocks.CONGLOMERATE_OF_ORE
import com.nred.azurum_miner.block.ModBlocks.CONGLOMERATE_OF_ORE_BLOCK
import com.nred.azurum_miner.block.ModBlocks.ENERGIZED_OBSIDIAN
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.ModMachines.CRYSTALLIZER
import com.nred.azurum_miner.machine.ModMachines.GENERATOR
import com.nred.azurum_miner.machine.ModMachines.INFUSER
import com.nred.azurum_miner.machine.ModMachines.LIQUIFIER
import com.nred.azurum_miner.machine.ModMachines.MINER_BLOCK_TIERS
import com.nred.azurum_miner.machine.ModMachines.SIMPLE_GENERATOR
import com.nred.azurum_miner.machine.ModMachines.TRANSMOGRIFIER
import com.nred.azurum_miner.util.OreHelper
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator

class ModBlockLootTableProvider(registries: HolderLookup.Provider) :
    BlockLootSubProvider(setOf(), FeatureFlags.REGISTRY.allFlags(), registries) {

    fun createMachineDrop(block: Block): LootTable.Builder {
        return LootTable.lootTable()
            .withPool(
                this.applyExplosionCondition(
                    block,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0f))
                        .add(
                            LootItem.lootTableItem(block)
                                .apply(
                                    CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                        .include(DataComponents.CUSTOM_DATA).include(DataComponents.CUSTOM_NAME)
                                )
                        )
                )
            )
    }

    override fun generate() {
        for (i in 0..<5) {
            add(MINER_BLOCK_TIERS[i].get(), createMachineDrop(MINER_BLOCK_TIERS[i].get()))
        }

        add(INFUSER.get(), createMachineDrop(INFUSER.get()))
        add(LIQUIFIER.get(), createMachineDrop(LIQUIFIER.get()))
        add(CRYSTALLIZER.get(), createMachineDrop(CRYSTALLIZER.get()))
        add(TRANSMOGRIFIER.get(), createMachineDrop(TRANSMOGRIFIER.get()))
        add(GENERATOR.get(), createMachineDrop(GENERATOR.get()))
        add(SIMPLE_GENERATOR.get(), createMachineDrop(SIMPLE_GENERATOR.get()))

        dropSelf(CONGLOMERATE_OF_ORE_BLOCK.get())
        dropSelf(CONGLOMERATE_OF_ORE.get())
        dropSelf(ENERGIZED_OBSIDIAN.get())

        for (ore in OreHelper.ORES) {
            if (ore.isGem) {
                createOreDrops(ore.ore.get(), ore.gem!!.get(), 1f, 2f)
                createOreDrops(ore.deepslate_ore.get(), ore.gem!!.get(), 1f, 2f)
            } else if (ore.isOre) {
                createOreDrops(ore.ore.get(), ore.raw!!.get(), 1f, 1f)
                createOreDrops(ore.deepslate_ore.get(), ore.raw!!.get(), 1f, 1f)
            }

            dropSelf(ore.block.get())

            if (ore.isOre && !ore.isGem) {
                dropSelf(ore.raw_block!!.get())
            }
        }
    }

    fun createOreDrops(ore: Block, item: Item, min: Float, max: Float) {
        val registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT)

        add(
            ore, this.createSilkTouchDispatchTable(
                ore, this.applyExplosionDecay(
                    ore, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        .apply(ApplyBonusCount.addOreBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE)))
                )
            )
        )
    }

    override fun getKnownBlocks(): MutableIterable<Block> {
        return (BLOCKS.entries.stream().map { it.get() }.toList() + ModMachines.MACHINES.entries.stream()
            .map { it.get() }.toList())
            .toMutableList<Block>()
    }
}