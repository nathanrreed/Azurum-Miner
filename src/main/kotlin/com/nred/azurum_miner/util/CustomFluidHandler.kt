package com.nred.azurum_miner.util

import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import java.util.function.Consumer
import kotlin.math.min

abstract class CustomFluidStackHandler(private val capacity: Int, private val tanks: Int, private val allowInput: Boolean, private val allowOutput: Boolean) : IFluidHandler, INBTSerializable<CompoundTag> {
    protected lateinit var fluids: NonNullList<FluidStack>

    init {
        setSize(tanks)
    }

    override fun getTanks(): Int {
        return tanks
    }

    override fun getFluidInTank(tank: Int): FluidStack {
        validateSlotIndex(tank)
        return fluids[tank]
    }

    override fun getTankCapacity(tank: Int): Int {
        return capacity
    }

    abstract override fun isFluidValid(tank: Int, stack: FluidStack): Boolean
    abstract fun canOutput(tank: Int): Boolean
    abstract fun canInput(tank: Int): Boolean

    override fun deserializeNBT(lookupProvider: HolderLookup.Provider, nbt: CompoundTag) {
        setSize(if (nbt.contains("Size", Tag.TAG_INT.toInt())) nbt.getInt("Size") else fluids.size)
        val tagList = nbt.getList("Fluids", Tag.TAG_COMPOUND.toInt())
        for (i in tagList.indices) {
            val fluidTags = tagList.getCompound(i)
            val slot = fluidTags.getInt("Slot")

            if (slot >= 0 && slot < fluids.size) {
                FluidStack.parse(lookupProvider, fluidTags).ifPresent(Consumer { fluid: FluidStack -> fluids[slot] = fluid })
            }
        }
        onLoad()
    }

    companion object {
        fun listFromNBT(provider: HolderLookup.Provider, nbt: CompoundTag): List<FluidStack> {
            val tagList = nbt.getList("Fluids", Tag.TAG_COMPOUND.toInt())
            val fluids = ArrayList<FluidStack>()
            for (i in tagList.indices) {
                val fluidTags = tagList.getCompound(i)
                val slot = fluidTags.getInt("Slot")

                if (slot >= 0 && slot < nbt.getInt("Size")) {
                    FluidStack.parse(provider, fluidTags).ifPresent(Consumer { fluid: FluidStack -> fluids.add(fluid) })
                }
            }
            return fluids
        }
    }

    fun setSize(size: Int) {
        fluids = NonNullList.withSize(size, FluidStack.EMPTY)
        onContentsChanged()
    }

    fun getFluidAmount(tank: Int): Int {
        return fluids[tank].amount
    }

    fun setFluid(tank: Int, fluidStack: FluidStack) {
        validateSlotIndex(tank)
        fluids[tank] = fluidStack
        onContentsChanged()
    }

    override fun serializeNBT(lookupProvider: HolderLookup.Provider): CompoundTag {
        val nbtTagList = ListTag()
        for (i in 0..<this.tanks) {
            if (!fluids[i].isEmpty) {
                val fluidTag = CompoundTag()
                fluidTag.putInt("Slot", i)
                nbtTagList.add(fluids[i].save(lookupProvider, fluidTag))
            }
        }
        val nbt = CompoundTag()
        nbt.put("Fluids", nbtTagList)
        nbt.putInt("Size", fluids.size)
        return nbt
    }

    // Stop other mods using capability from doing unexpected things
    override fun fill(resource: FluidStack, action: FluidAction): Int {
        if (allowInput) {
            return internalInsertFluid(resource, action, false)
        }
        return 0
    }

    fun fill(tank: Int, resource: FluidStack, action: FluidAction): Int {
        if (!FluidStack.isSameFluidSameComponents(getFluidInTank(tank), resource) && !getFluidInTank(tank).isEmpty) {
            return 0
        }

        val amount = resource.amount
        if (action.execute()) {
            setFluid(tank, resource.copyWithAmount(resource.amount + getFluidAmount(tank)))
        }

        if (getFluidAmount(tank) + amount > capacity) {
            return capacity - getFluidAmount(tank)
        }
        return amount
    }

