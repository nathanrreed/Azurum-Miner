@file:Suppress("FunctionName")

package com.nred.azurum_miner.util

import com.mojang.blaze3d.shaders.FogShape
import com.mojang.blaze3d.systems.RenderSystem
import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.block.ModBlocks.BLOCKS
import com.nred.azurum_miner.fluid.ModFluids
import com.nred.azurum_miner.fluid.ModFluids.FLUID_TYPES
import com.nred.azurum_miner.item.ModItems.ITEMS
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
import kotlin.math.abs
import kotlin.reflect.KFunction2

@Suppress("LocalVariableName")
class FluidHelper(name: String, tint: Int, still: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "block/fluid/fluid_still"), flow: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "block/fluid/fluid_flow"), block_func: KFunction2<FlowingFluid, BlockBehaviour.Properties, LiquidBlock> = ::LiquidBlock) {
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

class Fluid(val name: String, val tint: Int, val blockFunc: KFunction2<FlowingFluid, BlockBehaviour.Properties, LiquidBlock>, still: ResourceLocation, flow: ResourceLocation) {
    val type: DeferredHolder<FluidType?, FluidType?> = FLUID_TYPES.register(name + "_type") { -> FluidType(FluidType.Properties.create().lightLevel(3).temperature(1200).viscosity(100000).density(100000).motionScale(0.00001).fallDistanceModifier(0.05f)) }
    val still = ModFluids.FLUIDS.register(name) { -> BaseFlowingFluid.Source(this.properties) }
    val flowing = ModFluids.FLUIDS.register(name + "flowing") { -> BaseFlowingFluid.Flowing(this.properties) }
    val properties: BaseFlowingFluid.Properties = BaseFlowingFluid.Properties({ -> this.type.get() }, { -> this.still.get() }, { -> this.flowing.get() }).bucket({ this.bucket.get() }).block({ this.block.get() }).tickRate(100).levelDecreasePerBlock(2)

    val block = BLOCKS.register(name) { ->
        blockFunc(
            this.still.get(), BlockBehaviour.Properties.of().mapColor(if (name == "molten_ore") MapColor.STONE else ARGBtoMapColor(tint))
                .replaceable()
                .noCollission()
                .strength(100.0F)
                .lightLevel { _ -> 2 }
                .pushReaction(PushReaction.DESTROY)
                .noLootTable()
                .liquid()
        )
    }

    val bucket = ITEMS.register(name + "_bucket") { -> BucketItem(this.still.get(), Properties().stacksTo(1)) }

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

@OptIn(ExperimentalStdlibApi::class) fun getRBG(tint: Int): Triple<Int, Int, Int> {
    val hex = tint.toHexString()
    if (tint > 0xFFFFFF)
        return Triple(hex.substring(2, 4).hexToInt(), hex.substring(4, 6).hexToInt(), hex.substring(6, 8).hexToInt())
    return Triple(hex.substring(0, 2).hexToInt(), hex.substring(2, 4).hexToInt(), hex.substring(4, 6).hexToInt())
}

// Try to find the closest color to the tint
fun ARGBtoMapColor(tint: Int): MapColor {
    val colorHex = getRBG(tint)

    var closest = MapColor.NONE
    var closestNum = 1024
    for (i in 1..61) {
        val temp = MapColor.byId(i)
        val rgb = getRBG(temp.col)
        val num = abs(rgb.first - colorHex.first) + abs(rgb.second - colorHex.second) + abs(rgb.third - colorHex.third)
        if (num < closestNum) {
            closest = temp
            closestNum = num
        }
    }

    return closest
}