package com.nred.nredmod.util

import com.mojang.blaze3d.shaders.FogShape
import com.mojang.blaze3d.systems.RenderSystem
import com.nred.nredmod.ModItems.ITEMS
import com.nred.nredmod.NredMod
import com.nred.nredmod.block.ModBlocks.BLOCKS
import com.nred.nredmod.fluid.ModFluids
import com.nred.nredmod.fluid.ModFluids.FLUID_TYPES
import net.minecraft.client.Camera
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import kotlin.reflect.KFunction2

class FluidHelper(name: String, tint: Int, still: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "block/fluid/fluid_still"), flow: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "block/fluid/fluid_flow"), block_func: KFunction2<FlowingFluid, BlockBehaviour.Properties, LiquidBlock> = ::LiquidBlock) {
    companion object {
        val FLUIDS = ArrayList<Fluid>()

        operator fun ArrayList<Fluid>.get(name: String): Fluid {
            return FLUIDS.filter { fluid -> fluid.name == name }[0]
        }
    }

    init {
        FLUIDS.add(Fluid(name, tint, block_func, still, flow))
    }
}

class Fluid(val name: String, val tint: Int, val block_func: KFunction2<FlowingFluid, BlockBehaviour.Properties, LiquidBlock>, still: ResourceLocation, flow: ResourceLocation) {
    val type = FLUID_TYPES.register(name + "_type", { -> FluidType(FluidType.Properties.create().lightLevel(3).temperature(1200).viscosity(100000).density(100000).motionScale(0.00001).fallDistanceModifier(0.05f)) })
    val still: DeferredHolder<net.minecraft.world.level.material.Fluid, BaseFlowingFluid.Source> = ModFluids.FLUIDS.register(name, { -> BaseFlowingFluid.Source(this.properties) })
    val flowing: DeferredHolder<net.minecraft.world.level.material.Fluid, BaseFlowingFluid.Flowing> = ModFluids.FLUIDS.register(name + "flowing", { -> BaseFlowingFluid.Flowing(this.properties) })
    val properties: BaseFlowingFluid.Properties = BaseFlowingFluid.Properties({ -> this.type.get() }, { -> this.still.get() }, { -> this.flowing.get() }).bucket({ this.bucket.get() }).block({ this.block.get() }).tickRate(100).levelDecreasePerBlock(2)

    val block = BLOCKS.register(name, { ->
        block_func(
            this.still.get(), BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                .replaceable()
                .noCollission()
                .strength(100.0F)
                .lightLevel({ _ -> 2 })
                .pushReaction(PushReaction.DESTROY)
                .noLootTable()
                .liquid()
        )
    })

    val bucket = ITEMS.register(name + "_bucket", { -> BucketItem(this.still.get(), Properties().stacksTo(1)) })

    val client = object : IClientFluidTypeExtensions {
        override fun getFlowingTexture(): ResourceLocation {
            return flow
        }

        override fun getOverlayTexture(): ResourceLocation? {
            return ResourceLocation.withDefaultNamespace("block/water_overlay")
        }

        override fun getStillTexture(): ResourceLocation {
            return still
        }

        override fun getTintColor(): Int {
            return tint
        }

        override fun modifyFogRender(camera: Camera, mode: FogRenderer.FogMode, renderDistance: Float, partialTick: Float, nearDistance: Float, farDistance: Float, shape: FogShape) {
            RenderSystem.setShaderFogStart(1f)
            RenderSystem.setShaderFogEnd(6f)
        }
    }
}