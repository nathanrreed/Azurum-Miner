package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.util.Fluid
import com.nred.azurum_miner.util.FluidHelper
import com.nred.azurum_miner.util.OreHelper
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder
import net.neoforged.neoforge.common.data.ExistingFileHelper

class ModItemModelProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) : ItemModelProvider(output, AzurumMiner.ID, existingFileHelper) {
    override fun registerModels() {
        for (fluid in FluidHelper.FLUIDS) {
            if (fluid.name.endsWith("crystal_solution")) {
                dynamicBucket(fluid)
            } else {
                basicItem(fluid.bucket.get())
            }
        }

        basicItem(ModItems.SIMPLE_VOID_PROCESSOR.get())
        basicItem(ModItems.VOID_PROCESSOR.get())
        basicItem(ModItems.ELABORATE_VOID_PROCESSOR.get())
        basicItem(ModItems.COMPLEX_VOID_PROCESSOR.get())
        basicItem(ModItems.CONGLOMERATE_OF_ORE_SHARD.get())
        basicItem(ModItems.NETHER_DIAMOND.get())
        basicItem(ModItems.ENDER_DIAMOND.get())
        basicItem(ModItems.DIMENSIONAL_MATRIX.get())
        basicItem(ModItems.EMPTY_DIMENSIONAL_MATRIX.get())
        basicItem(ModItems.ENERGY_SHARD.get())
        basicItem(ModItems.VOID_CRYSTAL.get())
        basicItem(ModItems.SEED_CRYSTAL.get())

        basicItem(ModItems.PALESTIUM_PICKAXE.get())

        handheldItem(ModItems.VOID_BULLET.get())

        for (ore in OreHelper.ORES) {
            if (ore.isOre) {
                basicItem(ore.ingot!!.get())
                basicItem(ore.gear!!.get())
                basicItem(ore.nugget!!.get())
            }

            if (ore.isGem) {
                basicItem(ore.gem!!.get())
            }

            if (ore.isOre && !ore.isGem) {
                basicItem(ore.raw!!.get())
            }
        }
    }

    private fun dynamicBucket(entry: Fluid) {
        withExistingParent(entry.name + "_bucket", ResourceLocation.fromNamespaceAndPath("neoforge", "item/bucket_drip"))
            .customLoader { parent: ItemModelBuilder, existingFileHelper: ExistingFileHelper -> DynamicFluidContainerModelBuilder.begin(parent, existingFileHelper) }
            .fluid(entry.still.get()).applyTint(true)
    }
}