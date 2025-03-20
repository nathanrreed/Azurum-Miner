package com.nred.azurum_miner.machine.infuser

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.MachineScreen
import com.nred.azurum_miner.machine.infuser.InfuserEntity.Companion.InfuserEnum.*
import com.nred.azurum_miner.machine.infuser.InfuserEntity.Companion.get
import com.nred.azurum_miner.util.ClearPayload
import com.nred.azurum_miner.util.Payload
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenAxis
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.network.PacketDistributor
import kotlin.math.floor

class InfuserScreen(menu: InfuserMenu, playerInventory: Inventory, title: Component) : MachineScreen<InfuserMenu>(menu, playerInventory, title) {
    companion object {
        val BASE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "infuser")
    }

    override fun resize(width: Int, imageWidth: Int, height: Int, imageHeight: Int) {
        super.resize(width, imageWidth, height, imageHeight)
        powerButton = ScreenRectangle(base.right() - 17, base.top() + 5, 12, 13)
        energy = ScreenRectangle(base.left() + 6, base.top() + 6, 4, 63)
        tank = ScreenRectangle(base.right() - 55, base.top() + 7, 34, 75)
        progressBar = ScreenRectangle(base.getCenterInAxis(ScreenAxis.HORIZONTAL) - 20, base.top() + 35, 22, 16)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blitSprite(BASE, base.left(), base.top(), 0, base.width, base.height)

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
            PacketDistributor.sendToServer(Payload(IS_ON, (menu.containerData[IS_ON]).xor(1), "ENUM", "infuser", menu.pos))
            return true
        } else if (hasShiftDown() && button == 0 && tank.containsPoint(mouseX.toInt(), mouseY.toInt())) {
            menu.fluidHandler!!.internalExtractFluid(menu.fluidHandler!!.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE)
            PacketDistributor.sendToServer(ClearPayload(0, menu.pos))
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}