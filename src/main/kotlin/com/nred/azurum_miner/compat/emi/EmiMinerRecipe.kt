package com.nred.azurum_miner.compat.emi

import com.nred.azurum_miner.compat.emi.EmiPlugin.Companion.MINER_CATEGORY
import com.nred.azurum_miner.compat.jei.MinerCategory
import dev.emi.emi.api.recipe.BasicEmiRecipe
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.widget.TextWidget
import dev.emi.emi.api.widget.WidgetHolder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class EmiMinerRecipe(id: ResourceLocation, val output: EmiIngredient, catalysts: List<EmiIngredient>, val tier: Int) : BasicEmiRecipe(MINER_CATEGORY, id, 120, 69) {
    init {
        this.outputs.addAll(output.emiStacks)
        this.catalysts.addAll(catalysts)
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addSlot(output, width / 2 - 13, height / 2 - 13).large(true).recipeContext(this)
        widgets.addText(Component.translatable("menu.title.azurum_miner.miner", MinerCategory.tierRange(tier + 1)), 60, 0, 0xFFFFFFFF.toInt(), true).horizontalAlign(TextWidget.Alignment.CENTER)
    }
}