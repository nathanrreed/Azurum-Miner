package com.nred.azurum_miner.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.nred.azurum_miner.AzurumMiner
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

@JvmRecord
data class GeneratorInput(val state: BlockState, val stack: ItemStack, val typeName: String) : RecipeInput {
    override fun getItem(slot: Int): ItemStack {
        return when (slot) {
            0 -> this.stack
            else -> {
                require(true) { "No item for index $slot" }; return ItemStack.EMPTY
            }
        }
    }

    override fun size(): Int {
        return 1
    }
}

class GeneratorRecipe(val input: ItemStack, val power: Int = 0, val multiplier: Float = 0f, val lasts: Int = 0, val typeName: String) : Recipe<GeneratorInput> {
    override fun matches(input: GeneratorInput, level: Level): Boolean {
        return this.input.`is`(input.stack.item) && input.typeName == this.typeName
    }

    override fun assemble(input: GeneratorInput, registries: HolderLookup.Provider): ItemStack {
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
        return ModRecipe.GENERATOR_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return ModRecipe.GENERATOR_RECIPE_TYPE.get()
    }

    override fun getIngredients(): NonNullList<Ingredient> {
        return NonNullList.of(Ingredient.EMPTY, Ingredient.of(this.input))
    }
}

class GeneratorRecipeSerializer : RecipeSerializer<GeneratorRecipe> {
    override fun codec(): MapCodec<GeneratorRecipe> {
        return CODEC
    }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> {
        return STREAM_CODEC
    }

    companion object {
        val CODEC: MapCodec<GeneratorRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                ItemStack.CODEC.fieldOf("input").forGetter(GeneratorRecipe::input),
                Codec.INT.optionalFieldOf("power", 0).forGetter(GeneratorRecipe::power),
                Codec.FLOAT.optionalFieldOf("multiplier", 0f).forGetter(GeneratorRecipe::multiplier),
                Codec.INT.optionalFieldOf("lasts", 0).forGetter(GeneratorRecipe::lasts),
                Codec.STRING.fieldOf("type_name").forGetter(GeneratorRecipe::typeName)
            ).apply(inst, ::GeneratorRecipe)
        }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> = StreamCodec.composite(
            ItemStack.STREAM_CODEC, GeneratorRecipe::input,
            ByteBufCodecs.INT, GeneratorRecipe::power,
            ByteBufCodecs.FLOAT, GeneratorRecipe::multiplier,
            ByteBufCodecs.INT, GeneratorRecipe::lasts,
            ByteBufCodecs.STRING_UTF8, GeneratorRecipe::typeName,
            ::GeneratorRecipe
        )
    }
}

class GeneratorRecipeBuilder : SimpleRecipeBuilder {
    val recipe: GeneratorRecipe
    val type_name: String

    companion object {
        var map = mutableMapOf("base" to 0, "fuel" to 1)
        fun increment(type: String): Int {
            val rtn = map.getValue(type)
            map.put(type, rtn + 1)
            return rtn
        }
    }

    constructor(inputItem: Item, multiplier: Float, lasts: Int) : super(ItemStack.EMPTY) {
        type_name = "base"
        recipe = GeneratorRecipe(ItemStack(inputItem), multiplier = multiplier, lasts = lasts, typeName = this.type_name)
    }

    constructor(inputItem: Item, power: Int, lasts: Int) : super(ItemStack.EMPTY) {
        type_name = "fuel"
        recipe = GeneratorRecipe(ItemStack(inputItem), power = power, lasts = lasts, typeName = this.type_name)
    }

    override fun save(output: RecipeOutput, key: ResourceLocation) {
        output.accept(ResourceLocation.parse(AzurumMiner.ID + ":generator_" + this.type_name + increment(this.type_name)), this.recipe, null)
    }
}