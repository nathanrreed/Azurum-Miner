package com.nred.azurum_miner.compat.jei

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.datagen.ModItemTagProvider.Companion.oreTier1Tag
import com.nred.azurum_miner.datagen.ModItemTagProvider.Companion.oreTier2Tag
import com.nred.azurum_miner.datagen.ModItemTagProvider.Companion.oreTier3Tag
import com.nred.azurum_miner.datagen.ModItemTagProvider.Companion.oreTier4Tag
import com.nred.azurum_miner.datagen.ModItemTagProvider.Companion.oreTier5Tag
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.infuser.InfuserScreen
import com.nred.azurum_miner.machine.liquifier.LiquifierScreen
import com.nred.azurum_miner.machine.miner.Miner
import com.nred.azurum_miner.machine.miner.MinerScreen
import com.nred.azurum_miner.recipe.*
import com.nred.azurum_miner.recipe.ModRecipe.INFUSER_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.LIQUIFIER_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.MINER_TIER1_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.MINER_TIER2_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.MINER_TIER3_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.MINER_TIER4_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.MINER_TIER5_RECIPE_TYPE
import com.nred.azurum_miner.recipe.ModRecipe.SHAPED_RECIPE_TRANSFORM_TYPE
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.gui.handlers.IGuiClickableArea
import mezz.jei.api.gui.handlers.IGuiContainerHandler
import mezz.jei.api.ingredients.ITypedIngredient
import mezz.jei.api.recipe.IRecipeManager
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin
import mezz.jei.api.registration.*
import mezz.jei.api.runtime.IJeiRuntime
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.crafting.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.MutableCollection
import kotlin.collections.MutableList
import kotlin.collections.listOf
import kotlin.collections.mutableListOf
import kotlin.collections.toMutableList
import kotlin.collections.withIndex

@JeiPlugin
class JEIPlugin : IModPlugin {
    lateinit var recipeManager: IRecipeManager
    override fun getPluginUid(): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "jei_plugin")
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories(LiquifierCategory(registration.jeiHelpers.guiHelper))
        registration.addRecipeCategories(InfuserCategory(registration.jeiHelpers.guiHelper))
        registration.addRecipeCategories(MinerCategory(registration.jeiHelpers.guiHelper, 1), MinerCategory(registration.jeiHelpers.guiHelper, 2), MinerCategory(registration.jeiHelpers.guiHelper, 3), MinerCategory(registration.jeiHelpers.guiHelper, 4), MinerCategory(registration.jeiHelpers.guiHelper, 5))
    }

    override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        //TODO
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalysts(LiquifierCategory.TYPE, ModMachines.LIQUIFIER)
        registration.addRecipeCatalysts(InfuserCategory.TYPE, ModMachines.INFUSER)

        registration.addRecipeCatalysts(MinerCategory.TYPE_TIER1, ModMachines.MINER_BLOCK_TIERS[0], ModMachines.MINER_BLOCK_TIERS[1], ModMachines.MINER_BLOCK_TIERS[2], ModMachines.MINER_BLOCK_TIERS[3], ModMachines.MINER_BLOCK_TIERS[4])
        registration.addRecipeCatalysts(MinerCategory.TYPE_TIER2, ModMachines.MINER_BLOCK_TIERS[1], ModMachines.MINER_BLOCK_TIERS[2], ModMachines.MINER_BLOCK_TIERS[3], ModMachines.MINER_BLOCK_TIERS[4])
        registration.addRecipeCatalysts(MinerCategory.TYPE_TIER3, ModMachines.MINER_BLOCK_TIERS[2], ModMachines.MINER_BLOCK_TIERS[3], ModMachines.MINER_BLOCK_TIERS[4])
        registration.addRecipeCatalysts(MinerCategory.TYPE_TIER4, ModMachines.MINER_BLOCK_TIERS[3], ModMachines.MINER_BLOCK_TIERS[4])
        registration.addRecipeCatalysts(MinerCategory.TYPE_TIER5, ModMachines.MINER_BLOCK_TIERS[4])
    }

    override fun registerAdvanced(registration: IAdvancedRegistration) {
        val data = listOf(Triple(MINER_TIER1_RECIPE_TYPE.get(), MinerCategory.TYPE_TIER1, oreTier1Tag), Triple(MINER_TIER2_RECIPE_TYPE.get(), MinerCategory.TYPE_TIER2, oreTier2Tag), Triple(MINER_TIER3_RECIPE_TYPE.get(), MinerCategory.TYPE_TIER3, oreTier3Tag), Triple(MINER_TIER4_RECIPE_TYPE.get(), MinerCategory.TYPE_TIER4, oreTier4Tag), Triple(MINER_TIER5_RECIPE_TYPE.get(), MinerCategory.TYPE_TIER5, oreTier5Tag))
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

        val crafting = RecipeType(BuiltInRegistries.RECIPE_TYPE.getKey(net.minecraft.world.item.crafting.RecipeType.CRAFTING)!!, RecipeHolder::class.java)
        registration.addRecipes(crafting, recipeManager.getAllRecipesFor(SHAPED_RECIPE_TRANSFORM_TYPE.get()).toList())
    }

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {
        this.recipeManager = jeiRuntime.recipeManager
        super.onRuntimeAvailable(jeiRuntime)
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addRecipeClickArea(LiquifierScreen::class.java, LiquifierScreen.progressBar.right() + 6, LiquifierScreen.progressBar.top(), 22, 16, LiquifierCategory.TYPE)
        registration.addRecipeClickArea(InfuserScreen::class.java, InfuserScreen.progressBar.right() + 6, InfuserScreen.progressBar.top(), 22, 16, InfuserCategory.TYPE)

        registration.addGuiContainerHandler(MinerScreen::class.java, object : IGuiContainerHandler<MinerScreen> {
            override fun getGuiClickableAreas(containerScreen: MinerScreen, guiMouseX: Double, guiMouseY: Double): MutableCollection<IGuiClickableArea> {
                val clickableArea = IGuiClickableArea.createBasic(
                    122, 67, 45, 13, *when (containerScreen.menu.tier) {
                        0 -> arrayOf(MinerCategory.TYPE_TIER1)
                        1 -> arrayOf(MinerCategory.TYPE_TIER2)
                        2 -> arrayOf(MinerCategory.TYPE_TIER3)
                        3 -> arrayOf(MinerCategory.TYPE_TIER4)
                        4 -> arrayOf(MinerCategory.TYPE_TIER5)
                        else -> arrayOf(MinerCategory.TYPE_TIER5)
                    }
                )
                return mutableListOf(clickableArea)
            }
        })
    }
}