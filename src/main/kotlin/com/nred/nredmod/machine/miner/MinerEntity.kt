package com.nred.nredmod.machine.miner

import com.nred.nredmod.NredMod.CONFIG
import com.nred.nredmod.datagen.ModItemTagProvider
import com.nred.nredmod.entity.ModBlockEntities
import com.nred.nredmod.machine.AbstractMachine
import com.nred.nredmod.machine.AbstractMachineBlockEntity
import com.nred.nredmod.machine.miner.MinerEntity.Companion.MinerEnum.*
import com.nred.nredmod.machine.miner.MinerEntity.Companion.MinerVariablesEnum.*
import com.nred.nredmod.util.FluidHelper
import com.nred.nredmod.util.FluidHelper.Companion.get
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.BlockCapabilityCache
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.client.extensions.IMenuProviderExtension
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import kotlin.random.Random

open class MinerEntity(pos: BlockPos, blockState: BlockState, private val tier: Int) : AbstractMachineBlockEntity(ModBlockEntities.MINER_ENTITY_TIERS[tier].get(), pos, blockState), IMenuProviderExtension { //, ModRecipeType.MINER
    fun getMinerConfig(key: String): Int {
        return CONFIG.getInt("miner.tiers.$key.tier${this.tier + 1}")
    }

    fun getMinerConfigStrList(key: String): ArrayList<String> {
        return CONFIG.get(key)
    }

    private var variables = IntArray(MinerVariablesEnum.entries.size + MinerEnum.entries.size)
    private var baseVariables = IntArray(MinerVariablesEnum.entries.size)
    private var modifierPoints = intArrayOf(0, 0, 0, 0, 0)

    private var data: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return this@MinerEntity.variables[index]
        }

        override fun set(index: Int, value: Int) {
            this@MinerEntity.variables[index] = value
        }

        override fun getCount(): Int {
            return variables.size
        }
    }

    private var modifierPointsData: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return this@MinerEntity.modifierPoints[index]
        }

        override fun set(index: Int, value: Int) {
            if (this@MinerEntity.modifierPoints[index] < value) {
                if ((this@MinerEntity.data[TOTAL_MODIFIER_POINTS] - this@MinerEntity.data[USED_MODIFIER_POINTS]) - (value - this@MinerEntity.modifierPoints[index]) < 0) { // Make sure the point can be added
                    return
                }

            }
            this@MinerEntity.data[USED_MODIFIER_POINTS] -= this@MinerEntity.modifierPoints[index] - value
            this@MinerEntity.modifierPoints[index] = value
        }

        override fun getCount(): Int {
            return modifierPoints.size
        }
    }

    private var aboveCache: BlockCapabilityCache<IItemHandler, Direction>? = null
    private var invalidAboveCacheBlock: BlockEntity? = null
    private var foundOres: Array<out ItemStack>? = null

    init {
        for ((idx, string) in listOf("numModifierPoints", "numModifierSlots", "baseTicksPerOp", "baseResetChance", "baseEnergyNeeded", "baseAccuracy", "baseVariance", "baseRawChance", "baseFilterChance", "energyCapacity").withIndex()) {
            baseVariables[idx] = getMinerConfig(string)
        }

        data[IS_ON] = 1
        data[PROGRESS] = 0
        data[ENERGY_LEVEL] = 0
        data[MOLTEN_ORE_LEVEL] = 0
        data[USED_MODIFIER_POINTS] = 0
        data[IS_STOPPED] = 1

        CalculateModifiers()
    }

    override val energyHandler = object : EnergyStorage(data[ENERGY_CAPACITY]) {
        override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
            setChanged()
            val rtn = super.receiveEnergy(toReceive, simulate)
            data[ENERGY_LEVEL] = this.energy

            return rtn
        }

        override fun extractEnergy(toExtract: Int, simulate: Boolean): Int {
            setChanged()
            data[ENERGY_LEVEL] = this.energy
            return super.extractEnergy(toExtract, simulate)
        }
    }

    override val fluidHandler = object : FluidTank(FLUID_SIZE, { it.`is`(FluidHelper.FLUIDS["molten_ore"].still) }) {
        override fun onContentsChanged() {
            setChanged()
            super.onContentsChanged()
            data[MOLTEN_ORE_LEVEL] = this.fluidAmount
        }
    }

    override val itemStackHandler = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
            if (!level!!.isClientSide()) {
                level!!.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return false // NO INPUT
        }

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            return stack
        }
    }

    companion object {
        const val FLUID_SIZE = 50000

        enum class MinerVariablesEnum {
            TOTAL_MODIFIER_POINTS, NUM_MODIFIER_SLOTS, TICKS_PER_OP, RESET_PER_TICK_CHANCE, ENERGY_NEEDED, ACCURACY,
            VARIANCE, RAW_CHANCE, FILTER_CHANCE, ENERGY_CAPACITY
        }

        enum class MinerEnum() {
            IS_ON, ENERGY_LEVEL, MOLTEN_ORE_LEVEL, USED_MODIFIER_POINTS, PROGRESS, IS_STOPPED, MISS_TICKS_PER_OP
        }

        operator fun ContainerData.get(e: Enum<*>): Int {
            if (e is MinerVariablesEnum) {
                return this.get(e.ordinal)
            } else if (e is MinerEnum) {
                return this.get(e.ordinal + MinerVariablesEnum.entries.size)
            }
            return 0
        }

        operator fun ContainerData.set(e: Enum<*>, value: Int) {
            if (e is MinerVariablesEnum) {
                this.set(e.ordinal, value)
            } else if (e is MinerEnum) {
                this.set(e.ordinal + MinerVariablesEnum.entries.size, value)
            }
        }

//        fun ContainerData.getArray(name: String): IntArray {
//            if (name == "POINTS"){
//                return this.
//            }
//return this
//        }
    }

    fun CalculateModifiers() {
        for ((idx, item) in baseVariables.withIndex()) {
            variables[idx] = item // + some modifier TODO
        }

//        MISS_TICKS_PER_OP
    }

    fun updateModifierPoints(index: Int, value: Int) {
        this.modifierPointsData.set(index, value)
        CalculateModifiers()
    }

    fun updateEnumData(index: Int, value: Int) {
        this.data.set(index, value)
    }

    fun updateEnumOthersData(index: Int, value: Int) {
        this.data.set(index + MinerVariablesEnum.entries.size, value)
    }

