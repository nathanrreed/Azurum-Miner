package com.nred.azurum_miner.compat.jei

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.recipe.GeneratorRecipe
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.CommonColors
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient


class GeneratorCategory(helper: IGuiHelper, val bases: List<GeneratorRecipe>) : IRecipeCategory<GeneratorRecipe> {
    companion object {
        val UID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "generator")
        val TYPE = RecipeType(UID, GeneratorRecipe::class.java)
        val font: Font = Minecraft.getInstance().font
    }

    private val icon: IDrawable = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ItemStack(ModMachines.GENERATOR.get()))
    private val bases_items = Ingredient.of(bases.map { it.input }.stream())


    override fun getWidth(): Int {
        return 130
    }

    override fun getHeight(): Int {
        return 69
    }

    override fun getRecipeType(): RecipeType<GeneratorRecipe> {
        return TYPE
    }

    override fun getTitle(): Component {
        return Component.translatable("menu.title.azurum_miner.generator")
    }

    override fun getIcon(): IDrawable {
        return this.icon
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: GeneratorRecipe, focuses: IFocusGroup) {
        builder.addInputSlot(2, 12).addIngredients(Ingredient.of(recipe.input)).setStandardSlotBackground().addRichTooltipCallback { recipeSlotView, builder -> builder.add(Component.translatable("tooltip.azurum_miner.generator.lasts", getTime(recipe.lasts)).withColor(0xFFFFAA22.toInt())) }
        builder.addInputSlot(2, 42).addIngredients(bases_items).setStandardSlotBackground().addRichTooltipCallback { recipeSlotView, builder -> builder.add(Component.translatable("tooltip.azurum_miner.generator.lasts", getTime(bases.first { it.input.`is`(recipeSlotView.displayedItemStack.get().item) }.lasts)).withColor(0xFFFFAA22.toInt())) }
        builder.addInputSlot(104, 12).addIngredients(Ingredient.of(ModItems.DIMENSIONAL_MATRIX)).setStandardSlotBackground()
        builder.addOutputSlot(104, 38).addItemStack(ModItems.ENERGY_SHARD.toStack()).setOutputSlotBackground().addRichTooltipCallback { recipeSlotView, builder -> builder.add(Component.translatable("tooltip.azurum_miner.generator.chance", CONFIG.get<Float>("generator.shardChance") * 100f).withColor(0xFFFFAA00.toInt())) }
    }

    override fun draw(recipe: GeneratorRecipe, recipeSlotsView: IRecipeSlotsView, guiGraphics: GuiGraphics, mouseX: Double, mouseY: Double) {
        guiGraphics.renderOutline(25, 12, 70, 45, 0xFF8B8B8B.toInt())
        guiGraphics.fill(26, 13, 94, 57, 0xFF282828.toInt())

        val currBase = bases[(System.currentTimeMillis() / 1000 % bases.size).toInt()]
        guiGraphics.drawCenteredString(font, getFE(recipe.power) + "/t", 60, 18, CommonColors.SOFT_YELLOW)
        guiGraphics.drawCenteredString(font, Component.translatable("tooltip.azurum_miner.generator.base", String.format("%.1fx", currBase.multiplier)), 60, 31, 0xFF6666FF.toInt())
        guiGraphics.drawCenteredString(font, getFE(recipe.power * currBase.multiplier) + "/t", 60, 46, 0xFF77FF77.toInt())
    }
}