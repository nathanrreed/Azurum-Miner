package com.nred.azurum_miner.machine.generator

import com.nred.azurum_miner.machine.MachineMenu
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.ModMachines.SlotItemHandlerWithPickup
import com.nred.azurum_miner.machine.generator.GeneratorEntity.Companion.GeneratorEnum
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
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler

class GeneratorMenu : MachineMenu {
    val inputSlots = ArrayList<Slot>()

    companion object {
        const val slot_x = 20
        const val slot_y = 19
    }

    // Server Constructor
    constructor (containerId: Int, inventory: Inventory, access: ContainerLevelAccess, pos: BlockPos, containerData: ContainerData) : super(ModMenuTypes.GENERATOR_MENU.get(), "generator", containerId, inventory, pos, containerData, access) {
        class FakeSlotEmptyItemHandler(itemHandler: IItemHandler, val index: Int, x: Int, y: Int) : SlotItemHandler(itemHandler, index, x, y) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return super.mayPlace(stack)
            }

            override fun isHighlightable(): Boolean {
                return this@GeneratorMenu.itemHandler!!.getStackInSlot(index + FUEL_SLOT_SAVE).isEmpty
            }
        }

        inputSlots += this.addSlot(FakeSlotEmptyItemHandler(this.itemHandler!!, FUEL_SLOT, slot_x + 1, slot_y + 1))
        inputSlots += this.addSlot(FakeSlotEmptyItemHandler(this.itemHandler, BASE_SLOT, slot_x + 1, slot_y + 28))
        inputSlots += this.addSlot(SlotItemHandlerWithPickup(this.itemHandler, MATRIX_SLOT, 137, slot_y + 1))

        // OUTPUT
        this.addSlot(object : SlotItemHandlerWithPickup(this.itemHandler, OUTPUT_SLOT, 137, slot_y + 28) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return false
            }
        })
        class FakeSlotItemHandler(itemHandler: IItemHandler, index: Int) : SlotItemHandlerWithPickup(itemHandler, index, 0, 0, false) {
            override fun isActive(): Boolean {
                return false
            }
        }

        inputSlots += this.addSlot(FakeSlotItemHandler(this.itemHandler, FUEL_SLOT_SAVE))
        inputSlots += this.addSlot(FakeSlotItemHandler(this.itemHandler, BASE_SLOT_SAVE))
    }

    // Client Constructor
    constructor (containerId: Int, inventory: Inventory, extraData: FriendlyByteBuf) :
            this(containerId, inventory, ContainerLevelAccess.NULL, extraData.readBlockPos(), SimpleContainerData(GeneratorEnum.entries.size))

    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, ModMachines.GENERATOR.get())
    }
}