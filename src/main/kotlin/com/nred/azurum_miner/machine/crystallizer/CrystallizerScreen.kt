package com.nred.azurum_miner.machine.crystallizer

import com.nred.azurum_miner.machine.MachineScreen
import com.nred.azurum_miner.machine.crystallizer.CrystallizerEntity.Companion.CrystallizerEnum.*
import com.nred.azurum_miner.machine.crystallizer.CrystallizerEntity.Companion.get
import com.nred.azurum_miner.util.Helpers.azLoc
import com.nred.azurum_miner.util.Payload
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenAxis
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.network.PacketDistributor
import kotlin.math.ceil
import kotlin.math.floor

class CrystallizerScreen(menu: CrystallizerMenu, playerInventory: Inventory, title: Component) : MachineScreen<CrystallizerMenu>(menu, playerInventory, title) {
    companion object {
        val BASE: ResourceLocation = azLoc("crystallizer")
    }

    override fun resize(width: Int, imageWidth: Int, height: Int, imageHeight: Int) {
        super.resize(width, imageWidth, height, imageHeight)
        powerButton = ScreenRectangle(base.right() - 17, base.top() + 5, 12, 13)
        energy = ScreenRectangle(base.left() + 6, base.top() + 6, 4, 63)
        tanks = listOf(ScreenRectangle(base.right() - 65, base.top() + 7, 34, 75))
        progressBar = ScreenRectangle(base.getCenterInAxis(ScreenAxis.HORIZONTAL) - 9, base.top() + 35, 22, 16)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blitSprite(BASE, base.left(), base.top(), 0, base.width, base.height)

        // Draw Energy
        val varLen = ceil(menu.energyStorage!!.energyStored.toDouble() / menu.energyStorage!!.maxEnergyStored.toDouble() * (energy.height.toDouble() - 2)).toInt()
        guiGraphics.blitSprite(ENERGY_INNER, energy.left(), energy.bottom() - varLen, 4, energy.width, varLen)

        // Draw Power Button
        if (menu.containerData[IS_ON] == 1) {
            guiGraphics.blitSprite(PAUSE, powerButton.left(), powerButton.top(), 12, 13)
        } else {
            guiGraphics.blitSprite(PLAY, powerButton.left(), powerButton.top(), powerButton.width, powerButton.height)
        }

        // Progress Bar
        guiGraphics.blit(ARROW_FILLED, progressBar.left(), progressBar.top(), 4, 0f, 0f, floor(menu.containerData[PROGRESS].toDouble() / menu.containerData[PROCESSING_TIME].toDouble() * (progressBar.width.toDouble())).toInt(), progressBar.height, progressBar.width, progressBar.height)

        super.renderBg(guiGraphics, partialTick, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && powerButton.containsPoint(mouseX.toInt(), mouseY.toInt())) {
            PacketDistributor.sendToServer(Payload(IS_ON, (menu.containerData[IS_ON]).xor(1), "ENUM", "crystallizer", menu.pos))
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}