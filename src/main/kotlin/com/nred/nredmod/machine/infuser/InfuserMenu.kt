package com.nred.nredmod.machine.infuser

import com.nred.nredmod.machine.ModMachines
import com.nred.nredmod.machine.infuser.InfuserEntity.Companion.InfuserEnum
import com.nred.nredmod.screen.GuiCommon.Companion.listPlayerInventoryHotbarPos
import com.nred.nredmod.screen.ModMenuTypes
import com.nred.nredmod.util.FluidPayload
import com.nred.nredmod.util.Helpers
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler
import net.neoforged.neoforge.network.PacketDistributor

class InfuserMenu : AbstractContainerMenu {
    val itemHandler: IItemHandler
    val fluidHandler: IFluidHandler
    val level: Level
    val containerData: ContainerData
    val access: ContainerLevelAccess
    val pos: BlockPos
    val inventory: Inventory

    companion object {
        val slot_x = 18
        val slot_y = 26
    }

    // Server Constructor
    constructor (containerId: Int, inventory: Inventory, access: ContainerLevelAccess, pos: BlockPos, containerData: ContainerData) : super(ModMenuTypes.INFUSER_MENU.get(), containerId) {
        this.level = inventory.player.level()
        this.inventory = inventory
        this.containerData = containerData
        this.access = access
        this.pos = pos

        // Set Inventory Slot locations
        for (slotInfo in listPlayerInventoryHotbarPos()) {
            this.addSlot(Slot(inventory, slotInfo[0], slotInfo[1], slotInfo[2]))
        }

        this.itemHandler = inventory.player.level().getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.NORTH)!!
        this.addSlot(SlotItemHandler(this.itemHandler, 0, slot_x - 26, slot_y + 7))
        this.addSlot(SlotItemHandler(this.itemHandler, 1, slot_x, slot_y + 29))

        // OUTPUT
        this.addSlot(object : SlotItemHandler(this.itemHandler, 2, slot_x + 28, slot_y + 7) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return false
            }
        })

        this.fluidHandler = inventory.player.level().getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.NORTH)!!

        this.addDataSlots(this.containerData)
    }

    override fun broadcastChanges() {
        if (inventory.player is ServerPlayer)
            PacketDistributor.sendToPlayer(inventory.player as ServerPlayer, FluidPayload(fluidHandler.getFluidInTank(0)))

        super.broadcastChanges()
    }

    // Client Constructor
    constructor (containerId: Int, inventory: Inventory, extraData: FriendlyByteBuf) :
            this(containerId, inventory, ContainerLevelAccess.NULL, extraData.readBlockPos(), SimpleContainerData(InfuserEnum.entries.size))

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return Helpers.quickMoveStack(player, index, slots, ::moveItemStackTo, 3)
    }

    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, ModMachines.INFUSER.get())
    }
}