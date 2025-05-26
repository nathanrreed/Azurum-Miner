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
data class CrystallizerInput(val state: BlockState, val stack: ItemStack, val fluidStack: FluidStack) : RecipeInput {
    override fun getItem(slot: Int): ItemStack {
        require(slot == 0) { "No item for index $slot" }
        return this.stack
    }

    override fun size(): Int {
        return 1
    }
}

class CrystallizerRecipe(val inputItem: Ingredient, val rate: Float, val inputFluid: FluidStack, val result: ItemStack, val power: Int, val processingTime: Int) : Recipe<CrystallizerInput> {
    override fun matches(input: CrystallizerInput, level: Level): Boolean {
        return this.inputItem.test(input.stack) && FluidStack.isSameFluid(this.inputFluid, input.fluidStack)
    }

    override fun assemble(input: CrystallizerInput, registries: HolderLookup.Provider): ItemStack {
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
        return ModRecipe.CRYSTALLIZER_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return ModRecipe.CRYSTALLIZER_RECIPE_TYPE.get()
    }

    override fun getIngredients(): NonNullList<Ingredient> {
        return NonNullList.of(Ingredient.EMPTY, this.inputItem)
    }
}

class CrystallizerRecipeSerializer : RecipeSerializer<CrystallizerRecipe> {
    override fun codec(): MapCodec<CrystallizerRecipe> {
        return CODEC
    }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, CrystallizerRecipe> {
        return STREAM_CODEC
    }

    companion object {
        val CODEC: MapCodec<CrystallizerRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(CrystallizerRecipe::inputItem),
                Codec.FLOAT.fieldOf("rate").forGetter(CrystallizerRecipe::rate),
                FluidStack.CODEC.fieldOf("fluid_stack").forGetter(CrystallizerRecipe::inputFluid),
                ItemStack.CODEC.fieldOf("result").forGetter(CrystallizerRecipe::result),
                Codec.INT.fieldOf("power").forGetter(CrystallizerRecipe::power),
                Codec.INT.fieldOf("processingTime").forGetter(CrystallizerRecipe::processingTime)
            ).apply(inst, ::CrystallizerRecipe)
        }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, CrystallizerRecipe> = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, CrystallizerRecipe::inputItem,
            ByteBufCodecs.FLOAT, CrystallizerRecipe::rate,
            FluidStack.STREAM_CODEC, CrystallizerRecipe::inputFluid,
            ItemStack.STREAM_CODEC, CrystallizerRecipe::result,
            ByteBufCodecs.INT, CrystallizerRecipe::power,
            ByteBufCodecs.INT, CrystallizerRecipe::processingTime,
            ::CrystallizerRecipe
        )
    }
}

class CrystallizerRecipeBuilder(result: ItemStack, private val inputItem: Ingredient, private val rate: Float, private val inputFluid: FluidStack, private val power: Int, private val processingTime: Int) : SimpleRecipeBuilder(result) {
    override fun save(output: RecipeOutput, key: ResourceLocation) {
        val advancement = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
            .rewards(AdvancementRewards.Builder.recipe(key))
            .requirements(AdvancementRequirements.Strategy.OR)
        this.criteria.forEach { _ -> advancement::addCriterion }
        // Our factory parameters are the result, the block state, and the ingredient.
        val recipe = CrystallizerRecipe(this.inputItem, this.rate, this.inputFluid, this.result, this.power, this.processingTime)

        output.accept(key, recipe, advancement.build(key.withPrefix("recipes/")))
    }
}