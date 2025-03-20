package com.nred.azurum_miner.machine.simple_generator

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

class SimpleGeneratorMenu : MachineMenu {
    val inputSlots = ArrayList<Slot>()

    companion object {
        const val slot_x = 79
        const val slot_y = 32
    }

    // Server Constructor
    constructor (containerId: Int, inventory: Inventory, access: ContainerLevelAccess, pos: BlockPos, containerData: ContainerData) : super(ModMenuTypes.SIMPLE_GENERATOR_MENU.get(), "simple_generator", containerId, inventory, pos, containerData, access) {
        inputSlots += this.addSlot(SlotItemHandlerWithPickup(this.itemHandler!!, 0, slot_x + 1, slot_y + 1))
    }

    // Client Constructor
    constructor (containerId: Int, inventory: Inventory, extraData: FriendlyByteBuf) :
            this(containerId, inventory, ContainerLevelAccess.NULL, extraData.readBlockPos(), SimpleContainerData(GeneratorEnum.entries.size))

    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, ModMachines.SIMPLE_GENERATOR.get())
    }
}