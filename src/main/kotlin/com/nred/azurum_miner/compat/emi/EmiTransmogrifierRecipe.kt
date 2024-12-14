package com.nred.azurum_miner.compat.emi

import com.nred.azurum_miner.compat.emi.EmiPlugin.Companion.TRANSMOGRIFIER_CATEGORY
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierMenu
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen.Companion.ENERGY_BAR
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen.Companion.ENERGY_INNER
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen.Companion.energy
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
import dev.emi.emi.api.recipe.BasicEmiRecipe
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient

class EmiTransmogrifierRecipe(id: ResourceLocation, inputs: List<EmiIngredient>, output: EmiStack, val power: Int, val processingTime: Int) : BasicEmiRecipe(TRANSMOGRIFIER_CATEGORY, id, 120, 69) {
    init {
        this.inputs.addAll(inputs)
        this.outputs.add(output)
        this.catalysts.add(EmiIngredient.of(Ingredient.of(ModMachines.TRANSMOGRIFIER)))
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addFillingArrow(50, 26, 1500).tooltipText(listOf(Component.literal(getTime(processingTime))))

        TransmogrifierScreen.resize(width, 0, 0, 0)
        widgets.addSlot(inputs[0], TransmogrifierMenu.slot_x + 10, TransmogrifierMenu.slot_y)
        widgets.addSlot(outputs[0], TransmogrifierMenu.slot_x + 60, TransmogrifierMenu.slot_y - 4).large(true).recipeContext(this)

        widgets.addDrawable(energy.left() - 13, energy.top() - 1, energy.width, energy.height) { guiGraphics, _, _, _ -> guiGraphics.blitSprite(ENERGY_BAR, 0, 0, 3, energy.width, energy.height) }.tooltipText(listOf(Component.literal(getFE(power))))
        widgets.addTexture(ENERGY_INNER.withPath("textures/gui/sprites/common/energy_inner.png"), energy.left() - 12, energy.bottom() - energy.height, energy.width - 2, energy.height - 2, 0, 0, energy.width - 2, energy.height - 2, 4, 2)
    }
}