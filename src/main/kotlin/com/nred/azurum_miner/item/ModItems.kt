package com.nred.azurum_miner.item

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import com.nred.azurum_miner.AzurumMiner
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object ModItems {
    val ITEMS = DeferredRegister.createItems(AzurumMiner.ID)

    val SIMPLE_VOID_PROCESSOR = ITEMS.register("simple_void_processor") { -> Item(Properties()) }
    val VOID_PROCESSOR = ITEMS.register("void_processor") { -> Item(Properties()) }
    val ELABORATE_VOID_PROCESSOR = ITEMS.register("elaborate_void_processor") { -> Item(Properties()) }
    val COMPLEX_VOID_PROCESSOR = ITEMS.register("complex_void_processor") { -> Item(Properties()) }

    val CONGLOMERATE_OF_ORE_SHARD = ITEMS.register("conglomerate_of_ore_shard") { -> Item(Properties()) }
    val NETHER_DIAMOND = ITEMS.register("nether_diamond") { -> Item(Properties()) }
    val ENDER_DIAMOND = ITEMS.register("ender_diamond") { -> Item(Properties()) }
    val DIMENSIONAL_MATRIX = ITEMS.register("dimensional_matrix") { -> Item(Properties()) }
    val EMPTY_DIMENSIONAL_MATRIX = ITEMS.register("empty_dimensional_matrix") { -> Item(Properties()) }

//    val EXAMPLE_ITEM: DeferredItem<Item> = ITEMS.register(
//        "test",
//        {
//            ->
//            object : Item(Properties()) {
//                override fun appendHoverText(
//                    stack: ItemStack,
//                    context: TooltipContext,
//                    tooltipComponents: MutableList<Component>,
//                    tooltipFlag: TooltipFlag
//                ) {
//                    tooltipComponents.add(Component.literal("TEST"))
//                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
//                }
//            }
//        }
//    )

    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}
