package com.nred.azurum_miner.machine.transmogrifier

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierEntity.Companion.TransmogrifierEnum.*
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierEntity.Companion.get
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierMenu.Companion.slot_x
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierMenu.Companion.slot_y
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.listPlayerInventoryHotbarPos
import com.nred.azurum_miner.util.Payload
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenAxis
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.network.PacketDistributor
import kotlin.math.ceil
import kotlin.math.floor

class TransmogrifierScreen(menu: TransmogrifierMenu, playerInventory: Inventory, title: Component) : AbstractContainerScreen<TransmogrifierMenu>(menu, playerInventory, title) {
    companion object {
        val BASE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/base")
        val SLOT: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/slot")
        val PLAY: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/play")
        val PAUSE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/pause")
        val ENERGY_BAR: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/energy_bar")
        val ENERGY_INNER: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/energy_inner")
        val ARROW: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/arrow")
        val ARROW_FILLED: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "textures/gui/sprites/common/arrow_filled.png")
    }

    var base: ScreenRectangle = ScreenRectangle.empty()
    var powerButton: ScreenRectangle = ScreenRectangle.empty()
    var energy: ScreenRectangle = ScreenRectangle.empty()
    var progressBar: ScreenRectangle = ScreenRectangle.empty()

    private var x = 0
    private var y = 0

    fun resize(width: Int, imageWidth: Int, height: Int, imageHeight: Int) {
        x = (width - imageWidth) / 2
        y = (height - imageHeight) / 2
        base = ScreenRectangle(x, y, imageWidth, imageHeight)
        powerButton = ScreenRectangle(base.right() - 17, base.top() + 5, 12, 13)
        energy = ScreenRectangle(base.left() + 5, base.top() + 5, 6, 65)
        progressBar = ScreenRectangle(base.getCenterInAxis(ScreenAxis.HORIZONTAL) - 11, base.top() + 34, 22, 16)
    }

    override fun resize(minecraft: Minecraft, width: Int, height: Int) {
        super.resize(minecraft, width, height)
        resize(this.width, imageWidth, this.height, imageHeight)
    }

    override fun init() {
        super.init()

        resize(width, imageWidth, height, imageHeight)
        this.titleLabelX = 16
        this.inventoryLabelX = 10
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blitSprite(BASE, base.left(), base.top(), 0, base.width, base.height)

        // Draw Inventory
        for (slotInfo in listPlayerInventoryHotbarPos()) {
            guiGraphics.blitSprite(SLOT, slotInfo[1] + base.left() - 1, slotInfo[2] + base.top() - 1, 18, 18)
        }

        guiGraphics.blitSprite(SLOT, base.left() + slot_x, base.top() + slot_y, 18, 18)
        guiGraphics.blitSprite(SLOT, base.left() + slot_x + 58, base.top() + slot_y, 18, 18)

        // Draw Energy
        val varLen = ceil(menu.energyStorage!!.energyStored.toDouble() / menu.energyStorage!!.maxEnergyStored.toDouble() * (energy.height.toDouble() - 2)).toInt()
        guiGraphics.blitSprite(ENERGY_BAR, energy.left(), energy.top(), 3, energy.width, energy.height)
        guiGraphics.blitSprite(ENERGY_INNER, energy.left() + 1, energy.bottom() - 1 - varLen, 4, energy.width - 2, varLen)

        // Draw Power Button
        if (menu.containerData[IS_ON] == 1) {
            guiGraphics.blitSprite(PAUSE, powerButton.left(), powerButton.top(), 12, 13)
        } else {
            guiGraphics.blitSprite(PLAY, powerButton.left(), powerButton.top(), powerButton.width, powerButton.height)
        }

        // Progress Bar
        guiGraphics.blitSprite(ARROW, progressBar.left(), progressBar.top(), 3, progressBar.width, progressBar.height)
        guiGraphics.blit(ARROW_FILLED, progressBar.left(), progressBar.top(), 4, 0f, 0f, floor(menu.containerData[PROGRESS].toDouble() / menu.containerData[PROCESSING_TIME].toDouble() * (progressBar.width.toDouble())).toInt(), progressBar.height, progressBar.width, progressBar.height)

        if (energy.containsPoint(mouseX, mouseY)) {
            if (hasShiftDown())
                guiGraphics.renderTooltip(font, Component.literal(String.format("%,d FE", menu.energyStorage!!.energyStored)), mouseX, mouseY)
            else
                guiGraphics.renderTooltip(font, Component.literal(getFE(menu.energyStorage!!.energyStored.toDouble())), mouseX, mouseY)
        }
        if (powerButton.containsPoint(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.translatable("tooltip.azurum_miner.machine." + if (menu.containerData[MinerEnum.IS_ON] == 1) "on" else "off"), mouseX, mouseY)
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        renderTooltip(guiGraphics, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && powerButton.containsPoint(mouseX.toInt(), mouseY.toInt())) {
            PacketDistributor.sendToServer(Payload(IS_ON, (menu.containerData[IS_ON]).xor(1), "ENUM", "transmogrifier", menu.pos))
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}
