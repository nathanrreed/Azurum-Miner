package com.nred.azurum_miner.worldgen

import com.nred.azurum_miner.util.Helpers.azLoc
import com.nred.azurum_miner.util.OreHelper
import com.nred.azurum_miner.util.OreHelper.Companion.get
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest

object ModConfiguredFeatures {
    val AZURUM_ORE_KEY: ResourceKey<ConfiguredFeature<*, *>> = registerKey("azurum_ore")

    fun bootstrap(context: BootstrapContext<ConfiguredFeature<*, *>>) {
        val stoneReplaceables = TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES)
        val deepslateReplaceables = TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES)

        val azurumOres = listOf(OreConfiguration.target(stoneReplaceables, OreHelper.ORES["azurum"].ore.get().defaultBlockState()), OreConfiguration.target(deepslateReplaceables, OreHelper.ORES["azurum"].deepslate_ore.get().defaultBlockState()))

        register(context, AZURUM_ORE_KEY, Feature.ORE, OreConfiguration(azurumOres, 5))

    }

    fun registerKey(name: String): ResourceKey<ConfiguredFeature<*, *>> {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, azLoc(name))
    }

    fun <FC : FeatureConfiguration, F : Feature<FC>> register(context: BootstrapContext<ConfiguredFeature<*, *>>, key: ResourceKey<ConfiguredFeature<*, *>>, feature: F, configuration: FC) {
        context.register(key, ConfiguredFeature(feature, configuration))
    }
}