package com.nred.azurum_miner.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.FluidStack


@JvmRecord
data class LiquifierInput(val state: BlockState, val stack: ItemStack) : RecipeInput {
    override fun getItem(slot: Int): ItemStack {
        require(slot == 0) { "No item for index $slot" }
        return this.stack
    }

    override fun size(): Int {
        return 1
    }
}

class LiquifierRecipe(val inputItem: Ingredient, val result: FluidStack, val power: Int, val processingTime: Int) : Recipe<LiquifierInput> {
    override fun matches(input: LiquifierInput, level: Level): Boolean {
        return this.inputItem.test(input.stack)
    }

    override fun assemble(input: LiquifierInput, registries: HolderLookup.Provider): ItemStack {
        return ItemStack.EMPTY
    }

    override fun isSpecial(): Boolean {
        return true
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return true
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return ItemStack.EMPTY
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return ModRecipe.LIQUIFIER_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return ModRecipe.LIQUIFIER_RECIPE_TYPE.get()
    }

    override fun getIngredients(): NonNullList<Ingredient> {
        return NonNullList.of(Ingredient.EMPTY, this.inputItem)
    }
}

class LiquifierRecipeSerializer : RecipeSerializer<LiquifierRecipe> {
    override fun codec(): MapCodec<LiquifierRecipe> {
        return CODEC
    }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, LiquifierRecipe> {
        return STREAM_CODEC
    }

    companion object {
        val CODEC: MapCodec<LiquifierRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(LiquifierRecipe::inputItem),
                FluidStack.CODEC.fieldOf("result").forGetter(LiquifierRecipe::result),
                Codec.INT.fieldOf("power").forGetter(LiquifierRecipe::power),
                Codec.INT.fieldOf("processingTime").forGetter(LiquifierRecipe::processingTime)
            ).apply(inst, ::LiquifierRecipe)
        }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, LiquifierRecipe> = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, LiquifierRecipe::inputItem,
            FluidStack.STREAM_CODEC, LiquifierRecipe::result,
            ByteBufCodecs.INT, LiquifierRecipe::power,
            ByteBufCodecs.INT, LiquifierRecipe::processingTime,
            ::LiquifierRecipe
        )
    }
}

class LiquifierRecipeBuilder(result: FluidStack, private val inputItem: Ingredient, private val power: Int, private val processingTime: Int) : SimpleFluidRecipeBuilder(result) {
    override fun save(output: RecipeOutput, key: ResourceLocation) {
        val advancement = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
            .rewards(AdvancementRewards.Builder.recipe(key))
            .requirements(AdvancementRequirements.Strategy.OR)
        this.criteria.forEach { advancement::addCriterion }
        // Our factory parameters are the result, the block state, and the ingredient.
        val recipe = LiquifierRecipe(this.inputItem, this.result, this.power, this.processingTime)

        output.accept(key, recipe, advancement.build(key.withPrefix("recipes/")))
    }
}