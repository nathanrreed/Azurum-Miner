package com.nred.azurum_miner.compat.jei

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.datagen.ModItemTagProvider.Companion.oreTierTag
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.crystallizer.CrystallizerMenu
import com.nred.azurum_miner.machine.generator.GeneratorMenu
import com.nred.azurum_miner.machine.infuser.InfuserMenu
import com.nred.azurum_miner.machine.liquifier.LiquifierMenu
import com.nred.azurum_miner.machine.miner.Miner
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierMenu
import com.nred.azurum_miner.recipe.*
import com.nred.azurum_miner.recipe.ModRecipe.CRYSTALLIZER_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.GENERATOR_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.INFUSER_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.LIQUIFIER_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.MINER_TIER1_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.MINER_TIER2_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.MINER_TIER3_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.MINER_TIER4_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.MINER_TIER5_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.SHAPED_RECIPE_TRANSFORM_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.TRANSMOGRIFIER_RECIPE_TYPE
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.ModMenuTypes
import com.nred.azurum_miner.util.Helpers.azLoc
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.ingredients.ITypedIngredient
import mezz.jei.api.recipe.IRecipeManager
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin
import mezz.jei.api.registration.*
import mezz.jei.api.runtime.IJeiRuntime
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.crafting.RecipeHolder

@JeiPlugin
class JEIPlugin : IModPlugin {
    lateinit var recipeManager: IRecipeManager
    override fun getPluginUid(): ResourceLocation {
        return azLoc("jei_plugin")
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        val recipeManager = Minecraft.getInstance().level!!.recipeManager

        registration.addRecipeCategories(CrystallizerCategory(registration.jeiHelpers.guiHelper))
        registration.addRecipeCategories(LiquifierCategory(registration.jeiHelpers.guiHelper))
        registration.addRecipeCategories(InfuserCategory(registration.jeiHelpers.guiHelper))
        registration.addRecipeCategories(TransmogrifierCategory(registration.jeiHelpers.guiHelper))
        registration.addRecipeCategories(GeneratorCategory(registration.jeiHelpers.guiHelper, recipeManager.getAllRecipesFor(GENERATOR_RECIPE_TYPE.get()).filter { it.value.typeName == "base" }.map { it.value }))
        registration.addRecipeCategories(MinerCategory(registration.jeiHelpers.guiHelper, 1), MinerCategory(registration.jeiHelpers.guiHelper, 2), MinerCategory(registration.jeiHelpers.guiHelper, 3), MinerCategory(registration.jeiHelpers.guiHelper, 4), MinerCategory(registration.jeiHelpers.guiHelper, 5))
    }


    override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        registration.addRecipeTransferHandler(GeneratorMenu::class.java, ModMenuTypes.GENERATOR_MENU.get(), InfuserCategory.TYPE, 0, 4, 4, 36)
        registration.addRecipeTransferHandler(CrystallizerMenu::class.java, ModMenuTypes.CRYSTALLIZER_MENU.get(), InfuserCategory.TYPE, 0, 1, 1, 36)
        registration.addRecipeTransferHandler(InfuserMenu::class.java, ModMenuTypes.INFUSER_MENU.get(), InfuserCategory.TYPE, 0, 3, 3, 36)
        registration.addRecipeTransferHandler(LiquifierMenu::class.java, ModMenuTypes.LIQUIFIER_MENU.get(), LiquifierCategory.TYPE, 0, 1, 1, 36)
        registration.addRecipeTransferHandler(TransmogrifierMenu::class.java, ModMenuTypes.TRANSMOGRIFIER_MENU.get(), TransmogrifierCategory.TYPE, 0, 2, 2, 36)
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalysts(LiquifierCategory.TYPE, ModMachines.LIQUIFIER)
        registration.addRecipeCatalysts(InfuserCategory.TYPE, ModMachines.INFUSER)
        registration.addRecipeCatalysts(TransmogrifierCategory.TYPE, ModMachines.TRANSMOGRIFIER)
        registration.addRecipeCatalysts(GeneratorCategory.TYPE, ModMachines.GENERATOR)
        registration.addRecipeCatalysts(CrystallizerCategory.TYPE, ModMachines.CRYSTALLIZER)

