package com.nred.azurum_miner.datagen;

import com.nred.azurum_miner.util.OreMaterial;
import com.nred.azurum_miner.render.item.TankSpecialRenderer;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;

import java.util.Optional;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.registration.BlockRegistration.*;
import static com.nred.azurum_miner.registration.ItemRegistration.*;
import static com.nred.azurum_miner.registration.OreRegistration.ORE_MATERIALS;
import static com.nred.azurum_miner.util.Helpers.azLoc;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(CONGLOMERATE_OF_ORE_BLOCK.get());
        blockModels.createTrivialCube(CONGLOMERATE_OF_ORE.get());
        blockModels.createTrivialCube(ENERGIZED_OBSIDIAN.get());

        Identifier modelLoc = modLocation("block/tank"); // TODO cleanup
        Variant variant = new Variant(modelLoc);
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
}