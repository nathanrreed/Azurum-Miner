package com.nred.azurum_miner.recipe

import com.nred.azurum_miner.AzurumMiner
import net.minecraft.advancements.Criterion
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.material.Fluid
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModRecipe {
    val RECIPE_TYPES: DeferredRegister<RecipeType<*>?> = DeferredRegister.create(Registries.RECIPE_TYPE, AzurumMiner.ID)

    private fun <T : Recipe<*>> register(identifier: String): DeferredHolder<RecipeType<*>?, out RecipeType<T>> {
        return RECIPE_TYPES.register(identifier) { registryName ->
            object : RecipeType<T> {
                override fun toString(): String {
                    return registryName.toString()
                }
            }
        }
    }

    val CRYSTALLIZER_RECIPE_TYPE = register<CrystallizerRecipe>("crystallizer_recipe")
    val LIQUIFIER_RECIPE_TYPE = register<LiquifierRecipe>("liquifier_recipe")
    val INFUSER_RECIPE_TYPE = register<InfuserRecipe>("infuser_recipe")
    val TRANSMOGRIFIER_RECIPE_TYPE = register<TransmogrifierRecipe>("transmogrifier_recipe")
    val GENERATOR_RECIPE_TYPE = register<GeneratorRecipe>("generator_recipe")
    val SHAPED_RECIPE_TRANSFORM_TYPE = register<ShapedRecipeWithComponents>("crafting_shaped_transform")
    val MINER_TIER1_RECIPE_TYPE = register<MinerRecipe>("miner_recipe_tier1")
    val MINER_TIER2_RECIPE_TYPE = register<MinerRecipe>("miner_recipe_tier2")
    val MINER_TIER3_RECIPE_TYPE = register<MinerRecipe>("miner_recipe_tier3")
    val MINER_TIER4_RECIPE_TYPE = register<MinerRecipe>("miner_recipe_tier4")
    val MINER_TIER5_RECIPE_TYPE = register<MinerRecipe>("miner_recipe_tier5")

    val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>?> = DeferredRegister.create(Registries.RECIPE_SERIALIZER, AzurumMiner.ID)

    val CRYSTALLIZER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("crystallizer_recipe") { -> CrystallizerRecipeSerializer() }
    val LIQUIFIER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("liquifier_recipe") { -> LiquifierRecipeSerializer() }
    val INFUSER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("infuser_recipe") { -> InfuserRecipeSerializer() }
    val TRANSMOGRIFIER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("transmogrifier_recipe") { -> TransmogrifierRecipeSerializer() }
    val GENERATOR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("generator_recipe") { -> GeneratorRecipeSerializer() }
    val SHAPED_RECIPE_TRANSFORM_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_shaped_transform") { -> ShapedRecipeTransformSerializer() }
    val MINER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("miner_recipe") { -> MinerRecipeSerializer() }

    fun register(eventBus: IEventBus) {
        RECIPE_TYPES.register(eventBus)
        RECIPE_SERIALIZERS.register(eventBus)
    }
}

abstract class SimpleRecipeBuilder(protected val result: ItemStack) : RecipeBuilder {
    protected val criteria = LinkedHashMap<String, Criterion<*>>()
    protected var group: String? = null

    override fun unlockedBy(name: String, criterion: Criterion<*>): SimpleRecipeBuilder {
        this.criteria[name] = criterion
        return this
    }

    override fun group(group: String?): SimpleRecipeBuilder {
        this.group = group
        return this
    }

    override fun getResult(): Item {
        return result.item
    }
}

abstract class SimpleFluidRecipeBuilder(protected val result: FluidStack) : RecipeBuilder {
    protected val criteria = LinkedHashMap<String, Criterion<*>>()

    protected var group: String? = null

    override fun unlockedBy(name: String, criterion: Criterion<*>): SimpleFluidRecipeBuilder {
        criteria.put(name, criterion)
        return this
    }

    override fun group(group: String?): SimpleFluidRecipeBuilder {
        this.group = group
        return this
    }

    override fun getResult(): Item {
        return ItemStack.EMPTY.item
    }

    fun getFluidResult(): Fluid {
        return this.result.fluid
    }

    override fun save(recipeOutput: RecipeOutput) {
        this.save(recipeOutput, getDefaultRecipeId(this.getFluidResult()))
    }

    fun getDefaultRecipeId(fluid: Fluid): ResourceLocation {
        return BuiltInRegistries.FLUID.getKey(fluid)
    }
}

abstract class SimpleItemFluidRecipeBuilder(protected val result: ItemStack, protected val fluidResult: FluidStack) : RecipeBuilder {
    protected val criteria = LinkedHashMap<String, Criterion<*>>()

    protected var group: String? = null

    override fun unlockedBy(name: String, criterion: Criterion<*>): SimpleItemFluidRecipeBuilder {
        criteria.put(name, criterion)
        return this
    }

    override fun group(group: String?): SimpleItemFluidRecipeBuilder {
        this.group = group
        return this
    }

    override fun getResult(): Item {
        return result.item
    }

    fun getFluidResult(): Fluid {
        return this.fluidResult.fluid
    }
}