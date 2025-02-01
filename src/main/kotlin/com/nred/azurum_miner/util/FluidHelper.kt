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
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundEvents.LAVA_EXTINGUISH
import net.minecraft.tags.DamageTypeTags
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageSources
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.common.SoundActions
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import kotlin.math.abs

class FluidHelper(name: String, tint: Int, still: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "block/fluid/fluid_still"), flow: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "block/fluid/fluid_flow")) {
    companion object {
        val FLUIDS = ArrayList<Fluid>()

        operator fun ArrayList<Fluid>.get(name: String): Fluid {
            return FLUIDS.filter { fluid -> fluid.name == name }[0]
        }
    }

    init {
        FLUIDS.add(Fluid(name, tint, still, flow))
    }
}

class Fluid(val name: String, val tint: Int, still: ResourceLocation, flow: ResourceLocation) {
    val type: DeferredHolder<FluidType?, FluidType?> = FLUID_TYPES.register(name + "_type") { -> FluidType(FluidType.Properties.create().lightLevel(3).temperature(1300).viscosity(100000).density(100000).motionScale(0.00001).fallDistanceModifier(0.05f).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)) }
    val still = ModFluids.FLUIDS.register(name) { -> BaseFlowingFluid.Source(this.properties) }
    val flowing = ModFluids.FLUIDS.register(name + "flowing") { -> BaseFlowingFluid.Flowing(this.properties) }
    val properties: BaseFlowingFluid.Properties = BaseFlowingFluid.Properties({ -> this.type.get() }, { -> this.still.get() }, { -> this.flowing.get() }).bucket({ this.bucket.get() }).block({ this.block.get() }).tickRate(100).levelDecreasePerBlock(2)
    val damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, name + "_damage"))

    val block = BLOCKS.register(name) { ->
        object : LiquidBlock(
            this.still.get(), BlockBehaviour.Properties.of().mapColor(if (name == "molten_ore") MapColor.STONE else ARGBtoMapColor(tint))
                .replaceable()
                .noCollission()
                .strength(100.0F)
                .lightLevel { _ -> 2 }
                .pushReaction(PushReaction.DESTROY)
                .noLootTable()
                .liquid()
                .randomTicks()
        ) {
            override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
                val damageSource = object : DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(damageType)) {
                    override fun `is`(damageTypeKey: ResourceKey<DamageType>): Boolean {
                        return damageTypeKey == DamageTypes.LAVA || damageTypeKey == DamageTypes.IN_FIRE
                    }

                    override fun `is`(damageTypeKey: TagKey<DamageType>): Boolean {
                        return damageTypeKey == DamageTypeTags.IS_FIRE || damageTypeKey == DamageTypeTags.NO_KNOCKBACK
                    }
                }

                if (entity is LivingEntity && !entity.fireImmune()) {
                    entity.hurt(damageSource, 2f)
                } else if (entity is ItemEntity && !entity.fireImmune()) {
                    entity.playSound(LAVA_EXTINGUISH)
                    entity.hurt(DamageSources(level.registryAccess()).lava(), 5f)
                }
                super.entityInside(state, level, pos, entity)
            }
        }
    }

    val bucket = ITEMS.register(name + "_bucket") { -> BucketItem(this.still.get(), Properties().craftRemainder(Items.BUCKET).stacksTo(1)) }

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