package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner
import net.minecraft.core.HolderLookup
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import java.util.concurrent.CompletableFuture

class EmiDefaultsProvider(val output: PackOutput, val registries: CompletableFuture<HolderLookup.Provider>) : DataProvider {
    val CODEC = ExtraCodecs.nonEmptyList(ResourceLocation.CODEC.listOf()).fieldOf("added").codec()
    val recipes = ArrayList<ResourceLocation>()
    override fun run(cachedOutput: CachedOutput): CompletableFuture<*> {
        return this.registries.thenCompose { lookupProvider ->
            addDefaults(lookupProvider)

            DataProvider.saveStable(cachedOutput, lookupProvider, CODEC, recipes, this.output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "recipe/defaults").json(ResourceLocation.fromNamespaceAndPath("emi", AzurumMiner.ID)))
        }
    }

    override fun getName(): String {
        return "EMI Defaults Provider: " + AzurumMiner.ID
    }

    fun addDefaults(lookupProvider: HolderLookup.Provider) {
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "/dimensional_matrix")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "azurum_block")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "azurum_from_smelting_azurum_ore")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "azurum_gear")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "azurum_ingot_from_infuser")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "azurum_nugget")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "complex_void_processor")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "conglomerate_of_ore")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "conglomerate_of_ore_block")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "conglomerate_of_ore_shard_from_ore")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "elaborate_void_processor")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "empty_dimensional_matrix")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "ender_diamond")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "ender_essence_from_end_rod")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "galibium_block")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "galibium_gear")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "galibium_ingot_from_smelting_galibium_ore")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "galibium_nugget")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "infuser_block")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "liquifier_block")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner_block_tier_1")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner_block_tier_2")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner_block_tier_3")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner_block_tier_4")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner_block_tier_5")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "molten_ore_from_tier1ore")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "nether_diamond")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "nether_essence_from_netherrack")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "palestium_block")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "palestium_gear")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "palestium_nugget")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "raw_galibium_block")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "raw_palestium_block")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "raw_thelxium_block")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "simple_void_processor")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "thelxium_block")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "thelxium_gear")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "thelxium_ingot_from_smelting_thelxium_ore")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "thelxium_nugget")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "transmogrifier_block")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "void_processor")
        recipes += ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "palestium_ingot_from_smelting_palestium_ore")

        recipes += ResourceLocation.withDefaultNamespace("crafter")
    }

}