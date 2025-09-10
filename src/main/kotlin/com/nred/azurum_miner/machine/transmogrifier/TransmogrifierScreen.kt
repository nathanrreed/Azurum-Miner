package com.nred.azurum_miner.machine.transmogrifier

import com.nred.azurum_miner.machine.MachineScreen
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierEntity.Companion.TransmogrifierEnum.*
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierEntity.Companion.get
import com.nred.azurum_miner.util.Helpers.azLoc
import com.nred.azurum_miner.util.Payload
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenAxis
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.network.PacketDistributor
import kotlin.math.ceil
import kotlin.math.floor

@OnlyIn(Dist.CLIENT)
class TransmogrifierScreen(menu: TransmogrifierMenu, playerInventory: Inventory, title: Component) : MachineScreen<TransmogrifierMenu>(menu, playerInventory, title) {
    companion object {
        val BASE: ResourceLocation = azLoc("transmogrifier")
    }

    override fun resize(width: Int, imageWidth: Int, height: Int, imageHeight: Int) {
        super.resize(width, imageWidth, height, imageHeight)
        powerButton = ScreenRectangle(base.right() - 17, base.top() + 5, 12, 13)
        energy = ScreenRectangle(base.left() + 6, base.top() + 6, 4, 63)
        progressBar = ScreenRectangle(base.getCenterInAxis(ScreenAxis.HORIZONTAL) - 10, base.top() + 35, 22, 16)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blitSprite(BASE, base.left(), base.top(), 0, base.width, base.height)

        // Draw Energy
        val varLen = ceil(menu.energyStorage!!.energyStored.toDouble() / menu.energyStorage!!.maxEnergyStored.toDouble() * (energy.height.toDouble() - 2)).toInt()
        guiGraphics.blitSprite(ENERGY_INNER, energy.left() + 1, energy.bottom() - 1 - varLen, 4, energy.width - 2, varLen)

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
            PacketDistributor.sendToServer(Payload(IS_ON, (menu.containerData[IS_ON]).xor(1), "ENUM", "transmogrifier", menu.pos))
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}
