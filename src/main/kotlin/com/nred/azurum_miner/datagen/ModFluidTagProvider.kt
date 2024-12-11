package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.FluidTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModFluidTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper?
) : FluidTagsProvider(output, lookupProvider, AzurumMiner.ID, existingFileHelper) {

    override fun addTags(provider: HolderLookup.Provider) {

    }
}