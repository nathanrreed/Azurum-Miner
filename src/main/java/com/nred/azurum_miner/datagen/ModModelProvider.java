package com.nred.azurum_miner.datagen;

import com.nred.azurum_miner.render.item.TankSpecialRenderer;
import com.nred.azurum_miner.util.OreMaterial;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;

import java.util.Optional;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.registration.BlockRegistration.*;
import static com.nred.azurum_miner.registration.ItemRegistration.*;
import static com.nred.azurum_miner.registration.OreRegistration.ORE_MATERIALS;
import static com.nred.azurum_miner.util.Helpers.azLoc;
import static net.minecraft.client.data.models.BlockModelGenerators.*;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(CONGLOMERATE_OF_ORE_BLOCK.get());
        blockModels.createTrivialCube(CONGLOMERATE_OF_ORE.get());
        blockModels.createTrivialCube(ENERGIZED_OBSIDIAN.get());
        createMachineSimple(UPGRADE_TABLE_BLOCK.get(), blockModels, azLoc("block/upgrade_table"));

        Variant variant = new Variant(modLocation("block/tank"));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(TANK_BLOCK.get(), BlockModelGenerators.variant(variant)));
        itemModels.itemModelOutput.accept(
                TANK_BLOCK.asItem(),
                new SpecialModelWrapper.Unbaked(
                        azLoc("block/tank"),
                        Optional.empty(),
                        new TankSpecialRenderer.Unbaked()
                )
        );

        itemModels.generateFlatItem(SIMPLE_VOID_PROCESSOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(VOID_PROCESSOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ELABORATE_VOID_PROCESSOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(COMPLEX_VOID_PROCESSOR.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(SIMPLE_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(UPGRADE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ELABORATE_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(COMPLEX_UPGRADE.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(CONGLOMERATE_OF_ORE_SHARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(NETHER_DIAMOND.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ENDER_DIAMOND.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ENERGIZED_SHARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(VOID_SHARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EMPTY_DIMENSIONAL_MATRIX.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(DIMENSIONAL_MATRIX.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SEED_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);

        itemModels.itemModelOutput.accept(
                PIPETTE.get(),
                new DynamicFluidContainerModel.Unbaked(
                        new DynamicFluidContainerModel.Textures(
                                Optional.of(new Material(azLoc("item/pipette"))),
                                Optional.of(new Material(azLoc("item/pipette"))),
                                Optional.of(new Material(azLoc("item/pipette_mask"))),
                                Optional.empty()
                        ),
                        Fluids.EMPTY,
                        false,
                        false,
                        true
                )
        );

        for (OreMaterial oreMaterial : ORE_MATERIALS) {
            oreMaterial.registerModels(blockModels, itemModels);
        }
    }

    public static final ModelTemplate MACHINE = ModelTemplates.create("azurum_miner:machine", TextureSlot.PARTICLE, TextureSlot.ALL);

    public void createMachineSimple(Block block, BlockModelGenerators blockModels, Identifier identifier) {
        TextureMapping mapping = TextureMapping.cube(block).put(TextureSlot.ALL, new Material(identifier));
        blockModels.blockStateOutput.accept(createSimpleBlock(block, plainVariant(MACHINE.create(block, mapping, blockModels.modelOutput))));
    }

    public void createMachine(Block block, BlockModelGenerators blockModels, Identifier identifier) {
        TextureMapping mapping = TextureMapping.cube(block).put(TextureSlot.ALL, new Material(identifier));
        MultiVariant model = plainVariant(MACHINE.create(block, mapping, blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, model).with(ROTATION_HORIZONTAL_FACING));
    }
}