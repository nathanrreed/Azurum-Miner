package com.nred.azurum_miner.machine.miner

import com.mojang.blaze3d.platform.InputConstants
import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum.*
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerVariablesEnum.*
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.get
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.listPlayerInventoryHotbarPos
import com.nred.azurum_miner.screen.SpriteTabButton
import com.nred.azurum_miner.screen.VerticalTabNavigationBar
import com.nred.azurum_miner.util.Helpers.componentSplit
import com.nred.azurum_miner.util.Payload
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.components.tabs.TabManager
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.network.PacketDistributor
import java.text.DecimalFormat
import java.util.Optional
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

class MinerScreen(menu: MinerMenu, playerInventory: Inventory, title: Component) : AbstractContainerScreen<MinerMenu>(menu, playerInventory, title) {
    companion object {
        val BASE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/base")
        val SLOT: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/slot")
        val PLAY: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/play")
        val PAUSE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/pause")
        val ENERGY_BAR: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/energy_bar")
        val ENERGY_INNER: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/energy_inner")
        val TANK: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner/fluid_tank")
    }

    var base: ScreenRectangle = ScreenRectangle.empty()
    var powerButton: ScreenRectangle = ScreenRectangle.empty()
    var energy: ScreenRectangle = ScreenRectangle.empty()
    val tabManager = TabManager({ widget -> this.addRenderableWidget(widget) }, { widget -> this.removeWidget(widget) })
    lateinit var navigationBar: VerticalTabNavigationBar