        registration.addRecipeCatalysts(MinerCategory.TYPE_TIER1, ModMachines.MINER_BLOCK_TIERS[0], ModMachines.MINER_BLOCK_TIERS[1], ModMachines.MINER_BLOCK_TIERS[2], ModMachines.MINER_BLOCK_TIERS[3], ModMachines.MINER_BLOCK_TIERS[4])
        registration.addRecipeCatalysts(MinerCategory.TYPE_TIER2, ModMachines.MINER_BLOCK_TIERS[1], ModMachines.MINER_BLOCK_TIERS[2], ModMachines.MINER_BLOCK_TIERS[3], ModMachines.MINER_BLOCK_TIERS[4])
        registration.addRecipeCatalysts(MinerCategory.TYPE_TIER3, ModMachines.MINER_BLOCK_TIERS[2], ModMachines.MINER_BLOCK_TIERS[3], ModMachines.MINER_BLOCK_TIERS[4])
        registration.addRecipeCatalysts(MinerCategory.TYPE_TIER4, ModMachines.MINER_BLOCK_TIERS[3], ModMachines.MINER_BLOCK_TIERS[4])
        registration.addRecipeCatalysts(MinerCategory.TYPE_TIER5, ModMachines.MINER_BLOCK_TIERS[4])
    }

    override fun registerAdvanced(registration: IAdvancedRegistration) {
        val data = listOf(Triple(MINER_TIER1_RECIPE_TYPE.get(), MinerCategory.TYPE_TIER1, oreTierTag[0]), Triple(MINER_TIER2_RECIPE_TYPE.get(), MinerCategory.TYPE_TIER2, oreTierTag[1]), Triple(MINER_TIER3_RECIPE_TYPE.get(), MinerCategory.TYPE_TIER3, oreTierTag[2]), Triple(MINER_TIER4_RECIPE_TYPE.get(), MinerCategory.TYPE_TIER4, oreTierTag[3]), Triple(MINER_TIER5_RECIPE_TYPE.get(), MinerCategory.TYPE_TIER5, oreTierTag[4]))
        for ((idx, tier) in data.withIndex()) {
            registration.addTypedRecipeManagerPlugin(RecipeType.createRecipeHolderType(tier.second.uid), object : ISimpleRecipeManagerPlugin<RecipeHolder<MinerRecipe>> {
                override fun isHandledInput(input: ITypedIngredient<*>): Boolean {
                    return (input.itemStack.get().item as BlockItem).block is Miner && idx == ((input.itemStack.get().item as BlockItem).block as Miner).tier
                }

                override fun isHandledOutput(output: ITypedIngredient<*>): Boolean {
                    return (output.itemStack.get().`is`(tier.third))
                }

                override fun getRecipesForInput(input: ITypedIngredient<*>): MutableList<RecipeHolder<MinerRecipe>> {
                    val recipes = ArrayList<RecipeHolder<MinerRecipe>>()
                    for (i in 0..idx) {
                        recipes.addAll(this@JEIPlugin.getRecipes(data[i].first))
                    }
                    recipes.addAll(this@JEIPlugin.getRecipes(data[idx].first))
                    return recipes
                }

                override fun getRecipesForOutput(output: ITypedIngredient<*>): MutableList<RecipeHolder<MinerRecipe>> {
                    return this@JEIPlugin.getRecipes(tier.first)
                }

                override fun getAllRecipes(): MutableList<RecipeHolder<MinerRecipe>> {
                    return mutableListOf()
                }
            })
        }
    }

    fun getRecipes(recipeType: net.minecraft.world.item.crafting.RecipeType<MinerRecipe>): MutableList<RecipeHolder<MinerRecipe>> {
        return Minecraft.getInstance().level!!.recipeManager.getAllRecipesFor(recipeType).toMutableList()
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val recipeManager = Minecraft.getInstance().level!!.recipeManager
        registration.addRecipes(LiquifierCategory.TYPE, recipeManager.getAllRecipesFor(LIQUIFIER_RECIPE_TYPE.get()).stream().map(RecipeHolder<LiquifierRecipe>::value).toList())
        registration.addRecipes(InfuserCategory.TYPE, recipeManager.getAllRecipesFor(INFUSER_RECIPE_TYPE.get()).stream().map(RecipeHolder<InfuserRecipe>::value).toList())
        registration.addRecipes(TransmogrifierCategory.TYPE, recipeManager.getAllRecipesFor(TRANSMOGRIFIER_RECIPE_TYPE.get()).stream().map(RecipeHolder<TransmogrifierRecipe>::value).toList())
        registration.addRecipes(CrystallizerCategory.TYPE, recipeManager.getAllRecipesFor(CRYSTALLIZER_RECIPE_TYPE.get()).stream().map(RecipeHolder<CrystallizerRecipe>::value).toList())
        registration.addRecipes(GeneratorCategory.TYPE, recipeManager.getAllRecipesFor(GENERATOR_RECIPE_TYPE.get()).stream().filter { it.value.typeName == "fuel" }.map(RecipeHolder<GeneratorRecipe>::value).toList())

        val crafting = RecipeType(BuiltInRegistries.RECIPE_TYPE.getKey(net.minecraft.world.item.crafting.RecipeType.CRAFTING)!!, RecipeHolder::class.java)
        registration.addRecipes(crafting, recipeManager.getAllRecipesFor(SHAPED_RECIPE_TRANSFORM_TYPE.get()).toList())

        registration.addIngredientInfo(ModItems.DIMENSIONAL_MATRIX, Component.translatable("jei.info.azurum_miner.dimensional_matrix"))
        registration.addIngredientInfo(ModMachines.SIMPLE_GENERATOR, Component.translatable("jei.info.azurum_miner.simple_generator", getFE(CONFIG.getOptional<Int>("simple_generator.energyProduction").get())))
    }

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {
        this.recipeManager = jeiRuntime.recipeManager
        super.onRuntimeAvailable(jeiRuntime)
    }
}