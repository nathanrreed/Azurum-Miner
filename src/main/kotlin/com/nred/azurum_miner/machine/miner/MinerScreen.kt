package com.nred.azurum_miner.machine.miner

import com.mojang.blaze3d.platform.InputConstants
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum.IS_ON
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerVariablesEnum.*
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.get
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.listPlayerInventoryHotbarPos
import com.nred.azurum_miner.screen.SpriteTabButton
import com.nred.azurum_miner.screen.SpriteTabButton.Companion.FILTER_ICON
import com.nred.azurum_miner.screen.SpriteTabButton.Companion.INV_ICON
import com.nred.azurum_miner.screen.SpriteTabButton.Companion.OPTIONS_ICON
import com.nred.azurum_miner.screen.VerticalTabNavigationBar
import com.nred.azurum_miner.util.Helpers.azLoc
import com.nred.azurum_miner.util.Helpers.componentSplit
import com.nred.azurum_miner.util.Payload
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.tabs.TabManager
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.network.PacketDistributor
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

@OnlyIn(Dist.CLIENT)
class MinerScreen(menu: MinerMenu, playerInventory: Inventory, title: Component) : AbstractContainerScreen<MinerMenu>(menu, playerInventory, title) {
    companion object {
        val BASE: ResourceLocation = azLoc("common/base")
        val SLOT: ResourceLocation = azLoc("common/slot")
        val PLAY: ResourceLocation = azLoc("common/play")
        val PAUSE: ResourceLocation = azLoc("common/pause")
        val ENERGY_BAR: ResourceLocation = azLoc("common/energy_bar")
        val ENERGY_INNER: ResourceLocation = azLoc("common/energy_inner")
        val TANK: ResourceLocation = azLoc("miner/fluid_tank")
    }

    var base: ScreenRectangle = ScreenRectangle.empty()
    var powerButton: ScreenRectangle = ScreenRectangle.empty()
    var energy: ScreenRectangle = ScreenRectangle.empty()
    val tabManager = TabManager({ widget -> this.addRenderableWidget(widget) }, { widget -> this.removeWidget(widget) })
    lateinit var navigationBar: VerticalTabNavigationBar

    private fun resize() {
        x = (width - imageWidth) / 2
        y = (height - imageHeight) / 2 - 32
        base = ScreenRectangle(x - 10, y, imageWidth + 57, imageHeight + 33)
        powerButton = ScreenRectangle(base.left() + 4, base.bottom() - 98, 12, 13)
        energy = ScreenRectangle(base.left() + 7, base.top() + 14, 6, 84)
    }

    override fun resize(minecraft: Minecraft, width: Int, height: Int) {
        val saveCurrentTab = navigationBar.saveCurrentTab()
        super.resize(minecraft, width, height)
        resize()

        navigationBar.loadCurrentTab(saveCurrentTab)
    }

