package com.nred.nredmod.datagen

import com.nred.nredmod.NredMod
import com.nred.nredmod.block.ModBlocks
import com.nred.nredmod.machine.AbstractMachine
import com.nred.nredmod.machine.ModMachines
import com.nred.nredmod.util.FluidHelper
import com.nred.nredmod.util.OreHelper
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredBlock


class ModBlockStateProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) :
    BlockStateProvider(output, NredMod.ID, existingFileHelper) {

    override fun registerStatesAndModels() {
        blockWithItem(ModBlocks.CONGLOMERATE_OF_ORE_BLOCK)
        blockWithItem(ModBlocks.CONGLOMERATE_OF_ORE)

        for(fluid in FluidHelper.FLUIDS){
            simpleBlock(fluid.block.get(), models().cubeAll(fluid.block.get().name.string, fluid.client.stillTexture))
        }

        for (ore in OreHelper.ORES) {
            blockWithItem(ore.ore)
            blockWithItem(ore.deepslate_ore)
            blockWithItem(ore.block)

            if (ore.isOre) {
                blockWithItem(ore.raw_block!!)
            }
        }

        for (i in 0..<5) {
            simpleMachineModel(ModMachines.MINER_BLOCK_TIERS[i], "miner")
        }

        simpleMachineModel(ModMachines.LIQUIFIER, "liquifier")
        simpleMachineModel(ModMachines.INFUSER, "infuser")
    }

    private fun blockWithItem(deferredBlock: DeferredBlock<Block>) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()))
    }

    private fun simpleMachineModel(deferredBlock: DeferredBlock<Block>, name: String) {
        val modelOn = UncheckedModelFile(modLoc("block/${name}_block_on"))
        val modelOff = UncheckedModelFile(modLoc("block/${name}_block_off"))

        horizontalBlock(
            deferredBlock.get()
        ) { state -> if (state.getValue(AbstractMachine.MACHINE_ON)) modelOn else modelOff }

        simpleBlockItem(deferredBlock.get(), UncheckedModelFile(modLoc("block/${name}_block_off")))
    }
}