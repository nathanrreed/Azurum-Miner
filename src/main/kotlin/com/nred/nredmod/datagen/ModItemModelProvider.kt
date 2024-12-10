package com.nred.nredmod.datagen

import com.nred.nredmod.ModItems
import com.nred.nredmod.NredMod
import com.nred.nredmod.util.FluidHelper
import com.nred.nredmod.util.OreHelper
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class ModItemModelProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) : ItemModelProvider(output, NredMod.ID, existingFileHelper) {
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

        for (ore in OreHelper.ORES) {
            if (ore.isOre) {
                basicItem(ore.ingot!!.get())
                basicItem(ore.gear!!.get())
                basicItem(ore.nugget!!.get())
                basicItem(ore.raw!!.get())
            }

            if (ore.isGem) {
                basicItem(ore.gem!!.get())
            }
        }
    }
}