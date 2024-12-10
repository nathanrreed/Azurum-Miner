package com.nred.nredmod.machine.liquifier

import com.nred.nredmod.NredMod
import com.nred.nredmod.machine.liquifier.LiquifierEntity.Companion.LiquifierEnum.*
import com.nred.nredmod.machine.liquifier.LiquifierEntity.Companion.get
import com.nred.nredmod.machine.miner.MinerEntity.Companion.MinerEnum
import com.nred.nredmod.screen.GuiCommon.Companion.getFE
import com.nred.nredmod.screen.GuiCommon.Companion.listPlayerInventoryHotbarPos
import com.nred.nredmod.screen.blitTile
import com.nred.nredmod.util.Payload
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenAxis
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.InventoryMenu
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.network.PacketDistributor
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.floor

class LiquifierScreen(menu: LiquifierMenu, playerInventory: Inventory, title: Component) : AbstractContainerScreen<LiquifierMenu>(menu, playerInventory, title) {
    companion object {
        val BASE = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "common/base")
        val SLOT = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "common/slot")
        val PLAY = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "common/play")
        val PAUSE = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "common/pause")
        val TANK = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "common/fluid_tank")
        val ENERGY_BAR = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "common/energy_bar")
        val ENERGY_INNER = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "common/energy_inner")
        val ARROW = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "common/arrow")
        val ARROW_FILLED = ResourceLocation.fromNamespaceAndPath(NredMod.ID, "textures/gui/sprites/common/arrow_filled.png")

        var base = ScreenRectangle.empty()
        var powerButton = ScreenRectangle.empty()
        var energy = ScreenRectangle.empty()
        var tank = ScreenRectangle.empty()
        var progressBar = ScreenRectangle.empty()

        private var x = 0
        private var y = 0

        fun resize(width: Int, imageWidth: Int, height: Int, imageHeight: Int) {
            x = (width - imageWidth) / 2 - 51
            y = (height - imageHeight) / 2 - 32
            base = ScreenRectangle(x, y + 30, imageWidth, imageHeight)
            powerButton = ScreenRectangle(base.right() - 17, base.top() + 5, 12, 13)
            energy = ScreenRectangle(base.left() + 5, base.top() + 5, 6, 65)
            tank = ScreenRectangle(base.right() - 55, base.top() + 5, 31, 77)
            progressBar = ScreenRectangle(base.getCenterInAxis(ScreenAxis.HORIZONTAL) - 11, base.top() + 34, 22, 16)
        }
    }

    lateinit var fluid: FluidStack

    override fun resize(minecraft: Minecraft, width: Int, height: Int) {
        super.resize(minecraft, width, height)
        resize(this.width, imageWidth, this.height, imageHeight)
    }

    override fun init() {
        super.init()

        this.fluid = FluidStack.EMPTY

        resize(width, imageWidth, height, imageHeight)
        this.titleLabelX = -36
//        this.titleLabelY = 1
        this.inventoryLabelX = -42
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blitSprite(BASE, base.left(), base.top(), 0, base.width, base.height)

        // Draw Inventory
        for (slotInfo in listPlayerInventoryHotbarPos()) {
            guiGraphics.blitSprite(SLOT, slotInfo[1] + x + 50, slotInfo[2] + y + 31, 18, 18)
        }

        guiGraphics.blitSprite(SLOT, base.left() + LiquifierMenu.slot_x + 20, base.top() + LiquifierMenu.slot_y + 8, 18, 18)

        // Draw Energy
        val varLen = ceil(menu.containerData[ENERGY_LEVEL].toDouble() / menu.containerData[ENERGY_CAPACITY].toDouble() * (energy.height.toDouble() - 2)).toInt()
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

        // FLUID
        if (!fluid.isEmpty) {
            val varMoltenLen = floor(fluid.amount.toDouble() / LiquifierEntity.FLUID_SIZE.toDouble() * (tank.height - 2)).toInt()
            blitTile(guiGraphics, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid.fluid).stillTexture), tank.left(), tank.bottom() - varMoltenLen - 2, tank.width - 2, varMoltenLen, 16, 16, IClientFluidTypeExtensions.of(fluid.fluid).tintColor)
        }

        // TANK
        guiGraphics.blitSprite(TANK, tank.left() - 1, tank.top() - 1, 4, tank.width, tank.height)

        if (energy.containsPoint(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.literal(getFE(menu.containerData[ENERGY_LEVEL].toDouble())), mouseX, mouseY)
        }
        if (powerButton.containsPoint(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.translatable("tooltip.nredmod.machine." + if (menu.containerData[MinerEnum.IS_ON] == 1) "on" else "off"), mouseX, mouseY)
        }
        if (tank.containsPoint(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.translatable("tooltip.nredmod.liquifier.info.tank", fluid.fluidType.description, DecimalFormat("#,###").format(fluid.amount)), mouseX, mouseY)
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        renderTooltip(guiGraphics, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && powerButton.containsPoint(mouseX.toInt(), mouseY.toInt())) {
            PacketDistributor.sendToServer(Payload(IS_ON, (menu.containerData[IS_ON]).xor(1), "ENUM", "liquifier", menu.pos))
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}