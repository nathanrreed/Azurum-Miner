package com.nred.azurum_miner.worldgen

import com.nred.azurum_miner.AzurumMiner
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BiomeTags
import net.minecraft.world.level.levelgen.GenerationStep
import net.neoforged.neoforge.common.world.BiomeModifier
import net.neoforged.neoforge.common.world.BiomeModifiers
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModBiomeModifiers {
    val ADD_AZURUM_ORE = registerKey("add_azurum_ore")

    fun bootstrap(context: BootstrapContext<BiomeModifier>) {
        val placedFeature = context.lookup(Registries.PLACED_FEATURE)
        val biomes = context.lookup(Registries.BIOME)

        context.register(ADD_AZURUM_ORE, BiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD), HolderSet.direct(placedFeature.getOrThrow(ModPlacedFeatures.AZURUM_ORE_PLACED_KEY)), GenerationStep.Decoration.UNDERGROUND_ORES))
    }

    fun registerKey(name: String): ResourceKey<BiomeModifier> {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, name))
    }
}