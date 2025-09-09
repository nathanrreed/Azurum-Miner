package com.nred.azurum_miner.compat.jei

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.miner.MinerScreen.Companion.ENERGY_BAR
import com.nred.azurum_miner.machine.miner.MinerScreen.Companion.ENERGY_INNER
import com.nred.azurum_miner.recipe.TransmogrifierRecipe
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
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack


class TransmogrifierCategory(helper: IGuiHelper) : IRecipeCategory<TransmogrifierRecipe> {
    companion object {
        val UID: ResourceLocation = azLoc("transmogrifier")
        val TYPE = RecipeType(UID, TransmogrifierRecipe::class.java)
        val baseEnergy = CONFIG.getInt("transmogrifier.baseEnergyRequired")
    }

    private val icon: IDrawable = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ItemStack(ModMachines.TRANSMOGRIFIER.get()))
    private val arrow: IDrawableAnimated = helper.createAnimatedRecipeArrow(30)

    override fun getWidth(): Int {
        return 100
    }

    override fun getHeight(): Int {
        return 69
    }

    override fun getRecipeType(): RecipeType<TransmogrifierRecipe> {
        return TYPE
    }

    override fun getTitle(): Component {
        return Component.translatable("menu.title.azurum_miner.transmogrifier")
    }

    override fun getIcon(): IDrawable {
        return this.icon
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: TransmogrifierRecipe, focuses: IFocusGroup) {
        builder.addInputSlot(18, 26).addIngredients(recipe.ingredients[0]).setStandardSlotBackground()
        builder.addOutputSlot(72, 26).addItemStack(recipe.result).setOutputSlotBackground()
    }

    override fun draw(recipe: TransmogrifierRecipe, recipeSlotsView: IRecipeSlotsView, guiGraphics: GuiGraphics, mouseX: Double, mouseY: Double) {
        guiGraphics.blitSprite(ENERGY_BAR, 2, 2, 3, 6, 65)
        guiGraphics.blitSprite(ENERGY_INNER, 3, 3, 4, 4, 63)

        if (ScreenRectangle(2, 2, 6, 65).containsPoint(mouseX.toInt(), mouseY.toInt() + 1)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getFE(Mth.ceil(recipe.powerMult * baseEnergy))), mouseX.toInt(), mouseY.toInt())
        }
        if (ScreenRectangle(40, 26, this.arrow.width, this.arrow.height).containsPoint(mouseX.toInt(), mouseY.toInt())) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getTime(recipe.processingTime)), mouseX.toInt(), mouseY.toInt())
        }

        this.arrow.draw(guiGraphics, 40, 26)
    }
}