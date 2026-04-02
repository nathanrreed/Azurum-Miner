package com.nred.azurum_miner.datagen;

import com.nred.azurum_miner.registration.OreRegistration;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.util.Helpers.azLoc;

@Mod(MODID)
@EventBusSubscriber
public class DataGenerators {
    public static final ResourceKey<ConfiguredFeature<?, ?>> AZURUM_ORE_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, azLoc("azurum_ore"));
    public static final ResourceKey<PlacedFeature> AZURUM_ORE_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, azLoc("azurum_ore"));
    public static final ResourceKey<BiomeModifier> AZURUM_ORE_BIOME_KEY = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, azLoc("azurum_ore"));

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects( // TODO move?
                new RegistrySetBuilder()
                        .add(Registries.CONFIGURED_FEATURE, bootstrap -> {
                            TagMatchTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
                            TagMatchTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

                            bootstrap.register(AZURUM_ORE_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(stoneReplaceables, OreRegistration.Azurum.ore.get().defaultBlockState()), OreConfiguration.target(deepslateReplaceables, OreRegistration.Azurum.deepslate_ore.get().defaultBlockState())), 5)));
                        })
                        .add(Registries.PLACED_FEATURE, bootstrap -> {
                            HolderGetter<ConfiguredFeature<?, ?>> configured_feature = bootstrap.lookup(Registries.CONFIGURED_FEATURE);
                            bootstrap.register(AZURUM_ORE_PLACED_KEY, new PlacedFeature(configured_feature.getOrThrow(AZURUM_ORE_CONFIGURED_KEY), List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(4)), BiomeFilter.biome())));
                        })
                        .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, bootstrap -> {
                            HolderGetter<PlacedFeature> placedFeature = bootstrap.lookup(Registries.PLACED_FEATURE);
                            HolderGetter<Biome> biomes = bootstrap.lookup(Registries.BIOME);
                            bootstrap.register(AZURUM_ORE_BIOME_KEY, new BiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD), HolderSet.direct(placedFeature.getOrThrow(AZURUM_ORE_PLACED_KEY)), GenerationStep.Decoration.UNDERGROUND_ORES));
                        }),
                conditions -> {
                },
                Set.of(MODID)
        );

        event.createProvider(ModLanguageProvider::new);
        event.createProvider(ModRecipeProvider.Runner::new);
        event.createProvider(ModModelProvider::new);
        event.createProvider(ModItemTagsProvider::new);
        event.createProvider(ModBlockTagsProvider::new);


        event.createProvider(((packOutput, lookupProvider) -> new LootTableProvider(
                packOutput,
                Set.of(),
                List.of(
                        new LootTableProvider.SubProviderEntry(ModBlockLootSubProvider::new, LootContextParamSets.BLOCK)
                ),
                lookupProvider
        )));

        DataGenerator.PackGenerator basePack = event.getGenerator().getBuiltinDatapack(true, MODID, "base_pack");

//        basePack.addProvider(output -> ...);
    }
}