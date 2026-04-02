package com.nred.azurum_miner.datagen;

import com.nred.azurum_miner.util.OreMaterial;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

import static com.nred.azurum_miner.registration.BlockRegistration.*;
import static com.nred.azurum_miner.registration.DataComponentRegistration.SIMPLE_FLUID_COMPONENT;
import static com.nred.azurum_miner.registration.OreRegistration.ORE_MATERIALS;
import static com.nred.azurum_miner.registration.Registries.BLOCKS;

public class ModBlockLootSubProvider extends BlockLootSubProvider {
    public ModBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    public void generate() {
//        for (i in 0..< 5){ TODO
//            add(MINER_BLOCK_TIERS[i].get(), createMachineDrop(MINER_BLOCK_TIERS[i].get()))
//        }

//        add(INFUSER.get(), createMachineDrop(INFUSER.get()))
//        add(LIQUIFIER.get(), createMachineDrop(LIQUIFIER.get()))
//        add(CRYSTALLIZER.get(), createMachineDrop(CRYSTALLIZER.get()))
//        add(TRANSMOGRIFIER.get(), createMachineDrop(TRANSMOGRIFIER.get()))
//        add(GENERATOR.get(), createMachineDrop(GENERATOR.get()))
//        add(SIMPLE_GENERATOR.get(), createMachineDrop(SIMPLE_GENERATOR.get()))

        dropSelf(CONGLOMERATE_OF_ORE_BLOCK.get());
        dropSelf(CONGLOMERATE_OF_ORE.get());
        dropSelf(ENERGIZED_OBSIDIAN.get());

        createTankDrop(TANK_BLOCK.get());

        for (OreMaterial ore : ORE_MATERIALS) {
            if (ore instanceof OreMaterial.OreMaterialHasShard) {
                createOreDrops(ore.ore.get(), ((OreMaterial.OreMaterialHasShard) ore).shard.get(), 1f, 2f);
                createOreDrops(ore.deepslate_ore.get(), ((OreMaterial.OreMaterialHasShard) ore).shard.get(), 1f, 2f);
            } else if (ore instanceof OreMaterial.OreMaterialHasRaw rawMaterial) {
                createOreDrops(ore.ore.get(), rawMaterial.raw.get(), 1f, 1f);
                createOreDrops(ore.deepslate_ore.get(), rawMaterial.raw.get(), 1f, 1f);

                dropSelf(rawMaterial.raw_block.get());
            }

            dropSelf(ore.ingot_block.get());
        }
    }

    private void createOreDrops(Block ore, Item item, float min, float max) {
        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        add(ore, this.createSilkTouchDispatchTable(
                        ore, this.applyExplosionDecay(
                                ore, LootItem.lootTableItem(item)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                        .apply(ApplyBonusCount.addOreBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE)))
                        )
                )
        );
    }

    private void createMachineDrop(Block block) {
        add(block, LootTable.lootTable().withPool(
                this.applyExplosionCondition(block,
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0f))
                                .add(LootItem.lootTableItem(block)
                                        .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                                                .include(DataComponents.CUSTOM_DATA).include(DataComponents.CUSTOM_NAME)
                                        )
                                )
                )
        ));
    }

    private void createTankDrop(Block block) {
        add(block, LootTable.lootTable().withPool(
                this.applyExplosionCondition(block,
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f))
                                .add(LootItem.lootTableItem(block)
                                        .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                                                .include(SIMPLE_FLUID_COMPONENT.get()).include(DataComponents.CUSTOM_NAME) // TODO see if better way
                                        )
                                )
                )
        ));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BLOCKS.getEntries().stream().map(e -> (Block) e.get()).toList();
    }
}