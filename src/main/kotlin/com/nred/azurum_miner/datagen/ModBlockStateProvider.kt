package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.block.ModBlocks
import com.nred.azurum_miner.machine.AbstractMachine
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.util.FluidHelper
import com.nred.azurum_miner.util.OreHelper
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredBlock


class ModBlockStateProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) :
    BlockStateProvider(output, AzurumMiner.ID, existingFileHelper) {

    override fun registerStatesAndModels() {
        blockWithItem(ModBlocks.CONGLOMERATE_OF_ORE_BLOCK)
        blockWithItem(ModBlocks.CONGLOMERATE_OF_ORE)
        blockWithItem(ModBlocks.ENERGIZED_OBSIDIAN)

        for (fluid in FluidHelper.FLUIDS) {
            simpleBlock(fluid.block.get(), models().cubeAll(fluid.block.get().name.string, fluid.client.stillTexture))
        }

        for (ore in OreHelper.ORES) {
            blockWithItem(ore.ore)
            blockWithItem(ore.deepslate_ore)
            blockWithItem(ore.block)

            if (ore.isOre && !ore.isGem) {
                blockWithItem(ore.raw_block!!)
            }
        }

        for (i in 0..<5) {
            simpleMachineModel(ModMachines.MINER_BLOCK_TIERS[i], "miner")
        }

        simpleMachineModel(ModMachines.LIQUIFIER, "liquifier")
        simpleMachineModel(ModMachines.INFUSER, "infuser")
        simpleMachineModel(ModMachines.TRANSMOGRIFIER, "transmogrifier")
        simpleMachineModel(ModMachines.GENERATOR, "generator")
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

        simpleBlockItem(deferredBlock.get(), UncheckedModelFile(modLoc("block/${name}_block_on")))
    }
}