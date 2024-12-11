package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.data.event.GatherDataEvent

@Mod(AzurumMiner.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object DataGenerators {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookupProvider = event.lookupProvider

        generator.addProvider(
            event.includeServer(),
            LootTableProvider(
                packOutput,
                emptySet(),
                listOf(LootTableProvider.SubProviderEntry(::ModBlockLootTableProvider, LootContextParamSets.BLOCK)),
                lookupProvider
            )
        )

        val blockTagProvider = ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper)
        generator.addProvider(event.includeServer(), blockTagProvider)
        generator.addProvider(event.includeServer(), ModItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper))
        generator.addProvider(event.includeServer(), ModRecipeProvider(packOutput, lookupProvider))
        generator.addProvider(event.includeClient(), ModItemModelProvider(packOutput, existingFileHelper))
        generator.addProvider(event.includeClient(), ModBlockStateProvider(packOutput, existingFileHelper))
        generator.addProvider(event.includeServer(), ModDatapackProvider(packOutput, lookupProvider))
    }
}