package com.nred.azurum_miner.machine.miner

import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerVariablesEnum
import com.nred.azurum_miner.screen.GuiCommon.Companion.listPlayerInventoryHotbarPos
import com.nred.azurum_miner.screen.ModMenuTypes
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class MinerMenu : AbstractContainerMenu {
    var level: Level
    var containerData: ContainerData
    var pointsContainerData: ContainerData
    var access: ContainerLevelAccess
    var pos: BlockPos
    var inventory: Inventory
    var tier: Int

    // Server Constructor
    constructor (containerId: Int, inventory: Inventory, access: ContainerLevelAccess, pos: BlockPos, containerData: ContainerData, pointsContainerData: ContainerData, tier: Int) : super(ModMenuTypes.MINER_MENU.get(), containerId) {
        this.level = inventory.player.level()
        this.inventory = inventory
        this.containerData = containerData
        this.pointsContainerData = pointsContainerData
        this.access = access
        this.pos = pos
        this.tier = tier

        // Set Inventory Slot locations
        for (slotInfo in listPlayerInventoryHotbarPos()) {
            this.addSlot(Slot(inventory, slotInfo[0], slotInfo[1], slotInfo[2]))
        }

        this.addDataSlots(this.pointsContainerData)
        this.addDataSlots(this.containerData)
    }

    // Client Constructor
    constructor (containerId: Int, inventory: Inventory, extraData: FriendlyByteBuf) :
            this(containerId, inventory, ContainerLevelAccess.NULL, extraData.readBlockPos(), SimpleContainerData(MinerVariablesEnum.entries.size + MinerVariablesEnum.entries.size), SimpleContainerData(5), extraData.readInt())


    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, ModMachines.MINER_BLOCK_TIERS[this.tier].get())
    }
}