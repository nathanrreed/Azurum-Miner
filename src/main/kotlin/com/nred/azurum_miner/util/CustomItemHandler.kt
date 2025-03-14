package com.nred.azurum_miner.util

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ItemStackHandler

open class CustomItemHandler(size: Int, private val allowInput: Boolean, private val allowOutput: Boolean) : ItemStackHandler(size) {
    private var tempEnergy = 0

    // Stop other mods using capability from doing unexpected things
    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (allowInput) {
            return super.insertItem(slot, stack, simulate)
        }
        return stack
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (allowOutput) {
            return super.extractItem(slot, amount, simulate)
        }
        return ItemStack.EMPTY
    }

    // Normal insert and extract for use within the mod
    fun internalInsertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return super.insertItem(slot, stack, simulate)
    }

    fun internalExtractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return super.extractItem(slot, amount, simulate)
    }
}