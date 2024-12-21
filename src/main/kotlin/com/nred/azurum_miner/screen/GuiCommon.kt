@file:Suppress("PrivatePropertyName")

package com.nred.azurum_miner.screen

import com.google.common.collect.ImmutableList
import com.nred.azurum_miner.AzurumMiner
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.components.TabButton
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.components.tabs.GridLayoutTab
import net.minecraft.client.gui.components.tabs.TabManager
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FastColor
import java.text.DecimalFormat
import kotlin.math.max

abstract class GuiCommon {
    companion object {
        fun getFE(powerNum: Number): String {
            val power = powerNum.toDouble()
            return when {
                (power / 1000000000.0) >= 0.999999 -> String.format("%.1f GFE", power / 1000000000.0)
                (power / 1000000.0) >= 0.999999 -> String.format("%.1f MFE", power / 1000000.0)
                (power / 1000.0) >= 0.999999 -> String.format("%.1f kFE", power / 1000.0)
                else -> String.format("%.1f FE", power)
            }
        }

        fun getBuckets(fluidNum: Number): String { //data[TOTAL_MODIFIER_POINTS]
            val fluid = fluidNum.toDouble()
            return when {
                fluid >= 10000 -> String.format("%s B", DecimalFormat("#,###.0").format(fluid / 1000.0))
                else -> String.format("%s mB", DecimalFormat("#,###").format(fluid.toInt()))
            }
        }

        fun getTime(ticks: Number): String {
            var time = max(ticks.toDouble() / 20.0, 0.0)
            val hours = time / 360.0
            time %= 360.0
            val mins = time / 60.0
            time %= 60.0

            var str = ""
            if (hours > 1) {
                str += String.format("%.1fh ", hours)
            }
            if (mins > 1) {
                str += String.format("%.1fm ", mins)
            }
            str += String.format("%.1fs", time)
            return str
        }

        fun listPlayerInventoryPos(offset: Int): ArrayList<IntArray> {
            val list = ArrayList<IntArray>()
            for (y in 0..2) {
                for (x in 0..8) {
                    list.add(intArrayOf(x + y * 9 + 9, 8 + x * 18, 83 + y * 18))
                }
            }

            return list
        }

        fun listPlayerHotbarPos(offset: Int): ArrayList<IntArray> {
            val list = ArrayList<IntArray>()
            for (x in 0..8) {
                list.add(intArrayOf(x, 8 + x * 18, 142))
            }

            return list
        }

        fun listPlayerInventoryHotbarPos(offset: Int): List<IntArray> {
            return (listPlayerInventoryPos(offset) + listPlayerHotbarPos(offset))
        }
    }

}

abstract class RenderTab(title: Component) : GridLayoutTab(title) {
    abstract fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int)
    abstract fun onSwap()
}

class VerticalTabNavigationBar(private val x: Int, private val y: Int, private val tabManager: TabManager, tabButtons: Iterable<SpriteTabButton>) : AbstractContainerEventHandler(), Renderable, NarratableEntry {
    private val tabs: ArrayList<RenderTab> = ArrayList(tabButtons.toList().size)
    private val tabButtons: ImmutableList<SpriteTabButton> = ImmutableList.copyOf(tabButtons)
    private val layout: LinearLayout = LinearLayout(x, y, LinearLayout.Orientation.VERTICAL)
    private var currentTab: SpriteTabButton = tabButtons.first()

    init {
        for ((idx, tabButton) in tabButtons.withIndex()) {
            this.layout.addChild(tabButton)
            tabs.add(tabButton.tab())
            tabButton.setNavBar(this)
            tabButton.setColor(idx)
        }

        layout.spacing(1)
        layout.arrangeElements()
        layout.x = x
        layout.y = y

        this.tabManager.setCurrentTab(tabButtons.first().tab(), false)
        tabButtons.first().isSelected = true
    }

    fun setTab(tabButton: SpriteTabButton, playClickSound: Boolean) {
        if (this.currentTab != tabButton) {
            this.currentTab.isSelected = false
            this.currentTab = tabButton
            tabButton.isSelected = true
            this.tabManager.setCurrentTab(tabButton.tab(), playClickSound)
            tabButton.tab().onSwap()
        }
    }

    fun loadCurrentTab(idx: Int) {
        setTab(this.tabButtons[idx], false)
    }

    fun saveCurrentTab(): Int {
        for ((idx, tabButton) in this.tabButtons.withIndex()) {
            if (tabButton == this.currentTab) {
                return idx
            }
        }
        return 0
    }

