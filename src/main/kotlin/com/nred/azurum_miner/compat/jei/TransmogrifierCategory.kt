package com.nred.azurum_miner.compat.jei

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierMenu
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen.Companion.ENERGY_BAR
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen.Companion.ENERGY_INNER
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen.Companion.energy
import com.nred.azurum_miner.recipe.TransmogrifierRecipe
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
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


class TransmogrifierCategory(helper: IGuiHelper) : IRecipeCategory<TransmogrifierRecipe> {
    companion object {
        val UID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "Transmogrifier")
        val TYPE = RecipeType(UID, TransmogrifierRecipe::class.java)
    }

    private val icon: IDrawable = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ItemStack(ModMachines.TRANSMOGRIFIER.get()))
    private val arrow: IDrawableAnimated = helper.createAnimatedRecipeArrow(30)

    override fun getWidth(): Int {
        return 100
    }

    override fun getHeight(): Int {
        return energy.height + 4
    }

    override fun getRecipeType(): RecipeType<TransmogrifierRecipe> {
        return TYPE
    }

    override fun getTitle(): Component {
        return Component.translatable("menu.title.azurum_miner.Transmogrifier")
    }

    override fun getIcon(): IDrawable {
        return this.icon
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: TransmogrifierRecipe, focuses: IFocusGroup) {
        TransmogrifierScreen.resize(width, 0, 0, 0)
        builder.addInputSlot(TransmogrifierMenu.slot_x, TransmogrifierMenu.slot_y).addIngredients(recipe.ingredients[0]).setStandardSlotBackground()
        builder.addOutputSlot(TransmogrifierMenu.slot_x + 80, TransmogrifierMenu.slot_y).addItemStack(recipe.result).setOutputSlotBackground()
    }

    override fun draw(recipe: TransmogrifierRecipe, recipeSlotsView: IRecipeSlotsView, guiGraphics: GuiGraphics, mouseX: Double, mouseY: Double) {
        TransmogrifierScreen.resize(width, 0, 0, 0)
        guiGraphics.blitSprite(ENERGY_BAR, energy.left(), energy.top() - 1, 3, energy.width, energy.height)
        guiGraphics.blitSprite(ENERGY_INNER, energy.left() + 1, energy.bottom() - energy.height, 4, energy.width - 2, energy.height - 2)

        if (energy.containsPoint(mouseX.toInt(), mouseY.toInt() + 1)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getFE(recipe.power)), mouseX.toInt(), mouseY.toInt())
        }
        if (ScreenRectangle(40, 26, this.arrow.width, this.arrow.height).containsPoint(mouseX.toInt(), mouseY.toInt())) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getTime(recipe.processingTime)), mouseX.toInt(), mouseY.toInt())
        }

        this.arrow.draw(guiGraphics, 40, 26)
    }
}