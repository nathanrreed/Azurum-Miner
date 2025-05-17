package com.nred.azurum_miner.fluid

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.util.FluidHelper
import com.nred.azurum_miner.util.Helpers.azLoc
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluid
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModFluids {
    val FLUID_TYPES: DeferredRegister<FluidType?> = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, AzurumMiner.ID)
    val FLUIDS: DeferredRegister<Fluid?> = DeferredRegister.create(BuiltInRegistries.FLUID, AzurumMiner.ID)

    init {
        FluidHelper("nether_essence", 0xFF982b2b.toInt())
        FluidHelper("ender_essence", 0xFF208c7a.toInt())
        FluidHelper("diamond_crystal_solution", 0XFF49EAD6.toInt(), ResourceLocation.withDefaultNamespace("block/water_still"), ResourceLocation.withDefaultNamespace("block/water_flow"))
        FluidHelper("emerald_crystal_solution", 0XFF17DA61.toInt(), ResourceLocation.withDefaultNamespace("block/water_still"), ResourceLocation.withDefaultNamespace("block/water_flow"))
        FluidHelper("quartz_crystal_solution", 0XFFE7E2DB.toInt(), ResourceLocation.withDefaultNamespace("block/water_still"), ResourceLocation.withDefaultNamespace("block/water_flow"))
        FluidHelper("amethyst_crystal_solution", 0XFF8B69CA.toInt(), ResourceLocation.withDefaultNamespace("block/water_still"), ResourceLocation.withDefaultNamespace("block/water_flow"))
        FluidHelper("prismarine_crystal_solution", 0XFF8FC3B5.toInt(), ResourceLocation.withDefaultNamespace("block/water_still"), ResourceLocation.withDefaultNamespace("block/water_flow"))
//        FluidHelper("end_crystal_solution", 0xFF0000FF.toInt())
        FluidHelper("molten_ore", 0xFFFFFFFF.toInt(), azLoc("block/fluid/molten_ore_still"), azLoc("block/fluid/molten_ore_flow"))
    }

    val snow_type: DeferredHolder<FluidType?, FluidType?> = FLUID_TYPES.register("snow_type") { -> FluidType(FluidType.Properties.create().temperature(-50)) }
    val snow_still = FLUIDS.register("snow") { -> BaseFlowingFluid.Source(snow_properties) }
    val snow_properties: BaseFlowingFluid.Properties = BaseFlowingFluid.Properties({ -> snow_type.get() }, { -> this.snow_still.get() }, { -> this.snow_still.get() }).bucket({ Items.POWDER_SNOW_BUCKET })

    val snow_client = object : IClientFluidTypeExtensions {
        override fun getStillTexture(): ResourceLocation {
            return azLoc("block/fluid/snow_still")
        }

        override fun getTintColor(): Int {
            return 0xFFD6E5FF.toInt()
        }
    }

    fun register(eventBus: IEventBus) {
        FLUID_TYPES.register(eventBus)
        FLUIDS.register(eventBus)
    }
}