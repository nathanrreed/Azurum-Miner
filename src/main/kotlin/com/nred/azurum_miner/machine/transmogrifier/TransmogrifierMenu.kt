package com.nred.azurum_miner.machine.transmogrifier

import com.nred.azurum_miner.machine.MachineMenu
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.ModMachines.SlotItemHandlerWithPickup
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierEntity.Companion.TransmogrifierEnum
import com.nred.azurum_miner.screen.ModMenuTypes
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class TransmogrifierMenu : MachineMenu {
    val inputSlots = ArrayList<Slot>()

    companion object {
        const val slot_x = 49
        const val slot_y = 34
    }

    // Server Constructor
    constructor (containerId: Int, inventory: Inventory, access: ContainerLevelAccess, pos: BlockPos, containerData: ContainerData) : super(ModMenuTypes.TRANSMOGRIFIER_MENU.get(), "transmogrifier", containerId, inventory, pos, containerData, access) {
        inputSlots += this.addSlot(SlotItemHandlerWithPickup(this.itemHandler!!, 0, slot_x + 1, slot_y + 1))

        // OUTPUT
        this.addSlot(object : SlotItemHandlerWithPickup(this.itemHandler, 1, slot_x + 59, slot_y + 1) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return false
            }
        })
    }

    // Client Constructor
    constructor (containerId: Int, inventory: Inventory, extraData: FriendlyByteBuf) :
            this(containerId, inventory, ContainerLevelAccess.NULL, extraData.readBlockPos(), SimpleContainerData(TransmogrifierEnum.entries.size))

    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, ModMachines.TRANSMOGRIFIER.get())
    }
}