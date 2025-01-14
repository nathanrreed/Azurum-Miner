package com.nred.azurum_miner.compat.emi

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.compat.emi.EmiPlugin.Companion.GENERATOR_CATEGORY
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.generator.BASE_SLOT
import com.nred.azurum_miner.machine.generator.FUEL_SLOT
import com.nred.azurum_miner.machine.generator.MATRIX_SLOT
import com.nred.azurum_miner.machine.generator.OUTPUT_SLOT
import com.nred.azurum_miner.recipe.GeneratorRecipe
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
import dev.emi.emi.api.recipe.BasicEmiRecipe
import dev.emi.emi.api.recipe.EmiInfoRecipe
import dev.emi.emi.api.recipe.EmiIngredientRecipe
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.SlotWidget
import dev.emi.emi.api.widget.WidgetHolder
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.CommonColors
import net.minecraft.world.item.crafting.Ingredient

class ConnectedSlotWidget(stack: EmiIngredient, x: Int, y: Int, val slot: Int, val generatorRecipe: com.nred.azurum_miner.compat.emi.GeneratorRecipe? = null) : SlotWidget(stack, x, y) {
    override fun getTooltip(mouseX: Int, mouseY: Int): List<ClientTooltipComponent> {
        val list = super.getTooltip(mouseX, mouseY)

        if (generatorRecipe != null) {
            when (slot) {
                FUEL_SLOT -> {
                    list.add(1, ClientTooltipComponent.create(Component.translatable("tooltip.azurum_miner.generator.lasts", getTime(generatorRecipe.lasts)).withColor(0xFFFFAA22.toInt()).visualOrderText))
                }

                BASE_SLOT -> {
                    val currStack = stack.emiStacks[(System.currentTimeMillis() / 1000 % stack.emiStacks.size).toInt()]
                    list.add(3, ClientTooltipComponent.create(Component.translatable("tooltip.azurum_miner.generator.lasts", getTime(generatorRecipe.bases.first { it.input.`is`(currStack.itemStack.item) }.lasts)).withColor(0xFFFFAA22.toInt()).visualOrderText))
                }

                OUTPUT_SLOT -> {
                    list.add(1, ClientTooltipComponent.create(Component.translatable("tooltip.azurum_miner.generator.chance", CONFIG.get<Float>("generator.shardChance") * 100f).withColor(0xFFFFAA00.toInt()).visualOrderText))
                }
            }
        }

        return list
    }
}

class GeneratorRecipe(recipeId: ResourceLocation, val input: EmiStack, val bases: List<GeneratorRecipe>, val power: Int, val lasts: Int, val workaround: EmiIngredientRecipe) : BasicEmiRecipe(GENERATOR_CATEGORY, recipeId, 130, 69) {
    val font: Font = Minecraft.getInstance().font

    init {
        this.inputs.add(input)

        this.inputs.add(workaround.inputs[0])
        this.inputs.add(EmiIngredient.of(Ingredient.of(ModItems.DIMENSIONAL_MATRIX)))
        this.catalysts.add(EmiIngredient.of(Ingredient.of(ModMachines.GENERATOR)))
        this.outputs.add(EmiStack.of(ModItems.ENERGY_SHARD))
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addDrawable(25, 12, 70, 45, { guiGraphics, x, y, _ ->
            guiGraphics.renderOutline(0, 0, 70, 45, 0xFF8B8B8B.toInt())
            guiGraphics.fill(1, 1, 69, 44, 0xFF282828.toInt())

            val currBase = bases[(System.currentTimeMillis() / 1000 % bases.size).toInt()]
            guiGraphics.drawCenteredString(font, getFE(power) + "/t", 35, 6, CommonColors.SOFT_YELLOW)
            guiGraphics.drawCenteredString(font, Component.translatable("tooltip.azurum_miner.generator.base", String.format("%.1fx", currBase.multiplier)), 35, 19, 0xFF7777FF.toInt())
            guiGraphics.drawCenteredString(font, getFE(power * currBase.multiplier) + "/t", 35, 32, 0xFF77FF77.toInt())
        })

        widgets.add(ConnectedSlotWidget(inputs[0], 2, 12, FUEL_SLOT, this))
        widgets.add(ConnectedSlotWidget(inputs[1], 2, 42, BASE_SLOT, this).recipeContext(workaround))
        widgets.add(ConnectedSlotWidget(inputs[2], 102, 12, MATRIX_SLOT))
        widgets.add(ConnectedSlotWidget(outputs[0], 100, 38, OUTPUT_SLOT, this).large(true).backgroundTexture(ResourceLocation.fromNamespaceAndPath("emi", "textures/gui/widgets.png"), 18, 0).recipeContext(EmiInfoRecipe(listOf<EmiIngredient>(), listOf<Component>(), null)))
    }
}