//    override fun saveToItem(stack: ItemStack, registries: HolderLookup.Provider) {
//        val tag = CompoundTag()
//        saveAdditional(tag, registries)
//        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
//
//        super.saveToItem(stack, registries)
//    }

    override fun onLoad() {
        super.onLoad()

        if (!level!!.isClientSide) {
            when (tier) {
                0 -> this.foundOres = Ingredient.of(ModItemTagProvider.oreTier1Tag).items
                1 -> this.foundOres = Ingredient.of(ModItemTagProvider.oreTier2Tag).items
                2 -> this.foundOres = Ingredient.of(ModItemTagProvider.oreTier3Tag).items
                3 -> this.foundOres = Ingredient.of(ModItemTagProvider.oreTier4Tag).items
                4 -> this.foundOres = Ingredient.of(ModItemTagProvider.oreTier5Tag).items
            }
        }

        this.loaded = true
    }

    override fun handleUpdateTag(tag: CompoundTag, lookupProvider: HolderLookup.Provider) {
        super.handleUpdateTag(tag, lookupProvider)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.put("inventory", itemStackHandler.serializeNBT(registries))

        tag.putIntArray("vars", variables)
        tag.putIntArray("modifierPoints", modifierPoints)

        tag.put("energy", energyHandler.serializeNBT(registries))
        fluidHandler.writeToNBT(registries, tag)

        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        itemStackHandler.deserializeNBT(registries, tag.getCompound("inventory"))

//        variables = arrayListOf(*tag.getIntArray("minerVars").toTypedArray(), 0).toIntArray() // When change size
        variables = tag.getIntArray("vars")
        modifierPoints = tag.getIntArray("modifierPoints")

        energyHandler.deserializeNBT(registries, tag.get("energy")!!)
        fluidHandler.readFromNBT(registries, tag.getCompound("fluid"))
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): MinerMenu {
        return MinerMenu(containerId, playerInventory, ContainerLevelAccess.create(level!!, blockPos), blockPos, this.data, this.modifierPointsData, this.tier)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("menu.title.nredmod.miner", this.tier + 1)
    }

    //This is where you can react to capability changes, removals, or appearances.
    fun onCapInvalidate() {
        this.aboveCache = null
        this.invalidAboveCacheBlock = null
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
        if (!this.loaded) return
        if (energyHandler.energyStored > data[ENERGY_NEEDED] / data[TICKS_PER_OP] && data[IS_ON] == 1) {
            if (this.aboveCache == null) {
                if (level.getBlockEntity(blockPos.above()) == null || level.getBlockEntity(blockPos.above()) == this.invalidAboveCacheBlock) {
                    level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, false))

                    data[IS_STOPPED] = 1
                    return
                }

                this.invalidAboveCacheBlock = null
                this.aboveCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, level as ServerLevel, blockPos.above(), Direction.DOWN, { -> !this.isRemoved }, { -> onCapInvalidate() })

                val aboveHandler = this.aboveCache!!.capability
                var valid = false
                for (slot in 0..<aboveHandler!!.slots) {
                    if (aboveHandler.insertItem(0, ItemStack(Blocks.COAL_ORE), true).isEmpty) {
                        valid = true
                        break
                    }
                }
                if (!valid) {
                    this.invalidAboveCacheBlock = level.getBlockEntity(blockPos.above()) // Above has no slots valid for the block
                    this.aboveCache = null
                    return
                }
            }

            if (data[PROGRESS] < data[TICKS_PER_OP]) { // Take into account modifiers
                level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, true))
                energyHandler.extractEnergy(data[ENERGY_NEEDED] / data[TICKS_PER_OP], false)
                data[PROGRESS]++
                data[IS_STOPPED] = 0
            } else { // Cycle Done
                if (Random.nextInt(0, 100) > data[ACCURACY]) {
                    data[PROGRESS] = 0
                    return // Miss
                }

                if (itemStackHandler.getStackInSlot(0).isEmpty) {
                    itemStackHandler.setStackInSlot(0, foundOres!![Random.nextInt(foundOres!!.size)])
                    setChanged(level, pos, state)
                }
                val aboveHandler = aboveCache!!.capability
                if (aboveHandler != null) {
                    var notMoved = true
                    val handler = capCache!!.capability
                    val stack = handler!!.extractItem(0, 1, false)

                    for (slot in 0..<aboveHandler.slots) {
                        if (aboveHandler.insertItem(slot, stack, true).isEmpty) {
                            if (aboveHandler.insertItem(slot, stack, false).isEmpty)
                                data[PROGRESS] = 0
                            notMoved = false
                            break
                        }
                    }

                    if (notMoved) {
                        data[IS_STOPPED] = 1
                        level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, false))
                        handler.insertItem(0, stack, false) // Put item back
                    }
                } else if (state.getValue(AbstractMachine.MACHINE_ON)) {
                    data[IS_STOPPED] = 1
                    level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, false))
                    setChanged(level, pos, state)
                }
            }
        } else {
            level.setBlockAndUpdate(pos, state.setValue(AbstractMachine.MACHINE_ON, false))
            if (data[PROGRESS] > 0 && Random.nextInt(100) < data[RESET_PER_TICK_CHANCE]) {
                data[PROGRESS]--
            }
        }
    }
}

class MinerEntityTier1(pos: BlockPos, blockState: BlockState) : MinerEntity(pos, blockState, 0)
class MinerEntityTier2(pos: BlockPos, blockState: BlockState) : MinerEntity(pos, blockState, 1)
class MinerEntityTier3(pos: BlockPos, blockState: BlockState) : MinerEntity(pos, blockState, 2)
class MinerEntityTier4(pos: BlockPos, blockState: BlockState) : MinerEntity(pos, blockState, 3)
class MinerEntityTier5(pos: BlockPos, blockState: BlockState) : MinerEntity(pos, blockState, 4)
