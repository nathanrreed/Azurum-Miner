package com.nred.azurum_miner.machine

import com.nred.azurum_miner.screen.GuiCommon.Companion.listPlayerInventoryHotbarPos
import com.nred.azurum_miner.util.CustomFluidStackHandler
import com.nred.azurum_miner.util.Helpers
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.items.IItemHandler

abstract class MachineMenu(menuType: MenuType<*>, val machineName: String, containerId: Int, val inventory: Inventory, val pos: BlockPos, val containerData: ContainerData, val access: ContainerLevelAccess) : AbstractContainerMenu(menuType, containerId) {
    val itemHandler: IItemHandler? = inventory.player.level().getCapability(Capabilities.ItemHandler.BLOCK, pos, null)
    val fluidHandler: CustomFluidStackHandler? = inventory.player.level().getCapability(Capabilities.FluidHandler.BLOCK, pos, null) as CustomFluidStackHandler?
    val energyStorage: IEnergyStorage? = inventory.player.level().getCapability(Capabilities.EnergyStorage.BLOCK, pos, null)
    val level: Level = inventory.player.level()

    init {
        // Set Inventory Slot locations
        for (slotInfo in listPlayerInventoryHotbarPos()) {
            this.addSlot(Slot(inventory, slotInfo[0], slotInfo[1], slotInfo[2]))
        }

        this.addDataSlots(this.containerData)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return Helpers.quickMoveStack(player, index, slots, ::moveItemStackTo, MachineInfo.data.get(machineName)!!.numItems)
    }
}