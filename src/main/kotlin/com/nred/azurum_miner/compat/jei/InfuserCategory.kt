package com.nred.azurum_miner.compat.jei

import com.nred.azurum_miner.machine.MachineScreen.Companion.ENERGY_INNER
import com.nred.azurum_miner.machine.MachineScreen.Companion.TANK
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.infuser.InfuserMenu
import com.nred.azurum_miner.machine.miner.MinerScreen.Companion.ENERGY_BAR
import com.nred.azurum_miner.recipe.InfuserRecipe
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


class InfuserCategory(helper: IGuiHelper) : IRecipeCategory<InfuserRecipe> {
    companion object {
        val UID: ResourceLocation = azLoc("infuser")
        val TYPE = RecipeType(UID, InfuserRecipe::class.java)
    }

    private val icon: IDrawable = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ItemStack(ModMachines.INFUSER.get()))
    private val arrow: IDrawableAnimated = helper.createAnimatedRecipeArrow(30)

    override fun getWidth(): Int {
        return 120
    }

    override fun getHeight(): Int {
        return 69
    }

    override fun getRecipeType(): RecipeType<InfuserRecipe> {
        return TYPE
    }

    override fun getTitle(): Component {
        return Component.translatable("menu.title.azurum_miner.infuser")
    }

    override fun getIcon(): IDrawable {
        return this.icon
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: InfuserRecipe, focuses: IFocusGroup) {
        builder.addInputSlot(InfuserMenu.slot_x, InfuserMenu.slot_y - 8).addIngredients(recipe.ingredients[0]).setStandardSlotBackground()
        builder.addInputSlot(InfuserMenu.slot_x + 24, InfuserMenu.slot_y + 14).addIngredients(recipe.ingredients[1]).setStandardSlotBackground()
        builder.addInputSlot(9, 2).addFluidStack(recipe.inputFluid.fluid, recipe.inputFluid.amount.toLong()).setFluidRenderer(recipe.inputFluid.amount.toLong(), false, 31, 65)
        builder.addOutputSlot(InfuserMenu.slot_x + 53, InfuserMenu.slot_y - 8).addItemStack(recipe.result).setOutputSlotBackground()
    }

    override fun draw(recipe: InfuserRecipe, recipeSlotsView: IRecipeSlotsView, guiGraphics: GuiGraphics, mouseX: Double, mouseY: Double) {
        guiGraphics.blitSprite(ENERGY_BAR, 0, 2, 3, 6, 65)
        guiGraphics.blitSprite(ENERGY_INNER, 1, 3, 4, 4, 63)

        // TANK
        guiGraphics.blitSprite(TANK, 9, 2, 150, 31, 65)

        if (ScreenRectangle(0, 2, 6, 65).containsPoint(mouseX.toInt(), mouseY.toInt())) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getFE(recipe.power)), mouseX.toInt(), mouseY.toInt())
        }

        if (ScreenRectangle(72, 26, this.arrow.width, this.arrow.height).containsPoint(mouseX.toInt(), mouseY.toInt())) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getTime(recipe.processingTime)), mouseX.toInt(), mouseY.toInt())
        }

        this.arrow.draw(guiGraphics, 67, 26)
    }
}