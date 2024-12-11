package com.nred.azurum_miner.fluid

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.util.FluidHelper
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModFluids {
    val FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, AzurumMiner.ID)
    val FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, AzurumMiner.ID)

    init {
        FluidHelper("nether_essence", 0xFF981d1d.toInt())
        FluidHelper("ender_essence", 0xFF349988.toInt())
        FluidHelper("molten_ore", 0xFFFFFFFF.toInt(), ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "block/fluid/molten_ore_still"), ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "block/fluid/molten_ore_flow"))
    }

//    class MoltenOreBlock(fluid: FlowingFluid, properties: Properties) : LiquidBlock(fluid, properties) {
//        override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
//            level.scheduleTick(pos, this, 200)
//            super.onPlace(state, level, pos, oldState, isMoving)
//        }
//
//        override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
//            level.setBlock(pos, CONGLOMERATE_OF_ORE_BLOCK.get().defaultBlockState(), 1.or(2))
//        }
//    }

    fun register(eventBus: IEventBus) {
        FLUID_TYPES.register(eventBus)
        FLUIDS.register(eventBus)
    }
}