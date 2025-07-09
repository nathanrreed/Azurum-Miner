package com.nred.azurum_miner.item

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.datagen.ModBlockTagProvider.Companion.INCORRECT_FOR_PALESTIUM_TOOL
import com.nred.azurum_miner.entity.EmptyMatrixItemEntity
import com.nred.azurum_miner.entity.ModEntities.ENTITY_TYPES
import com.nred.azurum_miner.util.Helpers.azLoc
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.PickaxeItem
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.SimpleTier
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
    val VOID_CRYSTAL: DeferredItem<Item> = ITEMS.register("void_crystal") { -> Item(Properties()) }
    val DIMENSIONAL_MATRIX: DeferredItem<Item> = ITEMS.register("dimensional_matrix") { -> Item(Properties().durability(1).setNoRepair()) }
    val EMPTY_DIMENSIONAL_MATRIX: DeferredItem<Item> = ITEMS.register("empty_dimensional_matrix") { -> Item(Properties()) }
    val EMPTY_DIMENSIONAL_MATRIX_TYPE = ENTITY_TYPES.register("empty_dimensional_matrix_type") { -> EntityType.Builder.of(::EmptyMatrixItemEntity, MobCategory.MISC).sized(0.25F, 0.25F).eyeHeight(0.2125F).clientTrackingRange(6).updateInterval(20).build("empty_dimensional_matrix_type") }
    val EMPTY_DIMENSIONAL_MATRIX_TAG_TYPE = TagKey.create(Registries.ENTITY_TYPE, azLoc("empty_dimensional_matrix_type"))
    val SEED_CRYSTAL: DeferredItem<Item> = ITEMS.register("seed_crystal") { -> Item(Properties()) }

    val VOID_GUN: DeferredItem<Item> = ITEMS.register("void_gun") { -> VoidGun() }
    val VOID_BULLET: DeferredItem<Item> = ITEMS.register("void_bullet") { -> VoidBulletItem(Properties()) }

    val PALESTIUM_PICKAXE_TIER = SimpleTier(INCORRECT_FOR_PALESTIUM_TOOL, 2400, 14f, 4f, 16) { -> Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/palestium"))) }
    val PALESTIUM_PICKAXE: DeferredItem<Item> = ITEMS.register("palestium_pickaxe") { -> PalestiumPickaxe(PALESTIUM_PICKAXE_TIER, Properties().attributes(PickaxeItem.createAttributes(PALESTIUM_PICKAXE_TIER, 2.0f, -2.4f))) }

    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}