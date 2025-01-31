@file:Suppress("unused")

package com.nred.azurum_miner.compat.cct

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.miner.MinerEntity
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum.*
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerVariablesEnum.*
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.get
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.set
import com.nred.azurum_miner.util.FALSE
import com.nred.azurum_miner.util.TRUE
import dan200.computercraft.api.detail.VanillaDetailRegistries
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

class MinerPeripheral(private val miner: MinerEntity) : MachinePeripheral(miner) {
    @LuaFunction(mainThread = true)
    fun getAccuracy(): Int {
        return miner.data[ACCURACY]
    }

    @LuaFunction(mainThread = true)
    fun getMoltenOre(): Int {
        return miner.fluidHandler.getFluidInTank(0).amount
    }

    @LuaFunction(mainThread = true)
    fun getMoltenOreNeeded(): Int {
        return miner.data[FLUID_NEEDED]
    }

    @LuaFunction(mainThread = true)
    fun getProgress(): Double {
        return miner.data[PROGRESS].toDouble() / miner.data[CURRENT_NEEDED].toDouble() * 100
    }

    @LuaFunction(mainThread = true)
    fun getUpgrade(index: Int): Int {
        return miner.modifierPoints[index]
    }

    @LuaFunction(mainThread = true)
    fun useModifier(index: Int) {
        miner.modifierPoints[index]++
    }

    @LuaFunction(mainThread = true)
    fun getModifiers(): Int {
        return miner.data[TOTAL_MODIFIER_POINTS] - miner.data[USED_MODIFIER_POINTS]
    }

    @LuaFunction(mainThread = true)
    fun getModifiersTotal(): Int {
        return miner.data[TOTAL_MODIFIER_POINTS]
    }

    @LuaFunction(mainThread = true)
    fun isOn(): Boolean {
        return miner.data[IS_ON] == TRUE
    }

    @LuaFunction(mainThread = true)
    fun setIsOn(state: Boolean) {
        miner.data[IS_ON] = if (state) TRUE else FALSE
    }

    @LuaFunction(mainThread = true)
    fun getFilters(): Map<Int, List<String>> {
        return listOf(0, 1, 2).map { it + 1 to (if (miner.filters[it].isEmpty()) listOf(miner.itemStackHandler.getStackInSlot(it).itemHolder.registeredName.replace("minecraft:air", ""), "item") else listOf(miner.filters[it], "tag")) }.associate { it }
    }

    @LuaFunction(mainThread = true)
    fun getFilterContents(index: Int): List<Any?> {
        val details = VanillaDetailRegistries.ITEM_STACK
        return miner.getFilterOptions(index).map { details.getBasicDetails(it)["name"] }
    }

    @LuaFunction(mainThread = true)
    fun setFilter(index: Int, input: String, item: Boolean) {
        if (item) {
            var found = ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(input)))
            if (found.isEmpty) {
                found = ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(input)))
            }
            miner.itemStackHandler.setStackInSlot(index, found)
            miner.updateFilterData(index, "")
        } else {
            miner.itemStackHandler.setStackInSlot(index, ItemStack.EMPTY)
            miner.updateFilterData(index, input)
        }
    }

    override fun getType(): String {
        return ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner").toString()
    }

    override fun equals(other: IPeripheral?): Boolean {
        return other is MinerPeripheral && miner == other.miner
    }
}