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
    val snow_still= FLUIDS.register("snow") { -> BaseFlowingFluid.Source(snow_properties) }
    val snow_properties: BaseFlowingFluid.Properties = BaseFlowingFluid.Properties({ -> snow_type.get() }, { -> this.snow_still.get() }, { -> this.snow_still.get() }).bucket({ Items.POWDER_SNOW_BUCKET })

    val snow_client = object : IClientFluidTypeExtensions {
        override fun getStillTexture(): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "block/fluid/snow_still")
        }

        override fun getTintColor(): Int {
            return 0xFFD6E5FF.toInt()
        }
    }


//    event.register<FluidType>(NeoForgeRegistries.Keys.FLUID_TYPES, Consumer<RegisterHelper<FluidType?>> { helper: RegisterHelper<FluidType?> ->
//        helper.register(
//            NeoForgeMod.MILK_TYPE.unwrapKey().orElseThrow(), FluidType(
//                FluidType.Properties.create().density(1024).viscosity(1024)
//                    .sound(SoundActions.BUCKET_FILL, NeoForgeMod.BUCKET_FILL_MILK.value())
//                    .sound(SoundActions.BUCKET_EMPTY, NeoForgeMod.BUCKET_EMPTY_MILK.value())
//            )
//        )
//    })


    // register fluids
//    event.register<Fluid>(Registries.FLUID, Consumer<RegisterHelper<Fluid?>> { helper: RegisterHelper<Fluid?> ->
//        // set up properties
//        val properties = BaseFlowingFluid.Properties({ NeoForgeMod.MILK_TYPE.value() }, { NeoForgeMod.MILK.value() }, { NeoForgeMod.FLOWING_MILK.value() }).bucket { Items.MILK_BUCKET }
//
//        helper.register(NeoForgeMod.MILK.id, BaseFlowingFluid.Source(properties))
//        helper.register(NeoForgeMod.FLOWING_MILK.id, BaseFlowingFluid.Flowing(properties))
//    })
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