    override fun children(): MutableList<out GuiEventListener> {
        return tabButtons
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        for (tabButton in this.tabButtons) {
            tabButton.render(guiGraphics, mouseX, mouseY, partialTick)
            if (tabButton.getSelected()) {
                tabButton.tab().render(guiGraphics, mouseX, mouseY)
            }
        }
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
//        val optional = tabButtons
//            .stream()
//            .filter { obj: TabButton -> obj.isHovered }
//            .findFirst()
//            .or { Optional.ofNullable<TabButton?>(this.currentTabButton()) }
//        optional.ifPresent { p_274663_: TabButton ->
//            this.narrateListElementPosition(narrationElementOutput.nest(), p_274663_)
//            p_274663_.updateNarration(narrationElementOutput)
//        }
//        if (this.isFocused) {
//            narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.tab_navigation.usage"))
//        }
    }

    /**
     * Narrates the position of a list element (tab button).
     *
     * @param narrationElementOutput the narration output to update.
     * @param tabButton              the tab button whose position is being narrated.
     */
    private fun narrateListElementPosition(narrationElementOutput: NarrationElementOutput, tabButton: TabButton?) {
        if (tabs.size > 1) {
            val i = tabButtons.indexOf(tabButton)
            if (i != -1) {
                narrationElementOutput.add(NarratedElementType.POSITION, Component.translatable("narrator.position.tab", i + 1, tabs.size))
            }
        }
    }

    private fun currentTabIndex(): Int {
        val tab = tabManager.currentTab
        val i = tabs.indexOf(tab)
        return if (i != -1) i else -1
    }

    private fun currentTabButton(): TabButton? {
        val i: Int = this.currentTabIndex()
        return if (i != -1) tabButtons[i] else null
    }

    override fun narrationPriority(): NarrationPriority {
        return tabButtons.stream().map { obj: TabButton -> obj.narrationPriority() }.max(Comparator.naturalOrder()).orElse(NarrationPriority.NONE)
    }
}

@Suppress("PrivatePropertyName")
class SpriteTabButton(tabManager: TabManager, tab: RenderTab, width: Int, height: Int) : TabButton(tabManager, tab, width, height) {
    private val TAB = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner/tab")
    private var navigationBar: VerticalTabNavigationBar? = null
    private var selected: Boolean = false
    private var color: List<Float>

    private val colors = listOf(listOf(0.945f, 0f, 0f), listOf(0.165f, 0.682f, 0.702f))

    init {
        this.tooltip = Tooltip.create(tab.tabTitle)
        this.color = colors[0]
    }

    fun setNavBar(navigationBar: VerticalTabNavigationBar) {
        this.navigationBar = navigationBar
    }

    fun setColor(int: Int) {
        this.color = colors[int]
    }

    override fun tab(): RenderTab {
        return super.tab() as RenderTab
    }

    fun setSelected(selected: Boolean) {
        this.selected = selected
    }

    fun getSelected(): Boolean {
        return this.selected
    }

    override fun onClick(mouseX: Double, mouseY: Double, button: Int) {
        this.navigationBar?.setTab(this, true)

        super.onClick(mouseX, mouseY, button)
    }

    override fun getRectangle(): ScreenRectangle {
        return ScreenRectangle(this.x, this.y, this.width, this.height)
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        var x = this.x + 2
        var width = this.width - 2
        if (this.selected) {
            guiGraphics.setColor(color[0], color[1], color[2], 1f)
            x -= 2
            width += 3
        } else if (this.isHovered) {
            guiGraphics.setColor(max(0f, color[0] - 0.1f), max(0f, color[1] - 0.1f), max(0f, color[2] - 0.1f), 1f)
        } else if (!this.active) {
            guiGraphics.setColor(max(0f, color[0] - 0.5f), max(0f, color[1] - 0.5f), max(0f, color[2] - 0.5f), 1f)
        } else {
            guiGraphics.setColor(max(0f, color[0] - 0.27f), max(0f, color[1] - 0.27f), max(0f, color[2] - 0.27f), 1f)
        }

        guiGraphics.blitSprite(TAB, this.width, this.height, 0, 0, x, this.y, 2, width, this.height)

        guiGraphics.setColor(1f, 1f, 1f, 1f)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

}

fun colorToFloat(color: Int): FloatArray {
    return floatArrayOf(FastColor.ARGB32.red(color) / 255.0f, FastColor.ARGB32.green(color) / 255.0f, FastColor.ARGB32.blue(color) / 255.0f, FastColor.ARGB32.alpha(color) / 255.0f)
}

fun blitTile(guiGraphics: GuiGraphics, sprite: TextureAtlasSprite, x: Int, y: Int, width: Int, height: Int, tWidth: Int, tHeight: Int, color: Int = 0xFFFFFFFF.toInt()) {
    val c = colorToFloat(color)
    for (cWidth in generateSequence(0) { if (it < (width - tWidth)) it + tWidth else null }) {
        for (cHeight in generateSequence(0) { if (it < (height - tHeight)) it + tHeight else null }) {
            guiGraphics.blit(x + cWidth, y + cHeight, 0, if (tWidth + cWidth < width) tWidth else width - cWidth, if (tHeight + cHeight < height) tHeight else height - cHeight, sprite, c[0], c[1], c[2], c[3])
        }
    }
}