package com.nred.azurum_miner.compat.jei

import com.nred.azurum_miner.machine.MachineScreen.Companion.TANK
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.miner.MinerScreen.Companion.ENERGY_BAR
import com.nred.azurum_miner.machine.miner.MinerScreen.Companion.ENERGY_INNER
import com.nred.azurum_miner.recipe.LiquifierRecipe
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
import com.nred.azurum_miner.util.Helpers.azLoc
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack


class LiquifierCategory(helper: IGuiHelper) : IRecipeCategory<LiquifierRecipe> {
    companion object {
        val UID: ResourceLocation = azLoc("liquifier")
        val TYPE = RecipeType(UID, LiquifierRecipe::class.java)
    }

    private val icon: IDrawable = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ItemStack(ModMachines.LIQUIFIER.get()))
    private val arrow: IDrawableAnimated = helper.createAnimatedRecipeArrow(30)

    override fun getWidth(): Int {
        return 100
    }

    override fun getHeight(): Int {
        return 69
    }

    override fun getRecipeType(): RecipeType<LiquifierRecipe> {
        return TYPE
    }

    override fun getTitle(): Component {
        return Component.translatable("menu.title.azurum_miner.liquifier")
    }

    override fun getIcon(): IDrawable {
        return this.icon
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: LiquifierRecipe, focuses: IFocusGroup) {
        builder.addInputSlot(18, 26).addIngredients(recipe.ingredients[0]).setStandardSlotBackground()
        if (!recipe.inputFluid.isEmpty)
            builder.addInputSlot(10, 3).addFluidStack(recipe.inputFluid.fluid, recipe.inputFluid.amount.toLong()).setFluidRenderer(recipe.inputFluid.amount.toLong(), false, 4, 63)
        builder.addOutputSlot(width - 33, 2).addFluidStack(recipe.result.fluid, recipe.result.amount.toLong()).setFluidRenderer(recipe.result.amount.toLong(), false, 31, 65)
    }

    override fun draw(recipe: LiquifierRecipe, recipeSlotsView: IRecipeSlotsView, guiGraphics: GuiGraphics, mouseX: Double, mouseY: Double) {
        guiGraphics.blitSprite(ENERGY_BAR, 2, 2, 3, 6, 65)
        guiGraphics.blitSprite(ENERGY_INNER, 3, 3, 4, 4, 63)

        // TANK
        guiGraphics.blitSprite(TANK, width - 33, 2, 150, 31, 65)

        if (!recipe.inputFluid.isEmpty)
            guiGraphics.blitSprite(ENERGY_BAR, 9, 2, 3, 6, 65)


        if (ScreenRectangle(2, 2, 6, 65).containsPoint(mouseX.toInt(), mouseY.toInt() + 1)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getFE(recipe.power)), mouseX.toInt(), mouseY.toInt())
        }
        if (ScreenRectangle(40, 26, this.arrow.width, this.arrow.height).containsPoint(mouseX.toInt(), mouseY.toInt())) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getTime(recipe.processingTime)), mouseX.toInt(), mouseY.toInt())
        }

        this.arrow.draw(guiGraphics, 40, 26)
    }
}