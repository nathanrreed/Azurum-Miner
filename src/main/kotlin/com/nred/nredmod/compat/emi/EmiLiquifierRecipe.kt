package com.nred.nredmod.compat.emi

import com.nred.nredmod.compat.emi.EmiPlugin.Companion.LIQUIFIER_CATEGORY
import com.nred.nredmod.machine.ModMachines
import com.nred.nredmod.machine.infuser.InfuserScreen.Companion
import com.nred.nredmod.machine.liquifier.LiquifierMenu
import com.nred.nredmod.machine.liquifier.LiquifierScreen
import com.nred.nredmod.machine.liquifier.LiquifierScreen.Companion.energy
import com.nred.nredmod.machine.liquifier.LiquifierScreen.Companion.tank
import com.nred.nredmod.screen.GuiCommon.Companion.getFE
import com.nred.nredmod.screen.GuiCommon.Companion.getTime
import dev.emi.emi.api.recipe.BasicEmiRecipe
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient

class EmiLiquifierRecipe(id: ResourceLocation, inputs: List<EmiIngredient>, output: EmiStack, val power: Int, val processingTime: Int) : BasicEmiRecipe(LIQUIFIER_CATEGORY, id, 100, 69) {
    init {
        this.inputs.addAll(inputs)
        this.outputs.add(output)
        this.catalysts.add(EmiIngredient.of(Ingredient.of(ModMachines.LIQUIFIER)))
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addFillingArrow(40, 26, 1500).tooltipText(listOf(Component.literal(getTime(processingTime))))

        LiquifierScreen.resize(width, 0, 0, 0)
        widgets.addSlot(inputs[0], LiquifierMenu.slot_x, LiquifierMenu.slot_y)
        widgets.addTank(outputs[0], width - tank.width - 1, tank.top() - 1, tank.width, energy.height + 1, outputs[0].amount.toInt()).drawBack(false).recipeContext(this)
        widgets.addDrawable(width - tank.width - 1, tank.top() - 1, tank.width, energy.height) { guiGraphics, _, _, _ -> guiGraphics.blitSprite(Companion.TANK, 0, 0, 150, tank.width, energy.height) }

        widgets.addDrawable(Companion.energy.left() - 13, Companion.energy.top() - 1, Companion.energy.width, Companion.energy.height) { guiGraphics, _, _, _ -> guiGraphics.blitSprite(Companion.ENERGY_BAR, 0, 0, 3, Companion.energy.width, Companion.energy.height) }.tooltipText(listOf(Component.literal(getFE(power))))
        widgets.addTexture(Companion.ENERGY_INNER.withPath("textures/gui/sprites/common/energy_inner.png"), Companion.energy.left() - 12, Companion.energy.bottom() - Companion.energy.height, Companion.energy.width - 2, Companion.energy.height - 2, 0, 0, Companion.energy.width - 2, Companion.energy.height - 2, 4, 2)
    }
}