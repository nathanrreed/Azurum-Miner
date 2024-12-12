package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.worldgen.ModBiomeModifiers
import com.nred.azurum_miner.worldgen.ModConfiguredFeatures
import com.nred.azurum_miner.worldgen.ModPlacedFeatures
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.concurrent.CompletableFuture

class ModDatapackProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) : DatapackBuiltinEntriesProvider(output, registries, BUILDER, setOf(AzurumMiner.ID)) {
    companion object {
        val BUILDER = RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
    }
}