    private fun resize() {
        x = (width - imageWidth) / 2 - 51
        y = (height - imageHeight) / 2 - 32
        base = ScreenRectangle(x - 10, y, imageWidth + 57, imageHeight + 30)
        powerButton = ScreenRectangle(base.left() + 4, base.bottom() - 94, 12, 13)
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
        this.titleLabelX = -40
        this.titleLabelY = -26
        this.inventoryLabelX = -42

        tabManager.setTabArea(base)
        val mainTab = MainTab(menu)
        val optionsTab = OptionsTab(menu)

        val btn = SpriteTabButton(tabManager, mainTab, 12, 18)
        val btn2 = SpriteTabButton(tabManager, optionsTab, 12, 18)

        this.navigationBar = VerticalTabNavigationBar(x - 21, y + 6, tabManager, listOf(btn, btn2))

        this.addRenderableWidget(navigationBar)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blitSprite(BASE, base.left(), base.top(), 0, base.width, base.height)

        // Draw Inventory
        for (slotInfo in listPlayerInventoryHotbarPos()) {
            guiGraphics.blitSprite(SLOT, slotInfo[1] + x + 50, slotInfo[2] + y + 31, 18, 18)
        }

        // Draw Energy
        val varLen = ceil(menu.containerData[ENERGY_LEVEL].toDouble() / menu.containerData[ENERGY_CAPACITY].toDouble() * 82.0).toInt()

        guiGraphics.blitSprite(ENERGY_BAR, energy.left(), energy.top(), 3, energy.width, energy.height)
        guiGraphics.blitSprite(ENERGY_INNER, energy.left() + 1, energy.bottom() - 1 - varLen, 4, energy.width - 2, varLen)

        // Draw Power Button
        if (menu.containerData[IS_ON] == 1) {
            guiGraphics.blitSprite(PAUSE, powerButton.left(), powerButton.top(), 12, 13)
        } else {
            guiGraphics.blitSprite(PLAY, powerButton.left(), powerButton.top(), powerButton.width, powerButton.height)
        }

        val barGraphs = ScreenRectangle(base.right() - 32, base.bottom() - 81, 13, 75)
        val tank = ScreenRectangle(base.right() - 49, base.bottom() - 81, 26, 75)

        // HIT
        var height = barGraphs.top()
        val hit = (menu.containerData[ACCURACY] / 100.0)
        val barData = ArrayList<Double>(4)
        val barRects = ArrayList<ScreenRectangle>(4)
        barData.add(1.0 - (menu.containerData[ACCURACY] / 100.0))

        //MISS
        guiGraphics.setColor(1f, 0.30f, 0.30f, 1f)
        barRects.add(ScreenRectangle(barGraphs.left() + barGraphs.width, height, barGraphs.width, barHeight(barGraphs.height * barData[0])))
        guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner/bar"), barRects[0].left(), barRects[0].top(), barGraphs.width, barRects[0].height)
        height += barHeight(barGraphs.height * barData[0])

        // MATERIAL CHANCE
        guiGraphics.setColor(0.3f, 0.8f, 1f, 1f)
        barData.add(hit * (menu.containerData[MATERIAL_CHANCE] / 100.0))
        barRects.add(ScreenRectangle(barRects[0].left(), height, barGraphs.width, barHeight(barGraphs.height * barData[1])))
        guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner/bar"), barRects[1].left(), barRects[1].top(), barGraphs.width, barRects[1].height)
        height += barHeight(barGraphs.height * barData[1])

        // RAW
        guiGraphics.setColor(0.9f, 0.3f, 0.9f, 1f)
        barData.add(hit * (menu.containerData[RAW_CHANCE] / 100.0))
        barRects.add(ScreenRectangle(barRects[0].left(), height, barGraphs.width, barHeight(barGraphs.height * barData[2])))
        guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner/bar"), barRects[2].left(), barRects[2].top(), barGraphs.width, barRects[2].height)
        height += barHeight(barGraphs.height * barData[2])

        // ORES
        guiGraphics.setColor(0.3f, 1f, 0.3f, 1f)
        barData.add(hit - barData[1] - barData[2])
        barRects.add(ScreenRectangle(barRects[0].left(), height, barGraphs.width, barGraphs.bottom() - height))
        guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner/bar"), barRects[3].left(), barRects[3].top(), barGraphs.width, barRects[3].height)

        guiGraphics.setColor(1f, 1f, 1f, 1f)

        // MOLTEN ORE
        val varMoltenLen = floor(menu.containerData[MOLTEN_ORE_LEVEL].toDouble() / MinerEntity.FLUID_SIZE.toDouble() * tank.height).toInt()
        guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner/molten_ore_still"), tank.left(), tank.bottom() - varMoltenLen, tank.width, varMoltenLen)

        // TANK
        guiGraphics.blitSprite(TANK, tank.left() - 1, tank.top() - 1, 4, 45, tank.height + 2)

        if (energy.containsPoint(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.literal(getFE(menu.containerData[ENERGY_LEVEL].toDouble())), mouseX, mouseY)
        }
        if (powerButton.containsPoint(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.translatable("tooltip.azurum_miner.machine." + if (menu.containerData[IS_ON] == 1) "on" else "off"), mouseX, mouseY)
        }
        if (tank.containsPoint(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, componentSplit("tooltip.azurum_miner.miner.info.tank", listOf(Style.EMPTY, Style.EMPTY.withColor(0xAAAAAA)), DecimalFormat("#,###").format(menu.containerData[MOLTEN_ORE_LEVEL])), mouseX, mouseY)
        }
        for ((idx, bar) in barRects.withIndex()) {
            if (bar.containsPoint(mouseX, mouseY)) {
                guiGraphics.renderTooltip(font, Component.translatable("tooltip.azurum_miner.miner.chance${idx}", String.format("%.1f", barData[idx] * 100)), mouseX, mouseY)
            }
        }
    }

    private var x = 0
    private var y = 0

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
        if (tabManager.currentTab is OptionsTab && edit is EditBox) {
            if (edit.isActive && edit.isFocused) {
                if (keyCode == InputConstants.KEY_NUMPADENTER || keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_ESCAPE) {
                    edit.isFocused = false
                    return true
                }
                return edit.keyPressed(keyCode, scanCode, modifiers)
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun getChildAt(mouseX: Double, mouseY: Double): Optional<GuiEventListener?> {
        return super.getChildAt(mouseX, mouseY)
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