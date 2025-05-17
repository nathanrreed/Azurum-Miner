package com.nred.azurum_miner.compat.emi

import com.nred.azurum_miner.compat.emi.EmiPlugin.Companion.CRYSTALLIZER_CATEGORY
import com.nred.azurum_miner.machine.MachineScreen.Companion.TANK
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.miner.MinerScreen.Companion.ENERGY_BAR
import com.nred.azurum_miner.machine.miner.MinerScreen.Companion.ENERGY_INNER
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
import dev.emi.emi.api.recipe.BasicEmiRecipe
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient

class EmiCrystallizerRecipe(val recipeId: ResourceLocation, inputs: List<EmiIngredient>, val fluid: EmiStack, val output: EmiStack, val power: Int, val processingTime: Int) : BasicEmiRecipe(CRYSTALLIZER_CATEGORY, recipeId, 120, 69) {
    init {
        this.inputs.addAll(inputs)
        this.inputs.add(fluid)
        this.outputs.add(output)
        this.catalysts.add(EmiIngredient.of(Ingredient.of(ModMachines.CRYSTALLIZER)))
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addFillingArrow(66, 26, 1500).tooltipText(listOf(Component.literal(getTime(processingTime))))

        widgets.addSlot(inputs[0], 46, 26)
        widgets.addTank(fluid, 11, 2, 31, 66, fluid.amount.toInt()).drawBack(false)
        widgets.addDrawable(11, 2, 31, 65) { guiGraphics, _, _, _ -> guiGraphics.blitSprite(TANK, 0, 0, 150, 31, 65) }
        widgets.addSlot(outputs[0], 92, 22).large(true).recipeContext(this)

        widgets.addDrawable(2, 2, 6, 65) { guiGraphics, _, _, _ -> guiGraphics.blitSprite(ENERGY_BAR, 0, 0, 3, 6, 65) }.tooltipText(listOf(Component.literal(getFE(power))))
        widgets.addTexture(ENERGY_INNER.withPath("textures/gui/sprites/common/energy_inner.png"), 3, 3, 4, 63, 0, 0, 4, 63, 4, 2)
    }
}