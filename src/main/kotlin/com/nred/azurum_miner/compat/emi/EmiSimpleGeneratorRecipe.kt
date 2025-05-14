package com.nred.azurum_miner.compat.emi

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.compat.emi.EmiPlugin.Companion.SIMPLE_GENERATOR_CATEGORY
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import dev.emi.emi.api.recipe.BasicEmiRecipe
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient

class EmiSimpleGeneratorRecipe(val recipeId: ResourceLocation, val stack: EmiIngredient, val processingTime: Int) : BasicEmiRecipe(SIMPLE_GENERATOR_CATEGORY, recipeId, 144, 18) {
    init {
        this.id = id
        if (stack.emiStacks[0].itemStack.item == Items.LAVA_BUCKET) {
            stack.emiStacks[0].setRemainder(EmiStack.of(Items.BUCKET))
        }
        this.catalysts.add(EmiIngredient.of(Ingredient.of(ModMachines.SIMPLE_GENERATOR)))
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addTexture(EmiTexture.EMPTY_FLAME, 1, 1)
        widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 1, 1, 1000 * processingTime / 20, false, true, true)
        widgets.addSlot(stack, 18, 0).recipeContext(this)
        widgets.addText(Component.literal(getFE(CONFIG.getIntOrElse("simple_generator.energyProduction", 20) * processingTime)), 38, 5, -1, true)
    }
}