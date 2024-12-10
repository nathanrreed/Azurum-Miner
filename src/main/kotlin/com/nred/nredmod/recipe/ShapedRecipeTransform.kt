package com.nred.nredmod.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.common.conditions.ICondition

class ShapedRecipeTransformSerializer : RecipeSerializer<ShapedRecipeWithComponents> {
    override fun codec(): MapCodec<ShapedRecipeWithComponents> {
        return CODEC
    }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, ShapedRecipeWithComponents> {
        return STREAM_CODEC
    }

    companion object {
        val CODEC: MapCodec<ShapedRecipeWithComponents> = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipeWithComponents::groupString),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipeWithComponents::bookCategory),
                ShapedRecipePattern.MAP_CODEC.forGetter(ShapedRecipeWithComponents::pattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ShapedRecipeWithComponents::resultStack),
                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipeWithComponents::showNoti),
                Codec.INT.fieldOf("index").forGetter(ShapedRecipeWithComponents::transformIndex)
            ).apply(it, ::ShapedRecipeWithComponents)
        }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ShapedRecipeWithComponents> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ShapedRecipeWithComponents::groupString,
            CraftingBookCategory.STREAM_CODEC, ShapedRecipeWithComponents::bookCategory,
            ShapedRecipePattern.STREAM_CODEC, ShapedRecipeWithComponents::pattern,
            ItemStack.STREAM_CODEC, ShapedRecipeWithComponents::resultStack,
            ByteBufCodecs.BOOL, ShapedRecipeWithComponents::showNoti,
            ByteBufCodecs.INT, ShapedRecipeWithComponents::transformIndex,
            ::ShapedRecipeWithComponents
        )
    }
}

class ShapedRecipeBuilderTransform(val category: RecipeCategory, val result: ItemLike, val count: Int, val transformIndex: Int) : ShapedRecipeBuilder(category, result, count) {
    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val fakeRecipeOutput = FakeRecipeOutputTransform()
        super.save(fakeRecipeOutput, id)
        val recipe = fakeRecipeOutput.recipe

        if (recipe is ShapedRecipe)
            recipeOutput.accept(id, ShapedRecipeWithComponents(recipe.group, RecipeBuilder.determineBookCategory(this.category), recipe.pattern, ItemStack(result, count), recipe.showNotification(), transformIndex), fakeRecipeOutput.advancement)
    }
}

class ShapedRecipeWithComponents(val groupString: String, val bookCategory: CraftingBookCategory, val pattern: ShapedRecipePattern, val resultStack: ItemStack, val showNoti: Boolean, val transformIndex: Int) : ShapedRecipe(groupString, bookCategory, pattern, resultStack, showNoti) {
    override fun assemble(input: CraftingInput, registries: HolderLookup.Provider): ItemStack {
        val itemStack = input.getItem(transformIndex).transmuteCopy(resultStack.item, resultStack.count)
        itemStack.applyComponents(resultStack.componentsPatch)
        return itemStack
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return ModRecipe.SHAPED_RECIPE_TRANSFORM_SERIALIZER.get()
    }
}

class FakeRecipeOutputTransform : RecipeOutput {
    lateinit var recipe: Recipe<*>
    var advancement: AdvancementHolder? = null
    override fun accept(location: ResourceLocation, recipe: Recipe<*>, advancement: AdvancementHolder?) {
        this.recipe = recipe
        this.advancement = advancement
    }

    override fun accept(id: ResourceLocation, recipe: Recipe<*>, advancement: AdvancementHolder?, vararg conditions: ICondition?) {
        this.recipe = recipe
        this.advancement = advancement
    }

    override fun advancement(): Advancement.Builder {
        return Advancement.Builder()
    }
}