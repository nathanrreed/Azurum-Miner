package com.nred.azurum_miner.worldgen

import com.nred.azurum_miner.util.Helpers.azLoc
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.placement.*

object ModPlacedFeatures {

    private fun orePlacement(countPlacement: PlacementModifier, heightRange: PlacementModifier): List<PlacementModifier> {
        return listOf(countPlacement, InSquarePlacement.spread(), heightRange, BiomeFilter.biome())
    }

//    private fun commonOrePlacement(count: Int, heightRange: PlacementModifier): List<PlacementModifier> {
//        return orePlacement(CountPlacement.of(count), heightRange)
//    }

    private fun rareOrePlacement(chance: Int, heightRange: PlacementModifier): List<PlacementModifier> {
        return orePlacement(RarityFilter.onAverageOnceEvery(chance), heightRange)
    }

    val AZURUM_ORE_PLACED_KEY = registerKey("azurum_ore_placed")

    fun bootstrap(context: BootstrapContext<PlacedFeature>) {
        val configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE)

        register(context, AZURUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.AZURUM_ORE_KEY), rareOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(4))))
    }

    private fun registerKey(name: String): ResourceKey<PlacedFeature> {
        return ResourceKey.create(Registries.PLACED_FEATURE, azLoc(name))
    }

    fun register(context: BootstrapContext<PlacedFeature>, key: ResourceKey<PlacedFeature>, configuration: Holder<ConfiguredFeature<*, *>>, modifiers: List<PlacementModifier>) {
        context.register(key, PlacedFeature(configuration, modifiers.toList()))
    }
}