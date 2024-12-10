package com.nred.nredmod.recipe

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
data class InfuserInput(val state: BlockState, val stack: ItemStack, val catalyst: ItemStack) : RecipeInput {
    override fun getItem(slot: Int): ItemStack {
        return when (slot) {
            1 -> this.stack
            2 -> this.catalyst
            else -> {
                require(true) { "No item for index $slot" }; return ItemStack.EMPTY
            }
        }
    }

    override fun size(): Int {
        return 2
    }
}

class InfuserRecipe(val inputItem: Ingredient, val catalyst: Ingredient, val inputFluid: FluidStack, val result: ItemStack, val power: Int, val processingTime: Int) : Recipe<InfuserInput> {
    override fun matches(input: InfuserInput, level: Level): Boolean {
        return this.inputItem.test(input.stack) || this.catalyst.test(input.catalyst)
    }

    override fun assemble(input: InfuserInput, registries: HolderLookup.Provider): ItemStack {
        return this.result.copy()
    }

    override fun isSpecial(): Boolean {
        return true
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return true
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return this.result.copy()
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return ModRecipe.INFUSER_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return ModRecipe.INFUSER_RECIPE_TYPE.get()
    }

    override fun getIngredients(): NonNullList<Ingredient> {
        return NonNullList.of(Ingredient.EMPTY, this.inputItem, this.catalyst)
    }
}

class InfuserRecipeSerializer : RecipeSerializer<InfuserRecipe> {
    override fun codec(): MapCodec<InfuserRecipe> {
        return CODEC
    }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> {
        return STREAM_CODEC
    }

    companion object {
        val CODEC: MapCodec<InfuserRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(InfuserRecipe::inputItem),
                Ingredient.CODEC.fieldOf("catalyst").forGetter(InfuserRecipe::catalyst),
                FluidStack.CODEC.fieldOf("fluid_stack").forGetter(InfuserRecipe::inputFluid),
                ItemStack.CODEC.fieldOf("result").forGetter(InfuserRecipe::result),
                Codec.INT.fieldOf("power").forGetter(InfuserRecipe::power),
                Codec.INT.fieldOf("processingTime").forGetter(InfuserRecipe::processingTime)
            ).apply(inst, ::InfuserRecipe)
        }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, InfuserRecipe::inputItem,
            Ingredient.CONTENTS_STREAM_CODEC, InfuserRecipe::catalyst,
            FluidStack.STREAM_CODEC, InfuserRecipe::inputFluid,
            ItemStack.STREAM_CODEC, InfuserRecipe::result,
            ByteBufCodecs.INT, InfuserRecipe::power,
            ByteBufCodecs.INT, InfuserRecipe::processingTime,
            ::InfuserRecipe
        )
    }
}

class InfuserRecipeBuilder(result: ItemStack, private val inputItem: Ingredient, private val catalyst: Ingredient, private val inputFluid: FluidStack, private val power: Int, private val processingTime: Int) : SimpleRecipeBuilder(result) {
    override fun save(output: RecipeOutput, key: ResourceLocation) {
        val advancement = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
            .rewards(AdvancementRewards.Builder.recipe(key))
            .requirements(AdvancementRequirements.Strategy.OR)

        val recipe = InfuserRecipe(this.inputItem, this.catalyst, this.inputFluid, this.result, this.power, this.processingTime)

        output.accept(key, recipe, advancement.build(key.withPrefix("recipes/")))
    }
}