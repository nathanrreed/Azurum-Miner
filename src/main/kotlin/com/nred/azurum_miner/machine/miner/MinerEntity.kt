package com.nred.azurum_miner.machine.miner

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.datagen.ModItemTagProvider
import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.machine.AbstractMachine
import com.nred.azurum_miner.machine.AbstractMachineBlockEntity
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum.*
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerVariablesEnum.*
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
import com.nred.azurum_miner.util.FluidHelper
import com.nred.azurum_miner.util.FluidHelper.Companion.get
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
import kotlin.jvm.optionals.getOrNull
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.random.Random
import kotlin.reflect.KCallable

open class MinerEntity(pos: BlockPos, blockState: BlockState, private val tier: Int) : AbstractMachineBlockEntity(ModBlockEntities.MINER_ENTITY_TIERS[tier].get(), pos, blockState), IMenuProviderExtension {
    private var variables = IntArray(MinerVariablesEnum.entries.size + MinerEnum.entries.size)
    private var modifierPoints = intArrayOf(0, 0, 0, 0, 0)
    private var filters = mutableListOf("", "", "")

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
            calculateModifiers()
        }

        override fun getCount(): Int {
            return modifierPoints.size
        }
    }

    private var aboveCache: BlockCapabilityCache<IItemHandler, Direction>? = null
    private var invalidAboveCacheBlock: BlockEntity? = null
    private var foundOres = ArrayList<ItemStack>()

    init {
        for ((idx, string) in listOf("numModifierPoints", "numModifierSlots", "baseTicksPerOp", "baseResetChance", "baseEnergyNeeded", "baseAccuracy", "baseMaterialChance", "baseRawChance", "baseFilterChance", "energyCapacity", "baseMultiChance", "baseMultiMin", "baseMultiMax").withIndex()) {
            variables[idx] = getMinerConfig(string, this.tier)
        }

        data[IS_ON] = 1
        data[PROGRESS] = 0
        data[ENERGY_LEVEL] = 0
        data[MOLTEN_ORE_LEVEL] = 0
        data[USED_MODIFIER_POINTS] = 0
        data[IS_STOPPED] = 1
        data[HAS_FILTER] = 0

        calculateModifiers()
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

    val fluidHandler = object : FluidTank(FLUID_SIZE, { it.`is`(FluidHelper.FLUIDS["molten_ore"].still) }) {
        override fun onContentsChanged() {
            setChanged()
            super.onContentsChanged()
            data[MOLTEN_ORE_LEVEL] = this.fluidAmount
        }
    }

    val OUTPUT = 3
    override val itemStackHandler = object : ItemStackHandler(4) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
            if (!level!!.isClientSide()) {
                level!!.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            if (slot == OUTPUT) {
                return false // NO INPUT
            } else if (data[NUM_FILTERS] > slot && foundOres.contains(stack)) {
                return true
            }
            return false
        }

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            if (slot == OUTPUT) {
                return stack // NO INPUT
            }
            return super.insertItem(slot, stack, simulate)
        }
    }

    companion object {
        const val FLUID_SIZE = 50000

        enum class MinerVariablesEnum {
            TOTAL_MODIFIER_POINTS, NUM_MODIFIER_SLOTS, TICKS_PER_OP, RESET_PER_TICK_CHANCE, ENERGY_NEEDED, ACCURACY,
            MATERIAL_CHANCE, RAW_CHANCE, FILTER_CHANCE, ENERGY_CAPACITY, MULTI_CHANCE, MULTI_MIN, MULTI_MAX
        }

        enum class MinerEnum() {
            IS_ON, ENERGY_LEVEL, MOLTEN_ORE_LEVEL, USED_MODIFIER_POINTS, PROGRESS, IS_STOPPED, MISS_TICKS_PER_OP, NUM_FILTERS, HIGHER_TIER_CHANCE, MISS_ENERGY_NEEDED, HAS_FILTER
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

        fun getMinerConfig(key: String, tier: Int): Int {
            return CONFIG.getInt("miner.tiers.$key.tier${tier + 1}")
        }

        class MappingInfo(val enum: Enum<*>, val additive: Boolean, val func: KCallable<String>, val vals: Int = 3)

        val minerMapping = mapOf(
            0 to mapOf("name" to "speed", "info" to listOf(MappingInfo(TICKS_PER_OP, false, ::getTime), MappingInfo(MISS_TICKS_PER_OP, true, ::getPercent, 1), MappingInfo(TICKS_PER_OP, false, ::getTime), MappingInfo(TICKS_PER_OP, false, ::getTime), MappingInfo(TICKS_PER_OP, false, ::getTime))),
            1 to mapOf("name" to "filter", "info" to listOf(MappingInfo(FILTER_CHANCE, true, ::getPercent, 2), null, MappingInfo(FILTER_CHANCE, true, ::getPercent, 2), null, MappingInfo(FILTER_CHANCE, true, ::getPercent, 2))),
            2 to mapOf("name" to "accuracy", "info" to listOf(MappingInfo(ACCURACY, true, ::getPercent, 2), MappingInfo(HIGHER_TIER_CHANCE, true, ::getPercent, 2), MappingInfo(ACCURACY, true, ::getPercent, 2), MappingInfo(HIGHER_TIER_CHANCE, true, ::getPercent, 2), null)),
            3 to mapOf("name" to "efficiency", "info" to listOf(MappingInfo(ENERGY_NEEDED, false, ::getInt, 1), MappingInfo(MISS_ENERGY_NEEDED, false, ::getInt, 1), null, MappingInfo(ENERGY_NEEDED, false, ::getPercent, 1), MappingInfo(ENERGY_NEEDED, false, ::getBlank, 1))),
            4 to mapOf("name" to "production", "info" to listOf(MappingInfo(MULTI_MAX, true, ::getInt), MappingInfo(MULTI_MAX, true, ::getInt), MappingInfo(MULTI_MIN, true, ::getInt), MappingInfo(MULTI_MAX, true, ::getInt), MappingInfo(MULTI_MIN, true, ::getInt)))
        )

        fun getPercent(num: Number): String {
            return "${num.toInt()}%"
        }

        fun getInt(num: Number): String {
            return "${num.toInt()}"
        }

        fun getBlank(): String {
            return ""
        }

        fun getModifierVal(path: String, start: Number, additive: Boolean): Pair<Number, String> {
            val value = CONFIG.getOptional<Any>(path).getOrNull()
            if (value is String) {
                if (value.endsWith("%")) {
                    val percent = value.substringBefore('%').toDouble() / 100.0
                    return Pair(start.toDouble() * if (additive) (1.0 + percent) else (1.0 - percent), value)
                } else if (value.endsWith("x")) {
                    return Pair(start.toDouble() * value.substringBefore('x').toDouble(), value)
                } else {
                    return Pair(start.toDouble() - value.toDouble(), value)
                }
            } else if (value is Number) {
                return Pair(if (additive) start.toDouble() + value.toDouble() else start.toDouble() - value.toDouble(), value.toString())
            } else {
                return Pair(0, "")
            }
        }

        fun getEnergyModifierVal(path: String, reduction: Double): Double {
            val value = CONFIG.getOptional<Number>("miner.modifiers.$path").getOrNull()
            if (value is Number) {
                return value.toDouble() - reduction
            } else {
                throw Exception("Invalid FE Multiplier in $path")
            }
        }

        fun getModifierVal(path: String): Double {
            val value = CONFIG.getOptional<Number>("miner.modifiers.$path").getOrNull()
            if (value is Number) {
                return value.toDouble()
            } else {
                throw Exception("Invalid Modifier value " + path)
            }
        }

        fun calculateEnergyModifier(base: Int, category: String, idx: Int, points3: Int): Int {
            val modifierFEReduction = if (points3 > 2) CONFIG.getOptional<Double>("miner.modifiers.efficiency.3").get() else 0.0 // Reduce if unlocked
            return (base * getEnergyModifierVal("${category}.${idx}FE", modifierFEReduction)).toInt()
        }

        fun halfCeil(num: Double): Double {
            return ceil(num * 2.0) / 2.0
        }

        fun calculateFluidCost(points: Int, tier: Int): Double {
            val value = CONFIG.getOptional<Number>("miner.options.fluidNeedExponentialBase").get().toDouble()
            return (halfCeil(value.pow((points - CONFIG.getInt("miner.tiers.numModifierPoints.tier$tier")).toDouble()) + 4)) * 1000
        }
    }

    fun calculateModifiers() {
        data[NUM_FILTERS] = 0
        data[HIGHER_TIER_CHANCE] = 0
        data[MISS_ENERGY_NEEDED] = 0
        data[MISS_TICKS_PER_OP] = 0

        for ((idx, string) in listOf("numModifierPoints", "numModifierSlots", "baseTicksPerOp", "baseResetChance", "baseEnergyNeeded", "baseAccuracy", "baseMaterialChance", "baseRawChance", "baseFilterChance", "energyCapacity", "baseMultiChance", "baseMultiMin", "baseMultiMax").withIndex()) {
            data[idx] = getMinerConfig(string, this.tier)
        }

        data[ENERGY_NEEDED] = calculateEnergyModifier()

        for ((catIdx, category) in minerMapping.filter { intArrayOf(0, 1, 2, 4).contains(it.key) }.values.withIndex()) { // Category 3 is efficiency and doesn't add any FE on upgrade
            for ((idx, info) in (category["info"] as List<*>).withIndex()) {
                if (info is MappingInfo && modifierPoints[if (catIdx == 3) 4 else catIdx] > idx) {
                    data[info.enum] = getModifierVal("miner.modifiers.${category["name"]}.${idx + 1}", data[info.enum], info.additive).first.toInt()
                }
            }
        }

        if (modifierPoints[2] > 4) {
            data[ACCURACY] = 100
        }
        if (modifierPoints[1] > 0) {
            data[NUM_FILTERS] = 1
        }
        if (modifierPoints[1] > 1) {
            data[MATERIAL_CHANCE] = 0
        }
        if (modifierPoints[1] > 2) {
            data[NUM_FILTERS] = 2
        }
        if (modifierPoints[1] > 3) {
            data[RAW_CHANCE] = 0
        }
        if (modifierPoints[1] > 4) {
            data[NUM_FILTERS] = 3
        }
    }

    fun calculateEnergyModifier(): Int {
        var base: Double = CONFIG.getOptional<Number>("miner.tiers.baseEnergyNeeded.tier${this.tier + 1}").get().toDouble()
        base *= if (modifierPoints[3] > 0) (100.0 - getModifierVal("efficiency.1")) / 100.0 else 1.0
        base *= if (modifierPoints[3] > 3) (100.0 - getModifierVal("efficiency.4")) / 100.0 else 1.0
        base *= if (modifierPoints[3] > 4) (100.0 - getModifierVal("efficiency.5")) / 100.0 else 1.0

        val modifierFEReduction = if (modifierPoints[3] > 2) CONFIG.getOptional<Double>("miner.modifiers.efficiency.3").get() else 0.0 // Reduce if unlocked

        data[MISS_ENERGY_NEEDED] = if (modifierPoints[3] > 1) getModifierVal("efficiency.2").toInt() else 0

        for (modifierCategory in listOf(Pair(0, "speed"), Pair(1, "filter"), Pair(2, "accuracy"), Pair(4, "production"))) { // Category 3 is efficiency and doesn't add any FE on upgrade
            for (i in 1..modifierPoints[modifierCategory.first]) {
                base *= getEnergyModifierVal("${modifierCategory.second}.${i}FE", modifierFEReduction)
            }
        }

        return base.toInt()
    }


    fun updateModifierPoints(index: Int, value: Int) {
        this.modifierPointsData.set(index, value)
        calculateModifiers()
    }

    fun updateEnumData(index: Int, value: Int) {
        this.data.set(index, value)
    }

    fun updateEnumOthersData(index: Int, value: Int) {
        this.data.set(index + MinerVariablesEnum.entries.size, value)
    }

    fun updateFilterData(index: Int, value: String) {
        filters[index] = value
        setChanged()
    }

    fun getFilterData(index: Int): String {
        return filters[index]
    }

    override fun onLoad() {
        super.onLoad()

        if (!level!!.isClientSide) {
            for (i in 0..this.tier){
                this.foundOres += Ingredient.of(ModItemTagProvider.oreTierTag[i]).items
            }
        }

        calculateModifiers()
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

        for (i in 0..<filters.size) {
            tag.putString("filter_$i", filters[i])
        }

        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        itemStackHandler.deserializeNBT(registries, tag.getCompound("inventory"))

//        variables = arrayListOf(*tag.getIntArray("vars").toTypedArray(), 0).toIntArray() // When change size
        variables = tag.getIntArray("vars")
        modifierPoints = tag.getIntArray("modifierPoints")

        energyHandler.deserializeNBT(registries, tag.get("energy")!!)
        fluidHandler.readFromNBT(registries, tag.getCompound("fluid"))

        for (i in 0..<filters.size) {
            filters[i] = tag.getString("filter_$i")
        }
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): MinerMenu {
        return MinerMenu(containerId, playerInventory, ContainerLevelAccess.create(level!!, blockPos), blockPos, this.data, this.modifierPointsData, this.tier)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("menu.title.azurum_miner.miner", this.tier + 1)
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
                    if (aboveHandler.insertItem(slot, ItemStack(Blocks.COAL_ORE), true).isEmpty) { //TODO DO BETTER
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

                if (itemStackHandler.getStackInSlot(OUTPUT).isEmpty) {
                    itemStackHandler.setStackInSlot(OUTPUT, foundOres[Random.nextInt(foundOres.size)]) // OUTPUT
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

    fun calculateNextOutcome(): ItemStack {

        val hit = (data[ACCURACY] / 100.0)
        val miss = 1.0 - (data[ACCURACY] / 100.0)
        val materialChance = hit * (data[MATERIAL_CHANCE] / 100.0)
        val raw = hit * (data[RAW_CHANCE] / 100.0)
        val filter = data[FILTER_CHANCE]
        val higherTier = data[HIGHER_TIER_CHANCE]
        val multi = data[MULTI_CHANCE]
        val amount = Random.nextInt(data[MULTI_MIN], data[MULTI_MAX])


        val ores = foundOres[Random.nextInt(foundOres.size)]


        return ItemStack.EMPTY
    }
}

class MinerEntityTier1(pos: BlockPos, blockState: BlockState) : MinerEntity(pos, blockState, 0)
class MinerEntityTier2(pos: BlockPos, blockState: BlockState) : MinerEntity(pos, blockState, 1)
class MinerEntityTier3(pos: BlockPos, blockState: BlockState) : MinerEntity(pos, blockState, 2)
class MinerEntityTier4(pos: BlockPos, blockState: BlockState) : MinerEntity(pos, blockState, 3)
class MinerEntityTier5(pos: BlockPos, blockState: BlockState) : MinerEntity(pos, blockState, 4)