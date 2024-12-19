package com.nred.azurum_miner.fluid

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.util.FluidHelper
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
        FluidHelper("molten_ore", 0xFFFFFFFF.toInt(), ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "block/fluid/molten_ore_still"), ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "block/fluid/molten_ore_flow"))
    }

    val snow_type: DeferredHolder<FluidType?, FluidType?> = FLUID_TYPES.register("snow_type") { -> FluidType(FluidType.Properties.create().temperature(-50)) }
    val snow_still = FLUIDS.register("snow") { -> BaseFlowingFluid.Source(snow_properties) }
    val snow_properties: BaseFlowingFluid.Properties = BaseFlowingFluid.Properties({ -> snow_type.get() }, { -> this.snow_still.get() }, { -> this.snow_still.get() }).bucket({ Items.POWDER_SNOW_BUCKET })

    val snow_client = object : IClientFluidTypeExtensions {
        override fun getStillTexture(): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "block/fluid/snow_still")
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