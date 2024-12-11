package com.nred.azurum_miner.compat.emi

import com.nred.azurum_miner.compat.emi.EmiPlugin.Companion.INFUSER_CATEGORY
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.infuser.InfuserMenu
import com.nred.azurum_miner.machine.infuser.InfuserScreen
import com.nred.azurum_miner.machine.infuser.InfuserScreen.Companion.ENERGY_BAR
import com.nred.azurum_miner.machine.infuser.InfuserScreen.Companion.ENERGY_INNER
import com.nred.azurum_miner.machine.infuser.InfuserScreen.Companion.TANK
import com.nred.azurum_miner.machine.infuser.InfuserScreen.Companion.energy
import com.nred.azurum_miner.machine.infuser.InfuserScreen.Companion.tank
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
import dev.emi.emi.api.recipe.BasicEmiRecipe
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient

class EmiInfuserRecipe(id: ResourceLocation, inputs: List<EmiIngredient>, output: EmiStack, val power: Int, val processingTime: Int) : BasicEmiRecipe(INFUSER_CATEGORY, id, 120, 69) {
    init {
        this.inputs.addAll(inputs)
        this.outputs.add(output)
        this.catalysts.add(EmiIngredient.of(Ingredient.of(ModMachines.LIQUIFIER)))
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addFillingArrow(67, 26, 1500).tooltipText(listOf(Component.literal(getTime(processingTime))))

        InfuserScreen.resize(width, 0, 0, 0)
        widgets.addSlot(inputs[0], InfuserMenu.slot_x + 29, InfuserMenu.slot_y)
        if (!inputs?.get(1)?.isEmpty!!) {
            widgets.addSlot(inputs[1], InfuserMenu.slot_x + 52, InfuserMenu.slot_y + 21)
        }else{
            widgets.addTexture(EmiTexture.SLOT, InfuserMenu.slot_x + 52, InfuserMenu.slot_y + 21)
        }
        widgets.addTank(inputs[2], energy.right() - 9, tank.top() - 1, tank.width, energy.height + 1, inputs[2].amount.toInt()).drawBack(false)
        widgets.addDrawable(energy.right() - 9, tank.top() - 1, tank.width, energy.height) { guiGraphics, _, _, _ -> guiGraphics.blitSprite(TANK, 0,0, 150, tank.width, energy.height) }
        widgets.addSlot(outputs[0], InfuserMenu.slot_x + 75, InfuserMenu.slot_y - 4).large(true).recipeContext(this)

        widgets.addDrawable(energy.left() - 13, energy.top() - 1, energy.width, energy.height) { guiGraphics, _, _, _ -> guiGraphics.blitSprite(ENERGY_BAR, 0, 0, 3, energy.width, energy.height) }.tooltipText(listOf(Component.literal(getFE(power))))
        widgets.addTexture(ENERGY_INNER.withPath("textures/gui/sprites/common/energy_inner.png"), energy.left() - 12, energy.bottom() - energy.height, energy.width - 2, energy.height - 2, 0, 0, energy.width - 2, energy.height - 2, 4, 2)
    }
}