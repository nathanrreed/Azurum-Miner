package com.nred.azurum_miner.compat.emi

import com.nred.azurum_miner.compat.emi.EmiPlugin.Companion.PORTAL_CATEGORY
import com.nred.azurum_miner.compat.emi.EmiPlugin.Companion.portal
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
import dev.emi.emi.api.recipe.BasicEmiRecipe
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.TextWidget
import dev.emi.emi.api.widget.WidgetHolder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import kotlin.math.abs

class EmiPortalRecipe(id: ResourceLocation, inputs: List<EmiIngredient>, output: EmiStack, val processingTime: Int) : BasicEmiRecipe(PORTAL_CATEGORY, id, 128, 105) {
    val obsidian: ResourceLocation = ResourceLocation.withDefaultNamespace("textures/block/obsidian.png")

    init {
        this.inputs.addAll(inputs)
        this.outputs.add(output)
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addFillingArrow(68, 44, 8000).tooltipText(listOf(Component.literal(getTime(processingTime))))

        widgets.addDrawable(2, 12, 64, 80) { guiGraphics, _, _, _ ->
            for (x in 0..3) {
                for (y in 0..4) {
                    if (abs(x.toDouble() - 1.5) < 1.0 && y != 0 && y != 4) {
                        guiGraphics.blitSprite(portal, x * 16, y * 16, 16, 16)
                    } else {
                        guiGraphics.blit(obsidian, x * 16, y * 16, 0f, 0f, 16, 16, 16, 16)
                    }
                }
            }
        }

        widgets.addSlot(inputs[0], 26, 44).drawBack(false)
        widgets.addSlot(outputs[0], 94, 40).large(true).recipeContext(this)

        widgets.addText(Component.translatable("tooltip.azurum_miner.portal"), 64, 0, 0xFFFFFFFF.toInt(), true).horizontalAlign(TextWidget.Alignment.CENTER)
        widgets.addText(Component.translatable("tooltip.azurum_miner.portal_warning"), 64, 95, 0xFFFF0000.toInt(), true).horizontalAlign(TextWidget.Alignment.CENTER)
    }
}