    override fun drain(resource: FluidStack, action: FluidAction): FluidStack {
        if (allowOutput) {
            return internalExtractFluid(resource, action)
        }
        return FluidStack.EMPTY
    }

    fun drain(tank: Int, maxDrain: Int, action: FluidAction): FluidStack {
        val fluid = fluids[tank]
        if (fluid.isEmpty) return FluidStack.EMPTY
        var drained = maxDrain
        if (fluid.amount < drained) {
            drained = fluid.amount
        }
        val stack = fluid.copyWithAmount(drained)
        if (action.execute() && drained > 0) {
            fluid.shrink(drained)
            onContentsChanged()
        }
        if (drained > 0) {
            return stack
        }
        return FluidStack.EMPTY
    }

    override fun drain(maxDrain: Int, action: FluidAction): FluidStack {
        if (allowOutput) {
            return internalExtractFluid(maxDrain, action, false)
        }
        return FluidStack.EMPTY
    }

    // Normal insert and extract for use within the mod
    fun internalInsertFluid(resource: FluidStack, action: FluidAction, internal: Boolean): Int {
        if (resource.isEmpty) {
            return 0
        }
        if (action.simulate()) {
            var amount = 0
            var i = -1 // Will start at 0 always since i++ is right after, just makes continues easier
            for (fluid in fluids) {
                i++
                if (!isFluidValid(i, resource) || (!canInput(i) && !internal)) continue
                if (fluid.isEmpty) {
                    amount = (amount + min(capacity.toDouble(), resource.amount.toDouble())).toInt()
                }
                if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
                    continue
                }
                amount = (amount + min((capacity - fluid.amount).toDouble(), resource.amount.toDouble())).toInt()
            }
            return amount
        } else {
            var i = -1 // Will start at 0 always since i++ is right after, just makes continues easier
            for (fluid in fluids) {
                i++
                if (!isFluidValid(i, resource)) continue
                if (fluid.isEmpty) {
                    fluids[i] = resource.copyWithAmount(min(capacity.toDouble(), resource.amount.toDouble()).toInt())
                    onContentsChanged()
                    return fluids[i].amount
                }
                if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
                    return 0
                }
                var filled = capacity - fluid.amount

                if (resource.amount < filled) {
                    fluid.grow(resource.amount)
                    filled = resource.amount
                } else {
                    fluid.amount = capacity
                }
                if (filled > 0) {
                    onContentsChanged()
                    return filled
                }
            }
            return 0
        }
    }

    fun internalInsertFluid(resource: FluidStack, action: FluidAction): Int {
        return internalInsertFluid(resource, action, true)
    }

    fun internalExtractFluid(maxDrain: Int, action: FluidAction, internal: Boolean): FluidStack {
        for (i in fluids.indices) {
            val fluid = internalExtractFluid(maxDrain, action, internal, i)

            if (!FluidStack.isSameFluidSameComponents(fluid, FluidStack.EMPTY))
                return fluid
        }
        return FluidStack.EMPTY
    }

    fun internalExtractFluid(maxDrain: Int, action: FluidAction, internal: Boolean, index: Int): FluidStack {
        val fluid = fluids[index]
        if (fluid.isEmpty || (!canOutput(index) && !internal)) return FluidStack.EMPTY
        var drained = maxDrain
        if (fluid.amount < drained) {
            drained = fluid.amount
        }
        val stack = fluid.copyWithAmount(drained)
        if (action.execute() && drained > 0) {
            fluid.shrink(drained)
            onContentsChanged()
        }
        if (drained > 0) {
            return stack
        }
        return FluidStack.EMPTY
    }

    protected fun validateSlotIndex(slot: Int) {
        if (slot < 0 || slot >= fluids.size) throw RuntimeException("Slot " + slot + " not in valid range - [0," + fluids.size + ")")
    }

    fun internalExtractFluid(resource: FluidStack, action: FluidAction): FluidStack {
        val index = fluids.indexOfFirst { fluidStack: FluidStack? -> fluidStack!!.`is`(resource.fluid) }

        if (index >= 0) {
            return internalExtractFluid(resource.amount, action, true, index)
        }
        return FluidStack.EMPTY
    }

    protected fun onLoad() {
    }

    protected open fun onContentsChanged() {
    }
}