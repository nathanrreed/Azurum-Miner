package com.nred.azurum_miner.compat.jei

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.infuser.InfuserMenu
import com.nred.azurum_miner.machine.infuser.InfuserScreen
import com.nred.azurum_miner.machine.infuser.InfuserScreen.Companion.ENERGY_BAR
import com.nred.azurum_miner.machine.infuser.InfuserScreen.Companion.ENERGY_INNER
import com.nred.azurum_miner.machine.infuser.InfuserScreen.Companion.TANK
import com.nred.azurum_miner.machine.infuser.InfuserScreen.Companion.energy
import com.nred.azurum_miner.machine.infuser.InfuserScreen.Companion.tank
import com.nred.azurum_miner.recipe.InfuserRecipe
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


class InfuserCategory(helper: IGuiHelper) : IRecipeCategory<InfuserRecipe> {
    companion object {
        val UID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "infuser")
        val TYPE = RecipeType(UID, InfuserRecipe::class.java)
    }

    private val icon: IDrawable = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ItemStack(ModMachines.INFUSER.get()))
    private val arrow: IDrawableAnimated = helper.createAnimatedRecipeArrow(30)

    override fun getWidth(): Int {
        return 120
    }

    override fun getHeight(): Int {
        return energy.height + 4
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
        InfuserScreen.resize(width, 0, 0, 0)
        builder.addInputSlot(InfuserMenu.slot_x + 29, InfuserMenu.slot_y).addIngredients(recipe.ingredients[0]).setStandardSlotBackground()
        builder.addInputSlot(InfuserMenu.slot_x + 52, InfuserMenu.slot_y + 21).addIngredients(recipe.ingredients[1]).setStandardSlotBackground()
        builder.addInputSlot(energy.right() - 9, tank.top() - 1).addFluidStack(recipe.inputFluid.fluid, recipe.inputFluid.amount.toLong()).setFluidRenderer(recipe.inputFluid.amount.toLong(), false, tank.width, energy.height)
        builder.addOutputSlot(InfuserMenu.slot_x + 80, InfuserMenu.slot_y).addItemStack(recipe.result).setOutputSlotBackground()
    }

    override fun draw(recipe: InfuserRecipe, recipeSlotsView: IRecipeSlotsView, guiGraphics: GuiGraphics, mouseX: Double, mouseY: Double) {
        InfuserScreen.resize(width, 0, 0, 0)
        guiGraphics.blitSprite(ENERGY_BAR, energy.left() - 13, energy.top() - 1, 3, energy.width, energy.height)
        guiGraphics.blitSprite(ENERGY_INNER, energy.left() - 12, energy.bottom() - energy.height, 4, energy.width - 2, energy.height - 2)

        // TANK
        guiGraphics.blitSprite(TANK, energy.right() - 9, tank.top() - 1, 150, tank.width, energy.height)

        if (ScreenRectangle(energy.left() - 13, energy.top() - 1, energy.width, energy.height).containsPoint(mouseX.toInt(), mouseY.toInt())) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getFE(recipe.power)), mouseX.toInt(), mouseY.toInt())
        }

        if (ScreenRectangle(72, 26, this.arrow.width, this.arrow.height).containsPoint(mouseX.toInt(), mouseY.toInt())) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(getTime(recipe.processingTime)), mouseX.toInt(), mouseY.toInt())
        }

        this.arrow.draw(guiGraphics, 67, 26)
    }
}