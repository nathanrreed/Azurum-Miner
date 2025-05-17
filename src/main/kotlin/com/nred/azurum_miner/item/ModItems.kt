package com.nred.azurum_miner.item

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.entity.EmptyMatrixItemEntity
import com.nred.azurum_miner.entity.ModEntities.ENTITY_TYPES
import com.nred.azurum_miner.util.Helpers.azLoc
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(AzurumMiner.ID)

    val SIMPLE_VOID_PROCESSOR: DeferredItem<Item> = ITEMS.register("simple_void_processor") { -> Item(Properties()) }
    val VOID_PROCESSOR: DeferredItem<Item> = ITEMS.register("void_processor") { -> Item(Properties()) }
    val ELABORATE_VOID_PROCESSOR: DeferredItem<Item> = ITEMS.register("elaborate_void_processor") { -> Item(Properties()) }
    val COMPLEX_VOID_PROCESSOR: DeferredItem<Item> = ITEMS.register("complex_void_processor") { -> Item(Properties()) }

    val CONGLOMERATE_OF_ORE_SHARD: DeferredItem<Item> = ITEMS.register("conglomerate_of_ore_shard") { -> Item(Properties()) }
    val NETHER_DIAMOND: DeferredItem<Item> = ITEMS.register("nether_diamond") { -> Item(Properties()) }
    val ENDER_DIAMOND: DeferredItem<Item> = ITEMS.register("ender_diamond") { -> Item(Properties()) }
    val ENERGY_SHARD: DeferredItem<Item> = ITEMS.register("energy_shard") { -> Item(Properties()) }
    val DIMENSIONAL_MATRIX: DeferredItem<Item> = ITEMS.register("dimensional_matrix") { -> Item(Properties().durability(1).setNoRepair()) }
    val EMPTY_DIMENSIONAL_MATRIX: DeferredItem<Item> = ITEMS.register("empty_dimensional_matrix") { -> Item(Properties()) }
    val EMPTY_DIMENSIONAL_MATRIX_TYPE = ENTITY_TYPES.register("empty_dimensional_matrix_type") { -> EntityType.Builder.of(::EmptyMatrixItemEntity, MobCategory.MISC).sized(0.25F, 0.25F).eyeHeight(0.2125F).clientTrackingRange(6).updateInterval(20).build("empty_dimensional_matrix_type") }
    val EMPTY_DIMENSIONAL_MATRIX_TAG_TYPE = TagKey.create(Registries.ENTITY_TYPE, azLoc("empty_dimensional_matrix_type"))
    val SEED_CRYSTAL: DeferredItem<Item> = ITEMS.register("seed_crystal") { -> Item(Properties()) }


    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}