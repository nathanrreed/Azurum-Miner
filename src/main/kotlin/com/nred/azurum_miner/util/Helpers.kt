package com.nred.azurum_miner.util

import com.nred.azurum_miner.item.ModItems
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister


object Helpers {
    fun itemComponentSplit(tooltip: String, vararg objects: Any): List<MutableComponent> {
        return Component.translatable(tooltip, *objects).string.split("\n").map { text -> Component.literal(text) }
    }

    fun componentSplit(tooltip: String, vararg objects: Any): List<FormattedCharSequence> {
        return Component.translatable(tooltip, *objects).string.split("\n").map { text -> FormattedCharSequence.forward(text, Style.EMPTY) }
    }

    fun componentSplit(tooltip: String, styles: List<Style>, vararg objects: Any): List<FormattedCharSequence> {
        return Component.translatable(tooltip, *objects).string.split("\n").mapIndexed { idx, text -> FormattedCharSequence.forward(text, styles[idx]) }
    }

    fun compC(tooltip: String, color: Int, vararg objects: Any): MutableComponent {
        return Component.translatable(tooltip, *objects).setStyle(Style.EMPTY.withColor(color))
    }

    fun compC(tooltip: String, vararg objects: Any): MutableComponent {
        return Component.translatable(tooltip, *objects)
    }

    fun compCat(vararg components: MutableComponent): MutableComponent {
        return components.reduce { acc, mutableComponent -> acc.append(mutableComponent) }
    }

    fun registerBlock(name: String, register: DeferredRegister.Blocks, block: () -> Block): DeferredBlock<Block> {
        val toReturn = register.register(name, block)
        registerBlockItem(name, toReturn)
        return toReturn
    }

    fun <T : Block> registerBlockItem(name: String, block: DeferredBlock<T>) {
        ModItems.ITEMS.registerSimpleBlockItem(name, { block.get() })
    }

    private const val HOTBAR_SLOT_COUNT: Int = 9
    private const val PLAYER_INVENTORY_ROW_COUNT: Int = 3
    private const val PLAYER_INVENTORY_COLUMN_COUNT: Int = 9
    private const val PLAYER_INVENTORY_SLOT_COUNT: Int = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT
    private val VANILLA_SLOT_COUNT: Int = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT
    private const val VANILLA_FIRST_SLOT_INDEX: Int = 0
    private val TE_INVENTORY_FIRST_SLOT_INDEX: Int = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT

    fun quickMoveStack(playerIn: Player, pIndex: Int, slots: NonNullList<Slot>, moveItemStackTo: (ItemStack, Int, Int, Boolean) -> Boolean, TE_INVENTORY_SLOT_COUNT: Int): ItemStack {
        val sourceSlot: Slot? = slots[pIndex]
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY //EMPTY_ITEM

        val sourceStack = sourceSlot.item
        val copyOfSourceStack = sourceStack.copy()

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY
            }
        } else {
            println("Invalid slotIndex:$pIndex")
            return ItemStack.EMPTY
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.count == 0) {
            sourceSlot.set(ItemStack.EMPTY)
        } else {
            sourceSlot.setChanged()
        }
        sourceSlot.onTake(playerIn, sourceStack)
        return copyOfSourceStack
    }
}