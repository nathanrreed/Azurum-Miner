package com.nred.azurum_miner.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.data.recipes.RecipeBuilder
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
data class MinerInput(val state: BlockState) : RecipeInput {
    override fun getItem(slot: Int): ItemStack {
        require(true) { "No item for index $slot" }; return ItemStack.EMPTY
    }

    override fun size(): Int {
        return 0
    }
}

class MinerRecipe(val result: Ingredient, val tier: Int) : Recipe<MinerInput> {
    override fun matches(input: MinerInput, level: Level): Boolean {
        return false
    }

    override fun isSpecial(): Boolean {
        return true
    }

    override fun assemble(input: MinerInput, registries: HolderLookup.Provider): ItemStack {
        return this.result.items[0].copy()
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return true
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return this.result.items[0].copy()
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return ModRecipe.MINER_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return when (this.tier) {
            1 -> ModRecipe.MINER_TIER1_RECIPE_TYPE.get()
            2 -> ModRecipe.MINER_TIER2_RECIPE_TYPE.get()
            3 -> ModRecipe.MINER_TIER3_RECIPE_TYPE.get()
            4 -> ModRecipe.MINER_TIER4_RECIPE_TYPE.get()
            else -> ModRecipe.MINER_TIER5_RECIPE_TYPE.get()
        }
    }

    override fun getIngredients(): NonNullList<Ingredient> {
        return NonNullList.of(Ingredient.EMPTY)
    }
}

class MinerRecipeSerializer : RecipeSerializer<MinerRecipe> {
    override fun codec(): MapCodec<MinerRecipe> {
        return CODEC
    }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, MinerRecipe> {
        return STREAM_CODEC
    }

    companion object {
        val CODEC: MapCodec<MinerRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                Ingredient.CODEC.fieldOf("result").forGetter(MinerRecipe::result),
                Codec.INT.fieldOf("tier").forGetter(MinerRecipe::tier)
            ).apply(inst, ::MinerRecipe)
        }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MinerRecipe> = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, MinerRecipe::result,
            ByteBufCodecs.INT, MinerRecipe::tier,
            ::MinerRecipe
        )
    }
}

class MinerRecipeBuilder(val result: Ingredient, val tier: Int) : RecipeBuilder {
    private val criteria = LinkedHashMap<String, Criterion<*>>()
    private var group: String? = null

    override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder {
        this.criteria[name] = criterion
        return this
    }

    override fun group(group: String?): RecipeBuilder {
        this.group = group
        return this
    }

    override fun getResult(): Item {
        return result.items[0].item
    }

    override fun save(output: RecipeOutput, key: ResourceLocation) {
        val advancement = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
            .rewards(AdvancementRewards.Builder.recipe(key))
            .requirements(AdvancementRequirements.Strategy.OR)

        val recipe = MinerRecipe(this.result, tier)

        output.accept(key, recipe, advancement.build(key.withPrefix("recipes/")))
    }
}