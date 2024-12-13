package com.nred.azurum_miner.compat.emi

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.recipe.ModRecipe
import dev.emi.emi.api.EmiEntrypoint
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.neoforge.NeoForgeEmiIngredient
import dev.emi.emi.api.recipe.EmiCraftingRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient

@EmiEntrypoint
class EmiPlugin : EmiPlugin {
    companion object {
        var animationTick = 0
        var frame = 0
        val portal = ResourceLocation.withDefaultNamespace("textures/block/nether_portal.png")

        val LIQUIFIER_WORKSTATION = EmiStack.of(ModMachines.LIQUIFIER)
        val INFUSER_WORKSTATION = EmiStack.of(ModMachines.INFUSER)
        val MINER_WORKSTATION = EmiIngredient.of(ModMachines.MINER_BLOCK_TIERS.map { EmiIngredient.of(Ingredient.of(it.get())) })
        val LIQUIFIER_CATEGORY = EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "liquifier"), LIQUIFIER_WORKSTATION)
        val INFUSER_CATEGORY = EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "infuser"), INFUSER_WORKSTATION)
        val MINER_CATEGORY = EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner"), MINER_WORKSTATION)
        val PORTAL_CATEGORY = EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "portal")) { guiGraphics, x, y, _ ->
            guiGraphics.blit(portal, x, y, 0f, animationTick * 16f, 16, 16, 16, 512)
            this.frame = (this.frame + 1) % 8
            if (frame == 0) {
                this.animationTick = (this.animationTick + 1) % 32
            }
        }


//            EmiTexture(ResourceLocation.withDefaultNamespace("textures/block/nether_portal.png"), 0, 0, 16, 16))
    }

    override fun register(registry: EmiRegistry) {
        registry.addCategory(LIQUIFIER_CATEGORY)
        registry.addCategory(INFUSER_CATEGORY)
        registry.addCategory(MINER_CATEGORY)
        registry.addCategory(PORTAL_CATEGORY)

        registry.addWorkstation(LIQUIFIER_CATEGORY, LIQUIFIER_WORKSTATION)
        registry.addWorkstation(INFUSER_CATEGORY, INFUSER_WORKSTATION)
        registry.addWorkstation(MINER_CATEGORY, MINER_WORKSTATION)

        val manager = registry.recipeManager

        for (recipe in manager.getAllRecipesFor(ModRecipe.SHAPED_RECIPE_TRANSFORM_TYPE.get())) {
            registry.addRecipe(EmiCraftingRecipe(recipe.value.ingredients.map { EmiIngredient.of(it) }, EmiStack.of(recipe.value.resultStack), recipe.id, false))
        }

        for ((idx, type) in listOf(ModRecipe.MINER_TIER1_RECIPE_TYPE.get(), ModRecipe.MINER_TIER2_RECIPE_TYPE.get(), ModRecipe.MINER_TIER3_RECIPE_TYPE.get(), ModRecipe.MINER_TIER4_RECIPE_TYPE.get(), ModRecipe.MINER_TIER5_RECIPE_TYPE.get()).withIndex()) {
            for (recipe in manager.getAllRecipesFor(type)) {
                registry.addRecipe(MinerRecipe(recipe.id, EmiIngredient.of(recipe.value.result), ModMachines.MINER_BLOCK_TIERS.filterIndexed { i, _ -> i <= idx }.map { EmiIngredient.of(Ingredient.of(ItemStack(it.asItem(), 1))) }, idx))
            }
        }

        for (recipe in manager.getAllRecipesFor(ModRecipe.LIQUIFIER_RECIPE_TYPE.get())) {
            registry.addRecipe(EmiLiquifierRecipe(recipe.id, recipe.value.ingredients.map { EmiIngredient.of(it) }, EmiStack.of(recipe.value.result.fluid, recipe.value.result.amount.toLong()), recipe.value.power, recipe.value.processingTime))
        }

        for (recipe in manager.getAllRecipesFor(ModRecipe.INFUSER_RECIPE_TYPE.get())) {
            registry.addRecipe(EmiInfuserRecipe(recipe.id, recipe.value.ingredients.map { EmiIngredient.of(it) } + NeoForgeEmiIngredient.of(SizedFluidIngredient.of(recipe.value.inputFluid)), EmiStack.of(recipe.value.result), recipe.value.power, recipe.value.processingTime))
        }

        registry.addRecipe(EmiPortalRecipe(ResourceLocation.parse(AzurumMiner.ID + ":/dimensional_matrix"), listOf(EmiIngredient.of(Ingredient.of(ItemStack(ModItems.EMPTY_DIMENSIONAL_MATRIX.asItem(), 1)))), EmiStack.of(ModItems.DIMENSIONAL_MATRIX, 1), 2400))
    }
}