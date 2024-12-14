package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.block.ModBlocks.CONGLOMERATE_OF_ORE
import com.nred.azurum_miner.block.ModBlocks.CONGLOMERATE_OF_ORE_BLOCK
import com.nred.azurum_miner.datagen.ModItemTagProvider.Companion.oreTier1Tag
import com.nred.azurum_miner.datagen.ModItemTagProvider.Companion.oreTier2Tag
import com.nred.azurum_miner.datagen.ModItemTagProvider.Companion.oreTier3Tag
import com.nred.azurum_miner.datagen.ModItemTagProvider.Companion.oreTier4Tag
import com.nred.azurum_miner.datagen.ModItemTagProvider.Companion.oreTier5Tag
import com.nred.azurum_miner.fluid.ModFluids
import com.nred.azurum_miner.item.ModItems.COMPLEX_VOID_PROCESSOR
import com.nred.azurum_miner.item.ModItems.CONGLOMERATE_OF_ORE_SHARD
import com.nred.azurum_miner.item.ModItems.DIMENSIONAL_MATRIX
import com.nred.azurum_miner.item.ModItems.ELABORATE_VOID_PROCESSOR
import com.nred.azurum_miner.item.ModItems.EMPTY_DIMENSIONAL_MATRIX
import com.nred.azurum_miner.item.ModItems.ENDER_DIAMOND
import com.nred.azurum_miner.item.ModItems.NETHER_DIAMOND
import com.nred.azurum_miner.item.ModItems.SIMPLE_VOID_PROCESSOR
import com.nred.azurum_miner.item.ModItems.VOID_PROCESSOR
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.recipe.*
import com.nred.azurum_miner.util.FluidHelper.Companion.FLUIDS
import com.nred.azurum_miner.util.FluidHelper.Companion.get
import com.nred.azurum_miner.util.OreHelper.Companion.ORES
import com.nred.azurum_miner.util.OreHelper.Companion.get
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.*
import net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS
import net.minecraft.tags.ItemTags.SAPLINGS
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items.*
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.conditions.IConditionBuilder
import net.neoforged.neoforge.fluids.FluidStack
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) : RecipeProvider(output, registries), IConditionBuilder {

    fun <T : AbstractCookingRecipe> smelting(
        recipeOutput: RecipeOutput, pSmeltingSerializer: RecipeSerializer<T>, factory: AbstractCookingRecipe.Factory<T>, ingredients: List<ItemLike>, pCategory: RecipeCategory, pResult: ItemLike, pExp: Float, pSmeltTime: Int, pGroup: String, pRecipeName: String
    ) {
        for (itemLike in ingredients) {
            SimpleCookingRecipeBuilder.generic(
                Ingredient.of(itemLike), pCategory, pResult, pExp, pSmeltTime, pSmeltingSerializer, factory
            ).group(pGroup)
                .unlockedBy(getHasName(itemLike), has(itemLike))
                .save(
                    recipeOutput,
                    AzurumMiner.ID + ":" + getItemName(pResult) + "_" + pRecipeName + "_" + getItemName(itemLike)
                )
        }
    }

    private fun oreSmelting(recipeOutput: RecipeOutput, ingredients: List<ItemLike>, pCategory: RecipeCategory, pResult: ItemLike, pExp: Float, pSmeltTime: Int, pGroup: String, blast: Boolean) {
        smelting(recipeOutput, RecipeSerializer.SMELTING_RECIPE, ::SmeltingRecipe, ingredients, pCategory, pResult, pExp, pSmeltTime, pGroup, "from_smelting")
        if (blast) {
            smelting(recipeOutput, RecipeSerializer.BLASTING_RECIPE, ::BlastingRecipe, ingredients, pCategory, pResult, pExp, pSmeltTime / 2, pGroup, "from_blasting")
        }
    }

    override fun buildRecipes(recipeOutput: RecipeOutput) {
        for (ore in ORES) {
            if (ore.isOre && !ore.isGem) {
                oreSmelting(recipeOutput, listOf(ore.raw!!, ore.ore, ore.deepslate_ore), RecipeCategory.MISC, ore.ingot!!, 0.25f, 200, ore.ore_name, true)
                ShapelessRecipeBuilder.shapeless(BUILDING_BLOCKS, ore.block, 1).requires(ore.ingot!!, 9).unlockedBy(getHasName(ore.ingot!!), has(ore.ingot!!)).save(recipeOutput)
                ShapelessRecipeBuilder.shapeless(BUILDING_BLOCKS, ore.ingot!!, 9).requires(ore.block, 1).unlockedBy(getHasName(ore.block), has(ore.block)).save(recipeOutput, AzurumMiner.ID + ":" + ore.ore_name + "_ingot_from_" + ore.ore_name + "_block")
            }

            if (ore.isOre) {
                ShapelessRecipeBuilder.shapeless(BUILDING_BLOCKS, ore.ingot!!, 1).requires(ore.nugget!!, 9).unlockedBy(getHasName(ore.nugget!!), has(ore.nugget!!)).save(recipeOutput, AzurumMiner.ID + ":" + ore.ore_name + "_ingot_from_" + ore.ore_name + "_nuggets")
                ShapelessRecipeBuilder.shapeless(BUILDING_BLOCKS, ore.nugget!!, 9).requires(ore.ingot!!, 1).unlockedBy(getHasName(ore.ingot!!), has(ore.ingot!!)).save(recipeOutput)
            }

            if (ore.isGem) {
                ShapelessRecipeBuilder.shapeless(BUILDING_BLOCKS, ore.block, 1).requires(ore.gem!!, 9).unlockedBy(getHasName(ore.gem!!), has(ore.gem!!)).save(recipeOutput)
                ShapelessRecipeBuilder.shapeless(BUILDING_BLOCKS, ore.gem!!, 9).requires(ore.block, 1).unlockedBy(getHasName(ore.block), has(ore.block)).save(recipeOutput)
                oreSmelting(recipeOutput, listOf(ore.ore, ore.deepslate_ore), RecipeCategory.MISC, ore.gem!!, 0.25f, 200, ore.ore_name, true)
            }

            if (ore.isOre && !ore.isGem) {
                ShapelessRecipeBuilder.shapeless(BUILDING_BLOCKS, ore.raw_block!!, 1).requires(ore.raw!!, 9).unlockedBy(getHasName(ore.raw!!), has(ore.raw!!)).save(recipeOutput)
                ShapelessRecipeBuilder.shapeless(BUILDING_BLOCKS, ore.raw!!, 9).requires(ore.raw_block!!, 1).unlockedBy(getHasName(ore.raw_block!!), has(ore.raw_block!!)).save(recipeOutput)
            }
        }

        ShapelessRecipeBuilder.shapeless(BUILDING_BLOCKS, CONGLOMERATE_OF_ORE_BLOCK, 1).requires(CONGLOMERATE_OF_ORE_SHARD, 9).unlockedBy(getHasName(CONGLOMERATE_OF_ORE_SHARD), has(CONGLOMERATE_OF_ORE_SHARD)).save(recipeOutput)
        ShapelessRecipeBuilder.shapeless(BUILDING_BLOCKS, CONGLOMERATE_OF_ORE_SHARD, 9).requires(CONGLOMERATE_OF_ORE_BLOCK, 1).unlockedBy(getHasName(CONGLOMERATE_OF_ORE_BLOCK), has(CONGLOMERATE_OF_ORE_BLOCK)).save(recipeOutput, AzurumMiner.ID + ":conglomerate_of_ore_shard_from_block")

        LiquifierRecipeBuilder(FluidStack(FLUIDS["molten_ore"].still, 25), Ingredient.of(oreTier1Tag), 5000, 50).unlockedBy("has_ore", has(Tags.Items.ORES)).save(recipeOutput, AzurumMiner.ID + ":molten_ore_from_tier1ore")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["molten_ore"].still, 50), Ingredient.of(oreTier2Tag), 5000, 35).unlockedBy("has_ore", has(Tags.Items.ORES)).save(recipeOutput, AzurumMiner.ID + ":molten_ore_from_tier2ore")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["molten_ore"].still, 75), Ingredient.of(oreTier3Tag), 5000, 30).unlockedBy("has_ore", has(Tags.Items.ORES)).save(recipeOutput, AzurumMiner.ID + ":molten_ore_from_tier3ore")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["molten_ore"].still, 100), Ingredient.of(oreTier4Tag), 5000, 25).unlockedBy("has_ore", has(Tags.Items.ORES)).save(recipeOutput, AzurumMiner.ID + ":molten_ore_from_tier4ore")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["molten_ore"].still, 250), Ingredient.of(oreTier5Tag), 5000, 20).unlockedBy("has_ore", has(Tags.Items.ORES)).save(recipeOutput, AzurumMiner.ID + ":molten_ore_from_tier5ore")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["nether_essence"].still, 5), Ingredient.of(NETHERRACK, NETHER_BRICK), 5000, 20).unlockedBy(getHasName(NETHERRACK), has(NETHERRACK)).unlockedBy(getHasName(NETHER_BRICK), has(NETHER_BRICK)).save(recipeOutput, AzurumMiner.ID + ":nether_essence_from_netherrack")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["nether_essence"].still, 20), Ingredient.of(NETHER_BRICKS, NETHER_WART), 5000, 20).unlockedBy(getHasName(NETHER_BRICKS), has(NETHER_BRICKS)).unlockedBy(getHasName(NETHER_WART), has(NETHER_WART)).save(recipeOutput, AzurumMiner.ID + ":nether_essence_from_nether_bricks")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["nether_essence"].still, 180), Ingredient.of(NETHER_WART_BLOCK), 5000, 20).unlockedBy(getHasName(NETHER_WART_BLOCK), has(NETHER_WART_BLOCK)).save(recipeOutput, AzurumMiner.ID + ":nether_essence_from_nether_wart_block")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["nether_essence"].still, 250), Ingredient.of(BLAZE_ROD), 5000, 20).unlockedBy(getHasName(BLAZE_ROD), has(BLAZE_ROD)).save(recipeOutput, AzurumMiner.ID + ":nether_essence_from_nether_blaze_rod")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["nether_essence"].still, 125), Ingredient.of(BLAZE_POWDER), 5000, 20).unlockedBy(getHasName(BLAZE_POWDER), has(BLAZE_POWDER)).save(recipeOutput, AzurumMiner.ID + ":nether_essence_from_blaze_powder")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["ender_essence"].still, 5), Ingredient.of(END_STONE, END_STONE_BRICKS), 5000, 20).unlockedBy(getHasName(END_STONE), has(END_STONE)).unlockedBy(getHasName(END_STONE_BRICKS), has(END_STONE_BRICKS)).save(recipeOutput, AzurumMiner.ID + ":ender_essence_from_end_stone")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["ender_essence"].still, 50), Ingredient.of(END_ROD, ENDER_PEARL), 5000, 20).unlockedBy(getHasName(END_ROD), has(END_ROD)).unlockedBy(getHasName(ENDER_PEARL), has(ENDER_PEARL)).save(recipeOutput, AzurumMiner.ID + ":ender_essence_from_end_rod")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["ender_essence"].still, 150), Ingredient.of(ENDER_EYE), 5000, 20).unlockedBy(getHasName(ENDER_EYE), has(ENDER_EYE)).save(recipeOutput, AzurumMiner.ID + ":ender_essence_from_ender_eye")
        LiquifierRecipeBuilder(FluidStack(FLUIDS["ender_essence"].still, 1000), Ingredient.of(END_CRYSTAL), 5000, 20).unlockedBy(getHasName(END_CRYSTAL), has(END_CRYSTAL)).save(recipeOutput, AzurumMiner.ID + ":ender_essence_from_end_crystal")

        InfuserRecipeBuilder(ItemStack(ORES["azurum"].ingot!!.asItem(), 1), Ingredient.of(ORES["azurum"].gem), Ingredient.of(IRON_INGOT), FluidStack(Fluids.LAVA, 1000), 5000, 200).save(recipeOutput, AzurumMiner.ID + ":azurum_ingot_from_infuser")
        InfuserRecipeBuilder(ItemStack(OBSIDIAN, 1), Ingredient.of(ICE), Ingredient.EMPTY, FluidStack(Fluids.LAVA, 1000), 5000, 200).save(recipeOutput, AzurumMiner.ID + ":obsidian_from_infuser")
        InfuserRecipeBuilder(ItemStack(OBSIDIAN, 9), Ingredient.of(ICE), Ingredient.of(BLUE_ICE), FluidStack(Fluids.LAVA, 1000), 10000, 600).save(recipeOutput, AzurumMiner.ID + ":multi_obsidian_from_infuser")

        InfuserRecipeBuilder(ItemStack(NETHER_DIAMOND.asItem(), 1), Ingredient.of(BLAZE_POWDER), Ingredient.of(DIAMOND), FluidStack(FLUIDS["nether_essence"].still.get(), 1000), 5000, 200).save(recipeOutput)
        InfuserRecipeBuilder(ItemStack(ENDER_DIAMOND.asItem(), 1), Ingredient.of(ENDER_PEARL), Ingredient.of(DIAMOND), FluidStack(FLUIDS["ender_essence"].still.get(), 1000), 5000, 200).save(recipeOutput)
        InfuserRecipeBuilder(ItemStack(ENDER_EYE, 1), Ingredient.of(ENDER_PEARL), Ingredient.EMPTY, FluidStack(FLUIDS["nether_essence"].still.get(), 500), 5000, 200).save(recipeOutput, AzurumMiner.ID + ":ender_eye_from_infuser")
        InfuserRecipeBuilder(ItemStack(END_STONE, 1), Ingredient.of(STONE), Ingredient.EMPTY, FluidStack(FLUIDS["ender_essence"].still.get(), 250), 5000, 20).save(recipeOutput, AzurumMiner.ID + ":end_stone_from_infuser")
        InfuserRecipeBuilder(ItemStack(END_STONE_BRICKS, 1), Ingredient.of(STONE_BRICKS), Ingredient.EMPTY, FluidStack(FLUIDS["ender_essence"].still.get(), 250), 5000, 20).save(recipeOutput, AzurumMiner.ID + ":end_stone_bricks_from_infuser")
        InfuserRecipeBuilder(ItemStack(NETHERRACK, 1), Ingredient.of(COBBLESTONE), Ingredient.EMPTY, FluidStack(FLUIDS["nether_essence"].still.get(), 50), 5000, 20).save(recipeOutput, AzurumMiner.ID + ":netherrack_from_infuser")
        InfuserRecipeBuilder(ItemStack(SOUL_SAND, 1), Ingredient.of(SAND), Ingredient.EMPTY, FluidStack(FLUIDS["nether_essence"].still.get(), 150), 5000, 20).save(recipeOutput, AzurumMiner.ID + ":soul_sand_from_infuser")
        InfuserRecipeBuilder(ItemStack(SOUL_SOIL, 1), Ingredient.of(RED_SAND), Ingredient.EMPTY, FluidStack(FLUIDS["nether_essence"].still.get(), 150), 5000, 20).save(recipeOutput, AzurumMiner.ID + ":soul_soil_from_infuser")
        InfuserRecipeBuilder(ItemStack(MAGMA_BLOCK, 1), Ingredient.of(DEEPSLATE), Ingredient.EMPTY, FluidStack(FLUIDS["nether_essence"].still.get(), 1000), 5000, 60).save(recipeOutput, AzurumMiner.ID + ":magma_block_from_infuser")
        InfuserRecipeBuilder(ItemStack(SOUL_TORCH, 1), Ingredient.of(TORCH), Ingredient.EMPTY, FluidStack(FLUIDS["nether_essence"].still.get(), 200), 5000, 40).save(recipeOutput, AzurumMiner.ID + ":soul_torch_from_infuser")
        InfuserRecipeBuilder(ItemStack(SOUL_LANTERN, 1), Ingredient.of(LANTERN), Ingredient.EMPTY, FluidStack(FLUIDS["nether_essence"].still.get(), 200), 5000, 40).save(recipeOutput, AzurumMiner.ID + ":soul_lantern_from_infuser")
        InfuserRecipeBuilder(ItemStack(SOUL_CAMPFIRE, 1), Ingredient.of(CAMPFIRE), Ingredient.EMPTY, FluidStack(FLUIDS["nether_essence"].still.get(), 200), 5000, 40).save(recipeOutput, AzurumMiner.ID + ":soul_campfire_from_infuser")
        InfuserRecipeBuilder(ItemStack(CONGLOMERATE_OF_ORE.asItem(), 1), Ingredient.of(ORES["azurum"].nugget), Ingredient.of(STONE), FluidStack(FLUIDS["molten_ore"].still.get(), 1000), 5000, 200).save(recipeOutput)

        InfuserRecipeBuilder(ItemStack(BLUE_ICE.asItem(), 1), Ingredient.of(PACKED_ICE), Ingredient.EMPTY, FluidStack(ModFluids.snow_still, 1000), 50000, 2000).save(recipeOutput)
        InfuserRecipeBuilder(ItemStack(PACKED_ICE.asItem(), 1), Ingredient.of(ICE), Ingredient.EMPTY, FluidStack(ModFluids.snow_still, 1000), 50000, 2000).save(recipeOutput)
        InfuserRecipeBuilder(ItemStack(ICE.asItem(), 1), Ingredient.of(SNOW_BLOCK), Ingredient.EMPTY, FluidStack(ModFluids.snow_still, 1000), 50000, 2000).save(recipeOutput)

        TransmogrifierRecipeBuilder(ItemStack(RED_SAND, 1), Ingredient.of(SAND), 50000, 60).save(recipeOutput)
        TransmogrifierRecipeBuilder(ItemStack(CALCITE, 1), Ingredient.of(DIORITE), 50000, 60).save(recipeOutput)
        TransmogrifierRecipeBuilder(ItemStack(TUFF, 1), Ingredient.of(ANDESITE), 50000, 60).save(recipeOutput)
        TransmogrifierRecipeBuilder(ItemStack(BASALT, 1), Ingredient.of(DEEPSLATE), 100000, 180).save(recipeOutput)
        TransmogrifierRecipeBuilder(ItemStack(GILDED_BLACKSTONE, 1), Ingredient.of(BLACKSTONE), 100000, 1200).save(recipeOutput)
        TransmogrifierRecipeBuilder(ItemStack(PACKED_MUD, 1), Ingredient.of(GRANITE), 80000, 120).save(recipeOutput)
        TransmogrifierRecipeBuilder(ItemStack(SPONGE, 1), Ingredient.of(STRIPPED_BAMBOO_BLOCK), 500000, 2400).save(recipeOutput)
        TransmogrifierRecipeBuilder(ItemStack(BUDDING_AMETHYST, 1), Ingredient.of(ItemStack(AMETHYST_BLOCK, 8)), 50000, 2400).save(recipeOutput)
        TransmogrifierRecipeBuilder(ItemStack(DEAD_BUSH, 1), Ingredient.of(SAPLINGS), 100000, 60).save(recipeOutput)
        TransmogrifierRecipeBuilder(ItemStack(OXIDIZED_COPPER, 1), Ingredient.of(COPPER_BLOCK), 100000, 360).save(recipeOutput)
        TransmogrifierRecipeBuilder(ItemStack(CRYING_OBSIDIAN, 1), Ingredient.of(OBSIDIAN), 1000000, 3600).save(recipeOutput)
        TransmogrifierRecipeBuilder(ItemStack(CONGLOMERATE_OF_ORE_SHARD.get(), 2), Ingredient.of(CONGLOMERATE_OF_ORE), 2000000, 1200).save(recipeOutput, AzurumMiner.ID + ":conglomerate_of_ore_shard_from_ore")

        MinerRecipeBuilder(Ingredient.of(oreTier1Tag), 1).save(recipeOutput, AzurumMiner.ID + ":ore_from_miner_tier1")
        MinerRecipeBuilder(Ingredient.of(oreTier2Tag), 2).save(recipeOutput, AzurumMiner.ID + ":ore_from_miner_tier2")
        MinerRecipeBuilder(Ingredient.of(oreTier3Tag), 3).save(recipeOutput, AzurumMiner.ID + ":ore_from_miner_tier3")
        MinerRecipeBuilder(Ingredient.of(oreTier4Tag), 4).save(recipeOutput, AzurumMiner.ID + ":ore_from_miner_tier4")
        MinerRecipeBuilder(Ingredient.of(oreTier5Tag), 5).save(recipeOutput, AzurumMiner.ID + ":ore_from_miner_tier5")

        for (ore in listOf("azurum", "galibium", "thelxium", "palestium").map { ORES[it] }) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ore.gear!!).pattern(" I ").pattern("IBI").pattern(" I ")
                .define('I', ore.ingot!!).define('B', ore.block)
                .unlockedBy(getHasName(ore.ingot!!), has(ore.ingot!!)).save(recipeOutput)
        }

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SIMPLE_VOID_PROCESSOR).pattern(" C ").pattern("GPG").pattern(" C ")
            .define('C', ORES["azurum"].gem!!).define('G', GLASS).define('P', ENDER_PEARL)
            .unlockedBy(getHasName(ORES["azurum"].gem!!), has(ORES["azurum"].gem!!)).save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EMPTY_DIMENSIONAL_MATRIX).pattern("AEA").pattern("NPN").pattern("AEA")
            .define('P', ENDER_EYE).define('A', ORES["azurum"].nugget!!).define('N', NETHER_DIAMOND).define('E', ENDER_DIAMOND)
            .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, VOID_PROCESSOR).pattern("SLS").pattern("RPR").pattern("GAG")
            .define('G', ORES["galibium"].gear!!).define('P', SIMPLE_VOID_PROCESSOR).define('A', ORES["azurum"].gear!!).define('R', REDSTONE).define('L', LAPIS_LAZULI).define('S', GLOWSTONE_DUST)
            .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ELABORATE_VOID_PROCESSOR).pattern("RLR").pattern("PGP").pattern("GDG")
            .define('G', ORES["thelxium"].gear!!).define('P', VOID_PROCESSOR).define('D', DIAMOND).define('L', REDSTONE_LAMP).define('R', REDSTONE)
            .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, COMPLEX_VOID_PROCESSOR).pattern("FGF").pattern("DCD").pattern("PGP")
            .define('G', ORES["palestium"].gear!!).define('P', ELABORATE_VOID_PROCESSOR).define('F', ORES["thelxium"].gear!!).define('C', CRAFTER).define('D', CONGLOMERATE_OF_ORE_SHARD)
            .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModMachines.INFUSER).pattern("BVB").pattern("PCP").pattern("OVO")
            .define('V', SIMPLE_VOID_PROCESSOR).define('C', CAULDRON).define('P', PISTON).define('B', COPPER_BLOCK).define('O', OBSIDIAN)
            .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModMachines.LIQUIFIER).pattern("OAO").pattern("GFG").pattern("OPO")
            .define('A', ANVIL).define('O', OBSIDIAN).define('F', BLAST_FURNACE).define('P', SIMPLE_VOID_PROCESSOR).define('G', TINTED_GLASS)
            .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModMachines.TRANSMOGRIFIER).pattern("NED").pattern("MCM").pattern("GVG")
            .define('V', VOID_PROCESSOR).define('G', ORES["galibium"].gear!!).define('M', DIMENSIONAL_MATRIX).define('E', ENDER_EYE).define('C', CONGLOMERATE_OF_ORE).define('N', NETHER_DIAMOND).define('D', ENDER_DIAMOND)
            .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModMachines.MINER_BLOCK_TIERS[0]).pattern("CPC").pattern("PDP").pattern("CPC")
            .define('C', COPPER_BLOCK).define('D', DIAMOND).define('P', SIMPLE_VOID_PROCESSOR)
            .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(recipeOutput)

        ShapedRecipeBuilderTransform(RecipeCategory.MISC, ModMachines.MINER_BLOCK_TIERS[1], 1, 4).pattern("IPI").pattern("DMD").pattern("IPI")
            .define('I', IRON_BLOCK).define('D', NETHER_DIAMOND).define('P', SIMPLE_VOID_PROCESSOR).define('M', ModMachines.MINER_BLOCK_TIERS[0])
            .unlockedBy(getHasName(ModMachines.MINER_BLOCK_TIERS[0]), has(ModMachines.MINER_BLOCK_TIERS[0])).save(recipeOutput)

        ShapedRecipeBuilderTransform(RecipeCategory.MISC, ModMachines.MINER_BLOCK_TIERS[2], 1, 4).pattern("SGS").pattern("DMD").pattern("SPS")
            .define('S', CONGLOMERATE_OF_ORE_SHARD).define('D', ENDER_DIAMOND).define('P', VOID_PROCESSOR).define('G', ORES["galibium"].gear!!).define('M', ModMachines.MINER_BLOCK_TIERS[1])
            .unlockedBy(getHasName(ModMachines.MINER_BLOCK_TIERS[1]), has(ModMachines.MINER_BLOCK_TIERS[1])).save(recipeOutput)

        ShapedRecipeBuilderTransform(RecipeCategory.MISC, ModMachines.MINER_BLOCK_TIERS[3], 1, 4).pattern("GDG").pattern("SMS").pattern("GPG")
            .define('S', CONGLOMERATE_OF_ORE_SHARD).define('D', DIAMOND_BLOCK).define('P', ELABORATE_VOID_PROCESSOR).define('G', ORES["thelxium"].gear!!).define('M', ModMachines.MINER_BLOCK_TIERS[2])
            .unlockedBy(getHasName(ModMachines.MINER_BLOCK_TIERS[2]), has(ModMachines.MINER_BLOCK_TIERS[2])).save(recipeOutput)

        ShapedRecipeBuilderTransform(RecipeCategory.MISC, ModMachines.MINER_BLOCK_TIERS[4], 1, 4).pattern("BPB").pattern("DMD").pattern("GPG")
            .define('B', CONGLOMERATE_OF_ORE_BLOCK).define('D', DIMENSIONAL_MATRIX).define('P', COMPLEX_VOID_PROCESSOR).define('G', ORES["palestium"].gear!!).define('M', ModMachines.MINER_BLOCK_TIERS[3])
            .unlockedBy(getHasName(ModMachines.MINER_BLOCK_TIERS[3]), has(ModMachines.MINER_BLOCK_TIERS[3])).save(recipeOutput)
    }
}