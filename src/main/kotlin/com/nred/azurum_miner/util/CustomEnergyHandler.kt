package com.nred.azurum_miner.util

import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage
import kotlin.math.max
import kotlin.math.min


open class CustomEnergyHandler(capacity: Int, private val allowInput: Boolean, private val allowOutput: Boolean) : EnergyStorage(capacity) {

    companion object {
        fun getTooltip(itemStack: ItemStack): Component {
            return (itemStack.getCapability(Capabilities.EnergyStorage.ITEM) as CustomEnergyHandler).getTooltip()
        }
    }

    private var tempEnergy = 0

    // Stop other mods using capability from doing unexpected things
    override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
        if (allowInput) {
            val rtn = super.receiveEnergy(toReceive, simulate)
            if (rtn > 0 && !simulate) {
                onContentsChanged()
            }
            return rtn
        }
        return 0
    }

    override fun extractEnergy(toExtract: Int, simulate: Boolean): Int {
        if (allowOutput) {
            val rtn = super.extractEnergy(toExtract, simulate)
            if (rtn > 0 && !simulate) {
                this.tempEnergy = 0
                onContentsChanged()
            }
            return rtn
        }
        return 0
    }

    fun setCapacity(capacity: Int) {
        if (this.capacity == capacity) return
        val oldEnergy = max(energy.toDouble(), this.tempEnergy.toDouble()).toInt()

        if (this.tempEnergy > this.capacity) {
            this.energy = min(this.tempEnergy.toDouble(), capacity.toDouble()).toInt()
            onContentsChanged()
        } else {
            this.energy = min(this.energy.toDouble(), capacity.toDouble()).toInt()
            onContentsChanged()
        }

        this.capacity = capacity
        this.tempEnergy = oldEnergy // Save capacity in case of miss-click
    }

    // Normal insert and extract for use within the mod
    fun internalInsertEnergy(toReceive: Int, simulate: Boolean): Int {
        val rtn = super.receiveEnergy(toReceive, simulate)
        if (rtn > 0 && !simulate) {
            onContentsChanged()
        }
        return rtn
    }

    fun internalExtractEnergy(toExtract: Int, simulate: Boolean): Int {
        val rtn = super.extractEnergy(toExtract, simulate)
        if (rtn > 0 && !simulate) {
            this.tempEnergy = 0
            onContentsChanged()
        }
        return rtn
    }

    protected open fun onContentsChanged() {
    }

    fun updateStackComponent(stack: ItemStack) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY) { customData -> customData.update { compoundTag -> compoundTag.putInt("energy", this.energy) } }
    }

    fun getTooltip(): Component {
        return Component.translatable("tooltip.azurum_miner.energy", getFE(this.energyStored), getFE(this.maxEnergyStored)).withStyle(ChatFormatting.RED)
    }
}