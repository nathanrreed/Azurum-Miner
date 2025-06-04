package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.util.Helpers.azLoc
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
        recipes += azLoc("/dimensional_matrix")
        recipes += azLoc("azurum_block")
        recipes += azLoc("azurum_from_smelting_azurum_ore")
        recipes += azLoc("azurum_gear")
        recipes += azLoc("azurum_ingot_from_infuser")
        recipes += azLoc("azurum_nugget")
        recipes += azLoc("complex_void_processor")
        recipes += azLoc("conglomerate_of_ore")
        recipes += azLoc("conglomerate_of_ore_block")
        recipes += azLoc("conglomerate_of_ore_shard_from_ore")
        recipes += azLoc("elaborate_void_processor")
        recipes += azLoc("empty_dimensional_matrix")
        recipes += azLoc("ender_diamond")
        recipes += azLoc("ender_essence_from_end_rod")
        recipes += azLoc("galibium_block")
        recipes += azLoc("galibium_gear")
        recipes += azLoc("galibium_ingot_from_smelting_galibium_ore")
        recipes += azLoc("galibium_nugget")
        recipes += azLoc("infuser_block")
        recipes += azLoc("crystallizer_block")
        recipes += azLoc("liquifier_block")
        recipes += azLoc("miner_block_tier_1")
        recipes += azLoc("miner_block_tier_2")
        recipes += azLoc("miner_block_tier_3")
        recipes += azLoc("miner_block_tier_4")
        recipes += azLoc("miner_block_tier_5")
        recipes += azLoc("molten_ore_from_tier1ore")
        recipes += azLoc("nether_diamond")
        recipes += azLoc("nether_essence_from_netherrack")
        recipes += azLoc("palestium_block")
        recipes += azLoc("palestium_gear")
        recipes += azLoc("palestium_nugget")
        recipes += azLoc("raw_galibium_block")
        recipes += azLoc("raw_palestium_block")
        recipes += azLoc("raw_thelxium_block")
        recipes += azLoc("simple_void_processor")
        recipes += azLoc("generator")
        recipes += azLoc("simple_generator")
        recipes += azLoc("thelxium_block")
        recipes += azLoc("thelxium_gear")
        recipes += azLoc("thelxium_ingot_from_smelting_thelxium_ore")
        recipes += azLoc("thelxium_nugget")
        recipes += azLoc("transmogrifier_block")
        recipes += azLoc("void_processor")
        recipes += azLoc("palestium_ingot_from_smelting_palestium_ore")
        recipes += azLoc("energized_obsidian_block")
        recipes += azLoc("emerald_crystal_solution")
        recipes += azLoc("amethyst_crystal_solution")
        recipes += azLoc("diamond_crystal_solution")
        recipes += azLoc("quartz_crystal_solution")
        recipes += azLoc("prismarine_crystal_solution")
        recipes += azLoc("void_crystal")
        recipes += azLoc("generator_block")
        recipes += azLoc("simple_generator_block")
        recipes += azLoc("seed_crystal")
        recipes += azLoc("powdered_snow_from_snow_ball")
        recipes += azLoc("void_gun")
        recipes += azLoc("void_bullet")

        recipes += ResourceLocation.withDefaultNamespace("crafter")
    }
}