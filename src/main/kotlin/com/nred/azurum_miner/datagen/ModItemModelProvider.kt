package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.util.FluidHelper
import com.nred.azurum_miner.util.OreHelper
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class ModItemModelProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) : ItemModelProvider(output, AzurumMiner.ID, existingFileHelper) {
    override fun registerModels() {
        for (fluid in FluidHelper.FLUIDS) {
            basicItem(fluid.bucket.get())
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
}