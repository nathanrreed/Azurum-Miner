package com.nred.azurum_miner.compat.jei

import com.nred.azurum_miner.common.MinerCommon
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.recipe.MinerRecipe
import com.nred.azurum_miner.util.Helpers.azLoc
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder


class MinerCategory(helper: IGuiHelper, val tier: Int) : IRecipeCategory<RecipeHolder<MinerRecipe>> {
    companion object {
        val TYPE_TIER1 = RecipeType(azLoc("miner_tier1"), RecipeHolder::class.java)
        val TYPE_TIER2 = RecipeType(azLoc("miner_tier2"), RecipeHolder::class.java)
        val TYPE_TIER3 = RecipeType(azLoc("miner_tier3"), RecipeHolder::class.java)
        val TYPE_TIER4 = RecipeType(azLoc("miner_tier4"), RecipeHolder::class.java)
        val TYPE_TIER5 = RecipeType(azLoc("miner_tier5"), RecipeHolder::class.java)
    }

    private val icon: IDrawable = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ItemStack(ModMachines.MINER_BLOCK_TIERS[tier - 1].get()))

    override fun getWidth(): Int {
        return 100
    }

    override fun getHeight(): Int {
        return 50
    }

    @Suppress("UNCHECKED_CAST")
    override fun getRecipeType(): RecipeType<RecipeHolder<MinerRecipe>> {
        return when (this.tier) {
            1 -> TYPE_TIER1 as RecipeType<RecipeHolder<MinerRecipe>>
            2 -> TYPE_TIER2 as RecipeType<RecipeHolder<MinerRecipe>>
            3 -> TYPE_TIER3 as RecipeType<RecipeHolder<MinerRecipe>>
            4 -> TYPE_TIER4 as RecipeType<RecipeHolder<MinerRecipe>>
            5 -> TYPE_TIER5 as RecipeType<RecipeHolder<MinerRecipe>>
            else -> TYPE_TIER5 as RecipeType<RecipeHolder<MinerRecipe>>
        }
    }

    override fun getTitle(): Component {
        return Component.translatable("menu.title.azurum_miner.miner_no_tier")
    }

    override fun getIcon(): IDrawable {
        return this.icon
    }


    override fun isHandled(recipe: RecipeHolder<MinerRecipe>): Boolean {
        return true
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: RecipeHolder<MinerRecipe>, focuses: IFocusGroup) {
        builder.addOutputSlot(42, 21).addIngredients(recipe.value.result).setOutputSlotBackground()
    }

    override fun draw(recipe: RecipeHolder<MinerRecipe>, recipeSlotsView: IRecipeSlotsView, guiGraphics: GuiGraphics, mouseX: Double, mouseY: Double) {
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("menu.title.azurum_miner.miner", MinerCommon.tierRange(recipe.value.tier)), 50, 0, 0xFFFFFFFF.toInt())
    }
}