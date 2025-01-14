package com.nred.azurum_miner.datagen

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.item.ModItems
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.EntityTypeTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModEntityTypeTagsProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>, existingFileHelper: ExistingFileHelper) : EntityTypeTagsProvider(output, registries, AzurumMiner.ID, existingFileHelper) {
    override fun addTags(provider: HolderLookup.Provider) {
        tag(ModItems.EMPTY_DIMENSIONAL_MATRIX_TAG_TYPE).add(ModItems.EMPTY_DIMENSIONAL_MATRIX_TYPE.get())
    }
}