    override fun init() {
        super.init()

        resize()
        this.titleLabelX = 8
        this.titleLabelY = -26
        this.inventoryLabelX = 10

        tabManager.setTabArea(base)
        val upgradeTab = UpgradeTab(menu)
        val filterTab = FilterTab(menu)
        val inventoryTab = InventoryTab(menu)

        val upgradeBtn = SpriteTabButton(tabManager, upgradeTab, 12, 18, OPTIONS_ICON)
        val filterBtn = SpriteTabButton(tabManager, filterTab, 12, 18, FILTER_ICON)
        val invButton = SpriteTabButton(tabManager, inventoryTab, 12, 18, INV_ICON)

        this.navigationBar = VerticalTabNavigationBar(x - 21, base.top() + 6, tabManager, listOf(invButton, upgradeBtn, filterBtn))

        this.addRenderableWidget(navigationBar)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blitSprite(BASE, base.left(), base.top(), 0, base.width, base.height)

        // Draw Inventory
        for (slotInfo in listPlayerInventoryHotbarPos()) {
            guiGraphics.blitSprite(SLOT, slotInfo[1] + base.left() + 9, slotInfo[2] + base.top() + 31, 18, 18)
        }

        // Draw Energy
        val varLen = ceil(menu.energyStorage.energyStored.toDouble() / menu.energyStorage.maxEnergyStored.toDouble() * 82.0).toInt()

        guiGraphics.blitSprite(ENERGY_BAR, energy.left(), energy.top(), 3, energy.width, energy.height)
        guiGraphics.blitSprite(ENERGY_INNER, energy.left() + 1, energy.bottom() - 1 - varLen, 4, energy.width - 2, varLen)

        // Draw Power Button
        if (menu.containerData[IS_ON] == 1) {
            guiGraphics.blitSprite(PAUSE, powerButton.left(), powerButton.top(), 12, 13)
        } else {
            guiGraphics.blitSprite(PLAY, powerButton.left(), powerButton.top(), powerButton.width, powerButton.height)
        }

        val barGraphs = ScreenRectangle(base.right() - 33, base.bottom() - 84, 13, 75)
        val tank = ScreenRectangle(base.right() - 50, base.bottom() - 84, 26, 75)

        // HIT
        var height = barGraphs.top()
        val hit = (menu.containerData[ACCURACY] / 100.0)
        val barData = ArrayList<Double>(4)
        val barRects = ArrayList<ScreenRectangle>(4)
        barData.add(1.0 - (menu.containerData[ACCURACY] / 100.0))

        //MISS
        guiGraphics.setColor(1f, 0.30f, 0.30f, 1f)
        barRects.add(ScreenRectangle(barGraphs.left() + barGraphs.width, height, barGraphs.width, barHeight(barGraphs.height * barData[0])))
        guiGraphics.blitSprite(azLoc("miner/bar"), barRects[0].left(), barRects[0].top(), barGraphs.width, barRects[0].height)
        height += barHeight(barGraphs.height * barData[0])

        // MATERIAL CHANCE
        guiGraphics.setColor(0.3f, 0.8f, 1f, 1f)
        barData.add(hit * (menu.containerData[MATERIAL_CHANCE] / 100.0))
        barRects.add(ScreenRectangle(barRects[0].left(), height, barGraphs.width, barHeight(barGraphs.height * barData[1])))
        guiGraphics.blitSprite(azLoc("miner/bar"), barRects[1].left(), barRects[1].top(), barGraphs.width, barRects[1].height)
        height += barHeight(barGraphs.height * barData[1])

        // RAW
        guiGraphics.setColor(0.9f, 0.3f, 0.9f, 1f)
        barData.add(hit * (menu.containerData[RAW_CHANCE] / 100.0))
        barRects.add(ScreenRectangle(barRects[0].left(), height, barGraphs.width, barHeight(barGraphs.height * barData[2])))
        guiGraphics.blitSprite(azLoc("miner/bar"), barRects[2].left(), barRects[2].top(), barGraphs.width, barRects[2].height)
        height += barHeight(barGraphs.height * barData[2])

        // ORES
        guiGraphics.setColor(0.3f, 1f, 0.3f, 1f)
        barData.add(hit - barData[1] - barData[2])
        barRects.add(ScreenRectangle(barRects[0].left(), height, barGraphs.width, barGraphs.bottom() - height))
        guiGraphics.blitSprite(azLoc("miner/bar"), barRects[3].left(), barRects[3].top(), barGraphs.width, barRects[3].height)

        guiGraphics.setColor(1f, 1f, 1f, 1f)

        // MOLTEN ORE
        val varMoltenLen = floor(menu.fluidHandler.getFluidAmount(0).toDouble() / MinerEntity.FLUID_SIZE.toDouble() * tank.height).toInt()
        guiGraphics.blitSprite(azLoc("miner/molten_ore_still"), tank.left(), tank.bottom() - varMoltenLen, tank.width, varMoltenLen)

        // TANK
        guiGraphics.blitSprite(TANK, tank.left() - 1, tank.top() - 1, 4, 45, tank.height + 2)

        if (energy.containsPoint(mouseX, mouseY)) {
            if (hasShiftDown())
                guiGraphics.renderTooltip(font, Component.literal(String.format("%,d FE", menu.energyStorage.energyStored)), mouseX, mouseY)
            else
                guiGraphics.renderTooltip(font, Component.literal(getFE(menu.energyStorage.energyStored.toDouble())), mouseX, mouseY)
        }
        if (powerButton.containsPoint(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.translatable("tooltip.azurum_miner.machine." + if (menu.containerData[IS_ON] == 1) "on" else "off"), mouseX, mouseY)
        }
        if (tank.containsPoint(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, componentSplit("tooltip.azurum_miner.miner.info.tank", listOf(Style.EMPTY, Style.EMPTY.withColor(0xAAAAAA)), DecimalFormat("#,###").format(menu.fluidHandler.getFluidAmount(0))), mouseX, mouseY)
        }
        for ((idx, bar) in barRects.withIndex()) {
            if (bar.containsPoint(mouseX, mouseY)) {
                guiGraphics.renderTooltip(font, Component.translatable("tooltip.azurum_miner.miner.chance${idx}", String.format("%.1f", barData[idx] * 100)), mouseX, mouseY)
            }
        }
    }

    var x = 0
    var y = 0

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        renderTooltip(guiGraphics, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && powerButton.containsPoint(mouseX.toInt(), mouseY.toInt())) {
            PacketDistributor.sendToServer(Payload(IS_ON, (menu.containerData[IS_ON]).xor(1), "OTHERS", "miner", menu.pos))
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val edit = this.focused
        if (tabManager.currentTab is FilterTab && edit is EditBox && edit.isActive && edit.isFocused) {
            if (keyCode == InputConstants.KEY_NUMPADENTER || keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_ESCAPE) {
                edit.isFocused = false
                return true
            }
            return edit.keyPressed(keyCode, scanCode, modifiers)
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true
        } else if (keyCode >= InputConstants.KEY_1 && keyCode <= InputConstants.KEY_3) {
            navigationBar.loadCurrentTab(keyCode - InputConstants.KEY_1)
            return true
        } else if ((keyCode == InputConstants.KEY_NUMPADENTER || keyCode == InputConstants.KEY_RETURN) && navigationBar.isFocused) {
            navigationBar.loadCurrentTab(navigationBar.tabButtons.indexOf(navigationBar.focused))
            return true
        }
        return false
    }

    override fun getTabOrderGroup(): Int {
        return -1
    }
}

fun barHeight(inHeight: Double): Int {
    if (inHeight == 0.0) return 0
    if (inHeight < 3.0) return 3
    return round(inHeight).toInt()
}