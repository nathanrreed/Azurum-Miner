package com.nred.azurum_miner.machine.simple_generator

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.MachineScreen
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorEntity.Companion.SimpleGeneratorEnum.PROCESSING_TIME
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorEntity.Companion.SimpleGeneratorEnum.PROGRESS
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorEntity.Companion.get
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import kotlin.math.ceil

class SimpleGeneratorScreen(menu: SimpleGeneratorMenu, playerInventory: Inventory, title: Component) : MachineScreen<SimpleGeneratorMenu>(menu, playerInventory, title) {
    companion object {
        val BASE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "simple_generator")
    }

    override fun resize(width: Int, imageWidth: Int, height: Int, imageHeight: Int) {
        super.resize(width, imageWidth, height, imageHeight)
        energy = ScreenRectangle(base.right() - 13, base.top() + 8, 4, 63)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blitSprite(BASE, base.left(), base.top(), 0, base.width, base.height)

        val varLen = ceil((menu.containerData[PROGRESS].toDouble() / menu.containerData[PROCESSING_TIME].toDouble()) * 14.0).toInt()
        guiGraphics.blitSprite(FLAME_FILLED, 14, 14, 0, 14 - varLen, base.left() + SimpleGeneratorMenu.slot_x + 2, base.top() + SimpleGeneratorMenu.slot_y + 35 - varLen, 14, varLen)

        super.renderBg(guiGraphics, partialTick, mouseX, mouseY)
    }
}