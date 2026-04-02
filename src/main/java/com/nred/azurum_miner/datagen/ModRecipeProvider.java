package com.nred.azurum_miner.datagen;

import com.nred.azurum_miner.util.OreMaterial;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.registration.BlockRegistration.*;
import static com.nred.azurum_miner.registration.ItemRegistration.*;
import static com.nred.azurum_miner.registration.OreRegistration.*;

public class ModRecipeProvider extends RecipeProvider {
    protected ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    @Override
    protected void buildRecipes() {
        for (OreMaterial oreMaterial : ORE_MATERIALS) {
            ArrayList<ItemLike> ORE_SMELTABLES = new ArrayList<>(List.of(oreMaterial.ore, oreMaterial.deepslate_ore));
            String ingot_name = oreMaterial.name + "_ingot";

            this.nineBlockStorageRecipesWithCustomPacking(RecipeCategory.MISC, oreMaterial.nugget, RecipeCategory.MISC, oreMaterial.ingot, ingot_name + "_from_nuggets", ingot_name);
            this.nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, oreMaterial.ingot, RecipeCategory.BUILDING_BLOCKS, oreMaterial.ingot_block, ingot_name, ingot_name);
            if (oreMaterial instanceof OreMaterial.OreMaterialHasRaw ore_material_has_raw) {
                ORE_SMELTABLES.add(ore_material_has_raw.raw);
                this.nineBlockStorageRecipes(RecipeCategory.MISC, ore_material_has_raw.raw, RecipeCategory.BUILDING_BLOCKS, ore_material_has_raw.raw_block);
            }

            ItemLike smelting_output = oreMaterial.ingot;
            if (oreMaterial instanceof OreMaterial.OreMaterialHasShard ore_material_has_shard) {
                smelting_output = ore_material_has_shard.shard;
            }

            this.oreBlasting(ORE_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, smelting_output, 0.7F, 100, ingot_name);
            this.oreSmelting(ORE_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, smelting_output, 0.7F, 200, ingot_name);

            this.shaped(RecipeCategory.MISC, oreMaterial.gear).pattern(" N ").pattern("NBN").pattern(" N ")
                    .define('N', oreMaterial.nugget).define('B', oreMaterial.ingot_block)
                    .unlockedBy(getHasName(oreMaterial.ingot_block), has(oreMaterial.ingot_block)).save(this.output);
        }

        this.nineBlockStorageRecipes(RecipeCategory.MISC, CONGLOMERATE_OF_ORE_SHARD, RecipeCategory.BUILDING_BLOCKS, CONGLOMERATE_OF_ORE_BLOCK);

        this.shaped(RecipeCategory.MISC, SIMPLE_VOID_PROCESSOR).pattern(" C ").pattern("GPG").pattern(" C ")
                .define('C', Azurum.shard).define('G', Items.GLASS).define('P', Items.ENDER_PEARL)
                .unlockedBy(getHasName(Azurum.shard), has(Azurum.shard)).save(this.output);

        this.shaped(RecipeCategory.MISC, EMPTY_DIMENSIONAL_MATRIX).pattern("AEA").pattern("NPN").pattern("AEA")
                .define('P', Items.ENDER_EYE).define('A', Azurum.nugget).define('N', NETHER_DIAMOND).define('E', ENDER_DIAMOND)
                .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(this.output);

        this.shaped(RecipeCategory.MISC, VOID_PROCESSOR).pattern("SLS").pattern("RPR").pattern("GAG")
                .define('G', Galibium.gear).define('P', SIMPLE_VOID_PROCESSOR).define('A', Azurum.gear).define('R', Items.REDSTONE).define('L', Items.LAPIS_LAZULI).define('S', Items.GLOWSTONE_DUST)
                .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(this.output);

        this.shaped(RecipeCategory.MISC, ELABORATE_VOID_PROCESSOR).pattern("RLR").pattern("PGP").pattern("GDG")
                .define('G', Thelxium.gear).define('P', VOID_PROCESSOR).define('D', Items.DIAMOND).define('L', Items.REDSTONE_LAMP).define('R', Items.REDSTONE)
                .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(this.output);

        this.shaped(RecipeCategory.MISC, COMPLEX_VOID_PROCESSOR).pattern("FGF").pattern("DCD").pattern("PGP")
                .define('G', Palestium.gear).define('P', ELABORATE_VOID_PROCESSOR).define('F', Thelxium.gear).define('C', Items.CRAFTER).define('D', CONGLOMERATE_OF_ORE_SHARD)
                .unlockedBy(getHasName(SIMPLE_VOID_PROCESSOR), has(SIMPLE_VOID_PROCESSOR)).save(this.output);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, ENERGIZED_OBSIDIAN).pattern("NEN").pattern("EOE").pattern("NEN")
                .define('E', ENERGIZED_SHARD).define('O', Items.OBSIDIAN).define('N', NETHER_DIAMOND)
                .unlockedBy(getHasName(ENERGIZED_SHARD), has(ENERGIZED_SHARD)).save(this.output);

        this.shaped(RecipeCategory.DECORATIONS, TANK_BLOCK).pattern("IGI").pattern("G G").pattern("IGI")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS)
                .unlockedBy(getHasName(Items.GLASS), has(Items.GLASS)).save(this.output);

        this.shapeless(RecipeCategory.MISC, SEED_CRYSTAL, 4).requires(Azurum.shard, 2).requires(Ingredient.of(Items.DIAMOND, Items.EMERALD), 1).requires(Ingredient.of(Items.QUARTZ, Items.PRISMARINE_CRYSTALS), 1)
                .unlockedBy(getHasName(Items.QUARTZ), has(Items.QUARTZ)).save(this.output);

    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider provider, @NonNull RecipeOutput output) {
            return new ModRecipeProvider(provider, output);
        }

        @Override
        public @NonNull String getName() {
            return MODID + ".recipe_provider";
        }
    }
}