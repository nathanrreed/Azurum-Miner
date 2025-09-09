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

@JvmRecord
data class TransmogrifierInput(val state: BlockState, val stack: ItemStack) : RecipeInput {
    override fun getItem(slot: Int): ItemStack {
        return when (slot) {
            1 -> this.stack
            else -> {
                require(true) { "No item for index $slot" }; return ItemStack.EMPTY
            }
        }
    }

    override fun size(): Int {
        return 2
    }
}

class TransmogrifierRecipe(val inputItem: Ingredient, val result: ItemStack, val powerMult: Double, val processingTime: Int) : Recipe<TransmogrifierInput> {
    override fun matches(input: TransmogrifierInput, level: Level): Boolean {
        return this.inputItem.test(input.stack)
    }

    override fun assemble(input: TransmogrifierInput, registries: HolderLookup.Provider): ItemStack {
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
        return ModRecipe.TRANSMOGRIFIER_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return ModRecipe.TRANSMOGRIFIER_RECIPE_TYPE.get()
    }

    override fun getIngredients(): NonNullList<Ingredient> {
        return NonNullList.of(Ingredient.EMPTY, this.inputItem)
    }
}

class TransmogrifierRecipeSerializer : RecipeSerializer<TransmogrifierRecipe> {
    override fun codec(): MapCodec<TransmogrifierRecipe> {
        return CODEC
    }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, TransmogrifierRecipe> {
        return STREAM_CODEC
    }

    companion object {
        val CODEC: MapCodec<TransmogrifierRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(TransmogrifierRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(TransmogrifierRecipe::result),
                Codec.DOUBLE.fieldOf("power").forGetter(TransmogrifierRecipe::powerMult),
                Codec.INT.fieldOf("processingTime").forGetter(TransmogrifierRecipe::processingTime)
            ).apply(inst, ::TransmogrifierRecipe)
        }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, TransmogrifierRecipe> = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, TransmogrifierRecipe::inputItem,
            ItemStack.STREAM_CODEC, TransmogrifierRecipe::result,
            ByteBufCodecs.DOUBLE, TransmogrifierRecipe::powerMult,
            ByteBufCodecs.INT, TransmogrifierRecipe::processingTime,
            ::TransmogrifierRecipe
        )
    }
}

class TransmogrifierRecipeBuilder(result: ItemStack, private val inputItem: Ingredient, private val powerMult: Double, private val processingTime: Int) : SimpleRecipeBuilder(result) {
    override fun save(output: RecipeOutput, key: ResourceLocation) {
        val advancement = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
            .rewards(AdvancementRewards.Builder.recipe(key))
            .requirements(AdvancementRequirements.Strategy.OR)

        val recipe = TransmogrifierRecipe(this.inputItem, this.result, this.powerMult, this.processingTime)

        output.accept(key, recipe, advancement.build(key.withPrefix("recipes/")))
    }
}