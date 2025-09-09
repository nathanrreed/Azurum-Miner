package com.nred.azurum_miner.machine.generator

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.machine.AbstractMachineBlockEntity
import com.nred.azurum_miner.machine.ExtendedItemStackHandler
import com.nred.azurum_miner.machine.generator.GeneratorEntity.Companion.GeneratorEnum.*
import com.nred.azurum_miner.recipe.GeneratorInput
import com.nred.azurum_miner.recipe.GeneratorRecipe
import com.nred.azurum_miner.recipe.ModRecipe
import com.nred.azurum_miner.util.FALSE
import com.nred.azurum_miner.util.TRUE
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.client.extensions.IMenuProviderExtension
import net.neoforged.neoforge.energy.IEnergyStorage
import kotlin.random.Random

const val FUEL_SLOT = 0
const val BASE_SLOT = 1
const val MATRIX_SLOT = 2
const val OUTPUT_SLOT = 3
const val FUEL_SLOT_SAVE = 4
const val BASE_SLOT_SAVE = 5

open class GeneratorEntity(pos: BlockPos, blockState: BlockState) : AbstractMachineBlockEntity(ModBlockEntities.GENERATOR_ENTITY.get(), "generator", pos, blockState), IMenuProviderExtension {
    override var variables = IntArray(GeneratorEnum.entries.size)
    override var variablesSize = GeneratorEnum.entries.size

    var currFuelRecipe: GeneratorRecipe? = null
    var currBaseRecipe: GeneratorRecipe? = null
    private var matrixFinish: Int
    private var shardChance: Double

