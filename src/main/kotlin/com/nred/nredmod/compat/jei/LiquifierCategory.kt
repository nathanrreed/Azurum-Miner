package com.nred.nredmod.compat.jei

import com.nred.nredmod.NredMod
import com.nred.nredmod.machine.ModMachines
import com.nred.nredmod.machine.liquifier.LiquifierMenu
import com.nred.nredmod.machine.liquifier.LiquifierScreen
import com.nred.nredmod.machine.liquifier.LiquifierScreen.Companion.ENERGY_BAR
import com.nred.nredmod.machine.liquifier.LiquifierScreen.Companion.ENERGY_INNER
import com.nred.nredmod.machine.liquifier.LiquifierScreen.Companion.TANK
import com.nred.nredmod.machine.liquifier.LiquifierScreen.Companion.energy
import com.nred.nredmod.machine.liquifier.LiquifierScreen.Companion.tank
import com.nred.nredmod.recipe.LiquifierRecipe
import com.nred.nredmod.screen.GuiCommon.Companion.getFE
import com.nred.nredmod.screen.GuiCommon.Companion.getTime
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
        val UID = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "liquifier")
        val TYPE = RecipeType(UID, LiquifierRecipe::class.java)
    }

    private val icon: IDrawable = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ItemStack(ModMachines.LIQUIFIER.get()))
    private val arrow: IDrawableAnimated = helper.createAnimatedRecipeArrow(30)

    override fun getWidth(): Int {
        return 100
    }

    override fun getHeight(): Int {
        return energy.height + 4
    }

    override fun getRecipeType(): RecipeType<LiquifierRecipe> {
        return TYPE
    }

    override fun getTitle(): Component {
        return Component.translatable("menu.title.nredmod.liquifier")
    }

    override fun getIcon(): IDrawable {
        return this.icon
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: LiquifierRecipe, focuses: IFocusGroup) {
        LiquifierScreen.resize(width, 0, 0, 0)
        builder.addInputSlot(LiquifierMenu.slot_x, LiquifierMenu.slot_y).addIngredients(recipe.ingredients[0]).setStandardSlotBackground()
        builder.addOutputSlot(width - tank.width - 1, tank.top() - 1).addFluidStack(recipe.result.fluid, recipe.result.amount.toLong()).setFluidRenderer(recipe.result.amount.toLong(), false, tank.width, energy.height)
    }

    override fun draw(recipe: LiquifierRecipe, recipeSlotsView: IRecipeSlotsView, guiGraphics: GuiGraphics, mouseX: Double, mouseY: Double) {
        LiquifierScreen.resize(width, 0, 0, 0)
        guiGraphics.blitSprite(ENERGY_BAR, energy.left(), energy.top() - 1, 3, energy.width, energy.height)
        guiGraphics.blitSprite(ENERGY_INNER, energy.left() + 1, energy.bottom() - energy.height, 4, energy.width - 2, energy.height - 2)


        // TANK
        guiGraphics.blitSprite(TANK, width - tank.width - 1, tank.top() - 1, 150, tank.width, energy.height)

        if (energy.containsPoint(mouseX.toInt(), mouseY.toInt() + 1)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getFE(recipe.power)), mouseX.toInt(), mouseY.toInt())
        }
        if (ScreenRectangle(40, 26, this.arrow.width, this.arrow.height).containsPoint(mouseX.toInt(), mouseY.toInt())) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getTime(recipe.processingTime)), mouseX.toInt(), mouseY.toInt())
        }

        this.arrow.draw(guiGraphics, 40, 26)
    }
}