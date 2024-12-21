package com.nred.azurum_miner.machine.miner

import com.nred.azurum_miner.datagen.ModItemTagProvider
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerVariablesEnum
import com.nred.azurum_miner.screen.GuiCommon.Companion.listPlayerInventoryHotbarPos
import com.nred.azurum_miner.screen.ModMenuTypes
import com.nred.azurum_miner.util.Helpers
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler

class MinerMenu : AbstractContainerMenu {
    val itemHandler: IItemHandler
    var level: Level
    var containerData: ContainerData
    var pointsContainerData: ContainerData
    var access: ContainerLevelAccess
    var pos: BlockPos
    var inventory: Inventory
    var tier: Int
    val filters = mutableListOf("", "", "")
    val filterSlots = ArrayList<FilterSlot>()
    val foundTags = ArrayList<TagKey<Item>>()
    var containerId: Int

    // Server Constructor
    constructor (containerId: Int, inventory: Inventory, access: ContainerLevelAccess, pos: BlockPos, containerData: ContainerData, pointsContainerData: ContainerData, tier: Int) : super(ModMenuTypes.MINER_MENU.get(), containerId) {
        this.level = inventory.player.level()
        this.inventory = inventory
        this.containerData = containerData
        this.pointsContainerData = pointsContainerData
        this.access = access
        this.pos = pos
        this.tier = tier
        this.containerId = containerId

        // Set Inventory Slot locations
        for (slotInfo in listPlayerInventoryHotbarPos(60)) {
            this.addSlot(Slot(inventory, slotInfo[0], slotInfo[1], slotInfo[2]))
        }

        this.addDataSlots(this.pointsContainerData)
        this.addDataSlots(this.containerData)

        this.itemHandler = inventory.player.level().getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.NORTH)!!
        this.filterSlots += FilterSlot(this.itemHandler, 0, 11, -10, this)
        this.addSlot(this.filterSlots.last())
        this.filterSlots += FilterSlot(this.itemHandler, 1, 11, 15, this)
        this.addSlot(this.filterSlots.last())
        this.filterSlots += FilterSlot(this.itemHandler, 2, 11, 40, this)
        this.addSlot(this.filterSlots.last())

        for (i in 0..this.tier) {
            foundTags += ModItemTagProvider.oreTierTag[i]
        }
        foundTags += ModItemTagProvider.materialTag
    }

    // Client Constructor
    constructor (containerId: Int, inventory: Inventory, extraData: FriendlyByteBuf) :
            this(containerId, inventory, ContainerLevelAccess.NULL, extraData.readBlockPos(), SimpleContainerData(MinerVariablesEnum.entries.size + MinerVariablesEnum.entries.size), SimpleContainerData(5), extraData.readInt())

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return Helpers.quickMoveStack(player, index, this.slots, ::moveItemStackTo, 0) // No quick move for filters
    }

    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, ModMachines.MINER_BLOCK_TIERS[this.tier].get())
    }

    class FilterSlot(itemHandler: IItemHandler, index: Int, xPosition: Int, yPosition: Int, val menu: MinerMenu) : SlotItemHandler(itemHandler, index, xPosition, yPosition) {
        var dontUse = false
            set(value) {
                field = value
            }

        var active = false
            set(value) {
                field = value
            }

        override fun isFake(): Boolean {
            return true
        }

        override fun isActive(): Boolean { // Only draw if filter is unlocked
            return this.active
        }

        override fun getMaxStackSize(): Int {
            return 1
        }

        override fun mayPlace(stack: ItemStack): Boolean {
            return if (stack.isEmpty || !Ingredient.of(Ingredient.of(ItemTags.create(ResourceLocation.parse(menu.filters[index]))).items.filter { it -> menu.foundTags.any { tag -> it.`is`(tag) } }.stream()).hasNoItems()) false else this.itemHandler.isItemValid(this.index, stack)
        }

        override fun mayPickup(playerIn: Player): Boolean {
            return false
        }

        override fun safeInsert(stack: ItemStack): ItemStack {
            return super.safeInsert(stack)
        }
    }
}