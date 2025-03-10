package com.nred.azurum_miner.machine.transmogrifier

import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.ModMachines.SlotItemHandlerWithPickup
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierEntity.Companion.TransmogrifierEnum
import com.nred.azurum_miner.screen.GuiCommon.Companion.listPlayerInventoryHotbarPos
import com.nred.azurum_miner.screen.ModMenuTypes
import com.nred.azurum_miner.util.Helpers
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler

class TransmogrifierMenu : AbstractContainerMenu {
    val itemHandler: IItemHandler
    val level: Level
    val containerData: ContainerData
    val access: ContainerLevelAccess
    val pos: BlockPos
    val inventory: Inventory
    val inputSlots = ArrayList<Slot>()

    companion object {
        const val slot_x = 49
        const val slot_y = 34
    }

    // Server Constructor
    constructor (containerId: Int, inventory: Inventory, access: ContainerLevelAccess, pos: BlockPos, containerData: ContainerData) : super(ModMenuTypes.TRANSMOGRIFIER_MENU.get(), containerId) {
        this.level = inventory.player.level()
        this.inventory = inventory
        this.containerData = containerData
        this.access = access
        this.pos = pos

        // Set Inventory Slot locationsF
        for (slotInfo in listPlayerInventoryHotbarPos()) {
            this.addSlot(Slot(inventory, slotInfo[0], slotInfo[1], slotInfo[2]))
        }

        this.itemHandler = inventory.player.level().getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.NORTH)!!
        inputSlots += this.addSlot(SlotItemHandlerWithPickup(this.itemHandler, 0, slot_x + 1, slot_y + 1))

        // OUTPUT
        this.addSlot(object : SlotItemHandlerWithPickup(this.itemHandler, 1, slot_x + 59, slot_y + 1) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return false
            }
        })

        this.addDataSlots(this.containerData)
    }

    // Client Constructor
    constructor (containerId: Int, inventory: Inventory, extraData: FriendlyByteBuf) :
            this(containerId, inventory, ContainerLevelAccess.NULL, extraData.readBlockPos(), SimpleContainerData(TransmogrifierEnum.entries.size))

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return Helpers.quickMoveStack(player, index, slots, ::moveItemStackTo, 2)
    }

    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, ModMachines.TRANSMOGRIFIER.get())
    }
}