    override var data: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return this@GeneratorEntity.variables[index]
        }

        override fun set(index: Int, value: Int) {
            this@GeneratorEntity.variables[index] = value
        }

        override fun getCount(): Int {
            return variables.size
        }
    }

    override fun getProgress(): Float {
        return 0f
    }

    init {
        data[HAS_BASE] = FALSE
        data[HAS_FUEL] = FALSE
        matrixFinish = CONFIG.getInt("generator.matrixDurability")
        shardChance = CONFIG.get("generator.shardChance")
    }

    override val itemStackHandler = object : ExtendedItemStackHandler(6) {
        override fun onContentsChanged(slot: Int) {
            if (slot == MATRIX_SLOT && !this.getStackInSlot(slot).isEmpty) {
                if (matrixFinish > 0 && this.getStackInSlot(slot).maxDamage != matrixFinish)
                    this.getStackInSlot(slot).set(DataComponents.MAX_DAMAGE, matrixFinish)
            }

            setChanged()
            if (!level!!.isClientSide()) {
                level!!.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), Block.UPDATE_ALL)
            }
        }

        override fun getStackLimit(slot: Int, stack: ItemStack): Int {
            if (slot == OUTPUT_SLOT) {
                return super.getSlotLimit(slot)
            }
            return 1
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            if (slot == FUEL_SLOT && this.getStackInSlot(FUEL_SLOT_SAVE).isEmpty) {
                return level!!.recipeManager.getAllRecipesFor(ModRecipe.GENERATOR_RECIPE_TYPE.get()).filter { it.value.typeName == "fuel" }.any { it.value.input.`is`(stack.item) }
            } else if (slot == BASE_SLOT && this.getStackInSlot(BASE_SLOT_SAVE).isEmpty) {
                return level!!.recipeManager.getAllRecipesFor(ModRecipe.GENERATOR_RECIPE_TYPE.get()).filter { it.value.typeName == "base" }.any { it.value.input.`is`(stack.item) }
            } else if (slot == MATRIX_SLOT) {
                return stack.`is`(ModItems.DIMENSIONAL_MATRIX.get())
            } else if (slot == OUTPUT_SLOT) {
                return stack.`is`(ModItems.ENERGY_SHARD.get())
            }
            return false
        }

        override fun itemOutput(slot: Int): Boolean {
            return false
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (slot == OUTPUT_SLOT || (slot == MATRIX_SLOT && !simulate)) {
                return super.extractItem(slot, amount, simulate)
            }
            return ItemStack.EMPTY
        }
    }

    companion object {
        enum class GeneratorEnum() {
            HAS_BASE, HAS_FUEL, FUEL_POWER, BASE_MULT, FUEL_CURR, BASE_CURR, FUEL_LASTS, BASE_LASTS
        }

        operator fun ContainerData.get(e: Enum<*>): Int {
            return this.get(e.ordinal)
        }

        operator fun ContainerData.set(e: Enum<*>, value: Int) {
            this.set(e.ordinal, value)
        }
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): GeneratorMenu {
        return GeneratorMenu(containerId, playerInventory, ContainerLevelAccess.create(level!!, blockPos), blockPos, this.data)
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
        if (!this.loaded) return

        for (dir in Direction.entries) {
            val storage: IEnergyStorage? = level.getCapability(Capabilities.EnergyStorage.BLOCK, blockPos.relative(dir), dir.opposite)
            if (storage != null && storage.canReceive()) {
                val extracted = energyHandler.energyStored
                energyHandler.internalExtractEnergy(extracted, false)
                val actual = storage.receiveEnergy(extracted, false)
                if (extracted != actual) {
                    energyHandler.internalInsertEnergy(extracted - actual, false)
                }
            }
        }

        if (itemStackHandler.getStackInSlot(MATRIX_SLOT).isEmpty || energyHandler.energyStored >= energyHandler.maxEnergyStored) return

        if (currBaseRecipe == null && !itemStackHandler.getStackInSlot(BASE_SLOT_SAVE).isEmpty)
            currBaseRecipe = level.recipeManager.getRecipeFor(ModRecipe.GENERATOR_RECIPE_TYPE.get(), GeneratorInput(blockState, itemStackHandler.getStackInSlot(BASE_SLOT_SAVE), "base"), level).get().value
        if (currFuelRecipe == null && !itemStackHandler.getStackInSlot(FUEL_SLOT_SAVE).isEmpty)
            currFuelRecipe = level.recipeManager.getRecipeFor(ModRecipe.GENERATOR_RECIPE_TYPE.get(), GeneratorInput(blockState, itemStackHandler.getStackInSlot(FUEL_SLOT_SAVE), "fuel"), level).get().value

        val needNewBase = currBaseRecipe == null && !itemStackHandler.getStackInSlot(BASE_SLOT).isEmpty
        val needNewFuel = currFuelRecipe == null && !itemStackHandler.getStackInSlot(FUEL_SLOT).isEmpty

        if ((currFuelRecipe != null || needNewFuel) && needNewBase) { // New base
            currBaseRecipe = level.recipeManager.getRecipeFor(ModRecipe.GENERATOR_RECIPE_TYPE.get(), GeneratorInput(state, itemStackHandler.getStackInSlot(BASE_SLOT), "base"), level).get().value
            data[BASE_CURR] = 0
            itemStackHandler.setStackInSlot(BASE_SLOT_SAVE, itemStackHandler.getStackInSlot(BASE_SLOT).split(1))
            data[HAS_BASE] = TRUE
            val buffer = Unpooled.buffer()
            ByteBufCodecs.FLOAT.encode(buffer, currBaseRecipe!!.multiplier)
            data[BASE_MULT] = buffer.getInt(0)
            data[BASE_LASTS] = currBaseRecipe!!.lasts
        }
        if ((currBaseRecipe != null || needNewBase) && needNewFuel) { // New fuel
            currFuelRecipe = level.recipeManager.getRecipeFor(ModRecipe.GENERATOR_RECIPE_TYPE.get(), GeneratorInput(state, itemStackHandler.getStackInSlot(FUEL_SLOT), "fuel"), level).get().value
            itemStackHandler.setStackInSlot(FUEL_SLOT_SAVE, itemStackHandler.getStackInSlot(FUEL_SLOT).split(1))
            data[FUEL_CURR] = 0
            data[HAS_FUEL] = TRUE
            data[FUEL_POWER] = currFuelRecipe!!.power
            data[FUEL_LASTS] = currFuelRecipe!!.lasts
        }

        if (currFuelRecipe != null && currBaseRecipe != null) {
            energyHandler.internalInsertEnergy((currFuelRecipe!!.power * currBaseRecipe!!.multiplier).toInt(), false)

            if (matrixFinish > 0) {
                if (itemStackHandler.getStackInSlot(MATRIX_SLOT).damageValue >= matrixFinish) {
                    itemStackHandler.setStackInSlot(MATRIX_SLOT, ItemStack.EMPTY)
                } else {
                    itemStackHandler.getStackInSlot(MATRIX_SLOT).damageValue++
                }
            }

            data[FUEL_CURR]++
            data[BASE_CURR]++

            if (data[FUEL_CURR] > currFuelRecipe!!.lasts) {
                currFuelRecipe = null
                itemStackHandler.setStackInSlot(FUEL_SLOT_SAVE, ItemStack.EMPTY)
            }
            if (data[BASE_CURR] > currBaseRecipe!!.lasts) {
                currBaseRecipe = null
                itemStackHandler.setStackInSlot(BASE_SLOT_SAVE, ItemStack.EMPTY)
            }

            if (Random.nextDouble() <= shardChance) {
                itemStackHandler.internalInsertItem(OUTPUT_SLOT, ItemStack(ModItems.ENERGY_SHARD.get(), 1), false)
            }
        } else {
            if (currFuelRecipe == null) {
                data[HAS_FUEL] = FALSE
            }
            if (currBaseRecipe == null) {
                data[HAS_BASE] = FALSE
            }
        }

        setChanged()
    }
}