package com.nred.azurum_miner.compat.emi

import com.nred.azurum_miner.compat.emi.EmiPlugin.Companion.INFUSER_CATEGORY
import com.nred.azurum_miner.machine.MachineScreen.Companion.TANK
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.miner.MinerScreen.Companion.ENERGY_BAR
import com.nred.azurum_miner.machine.miner.MinerScreen.Companion.ENERGY_INNER
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

class EmiInfuserRecipe(val recipeId: ResourceLocation, inputs: List<EmiIngredient>, val output: EmiStack, val power: Int, val processingTime: Int) : BasicEmiRecipe(INFUSER_CATEGORY, recipeId, 120, 69) {
    init {
        this.inputs.addAll(inputs)
        this.outputs.add(output)
        this.catalysts.add(EmiIngredient.of(Ingredient.of(ModMachines.INFUSER)))
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addFillingArrow(66, 26, 1500).tooltipText(listOf(Component.literal(getTime(processingTime))))

        widgets.addSlot(inputs[0], 46, 26)
        if (!inputs?.get(1)?.isEmpty!!) {
            widgets.addSlot(inputs[1], 70, 48)
        } else {
            widgets.addTexture(EmiTexture.SLOT, 70, 48)
        }
        widgets.addTank(inputs[2], 11, 2, 31, 66, inputs[2].amount.toInt()).drawBack(false)
        widgets.addDrawable(11, 2, 31, 65) { guiGraphics, _, _, _ -> guiGraphics.blitSprite(TANK, 0, 0, 150, 31, 65) }
        widgets.addSlot(outputs[0], 92, 22).large(true).recipeContext(this)

        widgets.addDrawable(2, 2, 6, 65) { guiGraphics, _, _, _ -> guiGraphics.blitSprite(ENERGY_BAR, 0, 0, 3, 6, 65) }.tooltipText(listOf(Component.literal(getFE(power))))
        widgets.addTexture(ENERGY_INNER.withPath("textures/gui/sprites/common/energy_inner.png"), 3, 3, 4, 63, 0, 0, 4, 63, 4, 2)
    }
}