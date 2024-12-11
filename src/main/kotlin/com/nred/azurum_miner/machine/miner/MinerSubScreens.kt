package com.nred.azurum_miner.machine.miner

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum.*
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerVariablesEnum.*
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.get
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
import com.nred.azurum_miner.screen.RenderTab
import com.nred.azurum_miner.util.Helpers.compC
import com.nred.azurum_miner.util.Helpers.compCat
import com.nred.azurum_miner.util.Payload
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.CommonColors
import net.minecraft.world.inventory.ContainerData
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.network.PacketDistributor
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KCallable

@OnlyIn(Dist.CLIENT)
class MainTab(val menu: MinerMenu) : RenderTab(TITLE) {
    val infoBoxLayout: GridLayout
    val pointsData = menu.pointsContainerData
    val data = menu.containerData
    val font = Minecraft.getInstance().font

    companion object {
        private val TITLE: Component = Component.translatable("tab." + AzurumMiner.ID + ".miner.main.title")
        val POINTS_BUTTON = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/points_button")
        val POINTS_BUTTON_DISABLED = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/points_button_disabled")
        val POINTS_BUTTON_HOVER = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/points_button_hover")
        val POINTS_BUTTON_BARS = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/points_button_bars")

        val buttonSprites = WidgetSprites(POINTS_BUTTON, POINTS_BUTTON_DISABLED, POINTS_BUTTON_HOVER, POINTS_BUTTON_DISABLED)
    }

    init {
        val gridlayout = layout.rowSpacing(0)
        val helper = gridlayout.createRowHelper(6)

        infoBoxLayout = GridLayout()
        val infoBox = infoBoxLayout.createRowHelper(1)
        infoBox.addChild(InfoBox(0, 0, 70, 90, menu.containerData), LayoutSettings.defaults().alignHorizontallyLeft().paddingHorizontal(2))

        makePointButton(helper, menu)
    }

    override fun visitChildren(consumer: Consumer<AbstractWidget>) {
        super.visitChildren(consumer)
        infoBoxLayout.visitWidgets(consumer)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        var str = ""
        if (data[ENERGY_NEEDED].toDouble() / data[TICKS_PER_OP].toDouble() > data[ENERGY_LEVEL]) {
            str += "Error: Not enough FE"
        } else if (data[IS_STOPPED] == 1) {
            str += "Error: No space for item"
        }
        guiGraphics.drawString(font, str, this.infoBoxLayout.x - 138, this.infoBoxLayout.y + 80, 0xFF0000)
    }

    override fun doLayout(rectangle: ScreenRectangle) {
        this.layout.arrangeElements()
        FrameLayout.alignInRectangle(this.layout, rectangle, 0.17f, 0.17f)

        this.infoBoxLayout.arrangeElements()
        FrameLayout.alignInRectangle(this.infoBoxLayout, rectangle, 0.985f, 0.05f)
    }

    @OnlyIn(Dist.CLIENT)
    fun makePointButton(gridlayout: GridLayout.RowHelper, menu: MinerMenu) {

        for (i in 0..<pointsData.count) {
            val imageBtn = object : ImageButton(0, 0, 24, 54, buttonSprites, { }) {
                var init = false
                val num: Int

                init {
                    this.active = false
                    this.num = i
                }

                override fun onClick(mouseX: Double, mouseY: Double, button: Int) {
                    if (button == 0 && pointsData[i] < 5) {
                        PacketDistributor.sendToServer(Payload(i, pointsData[i] + 1, "POINTS", "miner", menu.pos))
                    } else if (button == 1 && pointsData[i] > 0) {
                        PacketDistributor.sendToServer(Payload(i, pointsData[i] - 1, "POINTS", "miner", menu.pos))
                    }
                }

                override fun isValidClickButton(button: Int): Boolean {
                    return button == 0 || button == 1
                }

                val mapping = mapOf(
                    0 to mapOf("name" to "speed", "info" to listOf(Triple(TICKS_PER_OP, false, ::getTime), Triple(MISS_TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime))),
                    1 to mapOf("name" to "filter", "info" to listOf(Triple(TICKS_PER_OP, false, ::getTime), Triple(MISS_TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime))),
                    2 to mapOf("name" to "accuracy", "info" to listOf(Triple(TICKS_PER_OP, false, ::getTime), Triple(MISS_TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime))),
                    3 to mapOf("name" to "efficiency", "info" to listOf(Triple(TICKS_PER_OP, false, ::getTime), Triple(MISS_TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime))),
                    4 to mapOf("name" to "production", "info" to listOf(Triple(TICKS_PER_OP, false, ::getTime), Triple(MISS_TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime), Triple(TICKS_PER_OP, false, ::getTime)))
                )

                // "additive" to listOf(false, false, false, false, false), "values" to listOf(TICKS_PER_OP, MISS_TICKS_PER_OP, TICKS_PER_OP, TICKS_PER_OP, TICKS_PER_OP)))

                fun getVal(path: String, start: Int, additive: Boolean): Pair<Number, String> {
                    val value = CONFIG.getOptional<Any>(path).getOrNull()
                    if (value is String) {
                        if (value.endsWith("%")) {
                            val percent = value.substringBefore('%').toDouble() / 100.0
                            return Pair(start.toDouble() * if (additive) (1.0 + percent) else (1.0 - percent), value)
                        } else if (value.endsWith("x")) {
                            return Pair(start.toDouble() * value.substringBefore('x').toDouble(), value)
                        } else {
                            return Pair(start.toDouble() - value.toDouble(), value)
                        }
                    } else if (value is Int) {
                        return Pair(if (additive) start + value else start - value, value.toString())
                    } else {
                        return Pair(0, "")
                    }
                }

                fun getVals(pointIdx: Int, levelIdx: Int): List<Any> {
                    val map = mapping[pointIdx]
                    val name = map?.get("name")
                    val info = (map?.get("info") as List<Triple<Enum<*>, Boolean, KCallable<String>>>)[levelIdx - 1]

                    val start = data[info.first]
                    val newValue = getVal("miner.modifiers.${name}.$levelIdx", start, info.second)
                    val FE = getFE(getVal("miner.modifiers.${name}.${levelIdx}FE", data[ENERGY_NEEDED], true).first)
                    return listOf(info.third.call(newValue.first), FE, newValue.second, info.third.call(start))
                }

                fun modifierToolTip(): Tooltip {
                    val points = pointsData[num]
                    if (points < 5) {
                        val vals = getVals(num, points + 1)
                        val lines = arrayOf(
                            compC("tooltip.azurum_miner.miner.button${num + 1}.upgrade${points + 1}", if (data[TOTAL_MODIFIER_POINTS] - data[USED_MODIFIER_POINTS] < 1) CommonColors.GRAY else CommonColors.GREEN, vals[2], vals[3], vals[0]), // Upside
                            compC("tooltip.azurum_miner.miner.button.FEChange", CommonColors.SOFT_RED, getFE(data[ENERGY_NEEDED]), vals[1]) // Power use
                        )

                        return Tooltip.create(compCat(compC("tooltip.azurum_miner.miner.button${num + 1}"), *lines))
                    }
                    return Tooltip.create(compCat(compC("tooltip.azurum_miner.miner.button${num + 1}")))
                }

                override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
                    if (!this.init) {
                        val numModifierSlots = data[NUM_MODIFIER_SLOTS]
                        if (numModifierSlots != 0) {
                            this.init = true
                            if (numModifierSlots > this.num) {
                                this.active = true

                            }
                        }
                    }

                    super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)

                    val varHeight = when (pointsData[i]) {
                        0 -> 0
                        1 -> 12
                        2 -> 18
                        3 -> 24
                        4 -> 30
                        5 -> this.height
                        else -> 0
                    }
                    guiGraphics.blitSprite(POINTS_BUTTON_BARS, this.width, this.height, 0, 0, this.x, this.y + 30 - pointsData[i] * 6, 2, this.width, varHeight)

                    if (this.active) {
                        if (Screen.hasShiftDown()) {
                            this.tooltip = Tooltip.create(Component.translatable("tooltip.azurum_miner.miner.button${num + 1}.extended"))
                        } else {
                            this.tooltip = modifierToolTip()// Component.translatable("tooltip.azurum_miner.miner.button${num + 1}"))
                        }
                    } else {
                        this.tooltip = Tooltip.create(Component.translatable("tooltip.azurum_miner.miner.button.disabled", Component.literal("${num + 1}").setStyle(Style.EMPTY.withBold(true)), Component.translatable("tooltip.azurum_miner.miner.button.${num + 1}").withColor((0xFF888888).toInt())))
                    }
                }
            }

            gridlayout.addChild(imageBtn, LayoutSettings.defaults().alignHorizontallyLeft().paddingHorizontal(2))
        }
    }
}

class InfoBox(x: Int, y: Int, width: Int, height: Int, data: ContainerData) : AbstractWidget(x, y, width, height, Component.empty()) {
    val data: ContainerData
    val font: Font = Minecraft.getInstance().font

    init {
        this.data = data
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        var rect = ScreenRectangle(this.x + 2, this.y + 2, 0, font.lineHeight)
        guiGraphics.renderOutline(this.x, this.y, 70, 92, (0xFF000000).toInt())
        val lines = listOf(
            getTime(data[TICKS_PER_OP] - data[PROGRESS]), getFE(data[ENERGY_NEEDED].toDouble() / data[TICKS_PER_OP].toDouble()) + "/t",
            "Modifiers: " + (data[TOTAL_MODIFIER_POINTS] - data[USED_MODIFIER_POINTS]),
        )
//        val lines = listOf("Acc: ${data.get(MinerEntity.Companion.MinerEnum.ACCURACY.ordinal)}%", "Raw: ${data.get(MinerEntity.Companion.MinerEnum.RAW_CHANCE.ordinal)}%")

        for ((idx, line) in lines.withIndex()) {
            guiGraphics.drawString(font, line, rect.left(), (font.lineHeight + 1) * idx + rect.top(), 0xFFFFFF)
        }

        for ((idx, line) in lines.withIndex()) {
            rect = ScreenRectangle(rect.left(), (font.lineHeight + 3) * idx + this.y + 2, font.width(line), rect.height)
            if (rect.containsPoint(mouseX, mouseY)) {
                guiGraphics.renderTooltip(font, Component.translatable("tooltip.azurum_miner.miner.info$idx"), mouseX, mouseY)
            }

        }
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
//        TODO("Not yet implemented")
    }
}

@OnlyIn(Dist.CLIENT)
class OptionsTab(val menu: MinerMenu) : RenderTab(TITLE) {
    companion object {
        private val TITLE: Component = Component.translatable("tab." + AzurumMiner.ID + ".miner.options.title")
    }

    init {
        val gridlayout = layout.rowSpacing(8).createRowHelper(3)

        for (i in 0..2) {
            gridlayout.addChild(FilterBox(menu.containerData))
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
    }

}

class FilterBox(data: ContainerData) : AbstractWidget(0, 0, 50, 100, Component.empty()) {
    val data: ContainerData

    init {
        this.data = data
    }

    val SLOT = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/slot")
    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        guiGraphics.blitSprite(SLOT, this.x + 13, this.y + 13, 18, 18)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
//        TODO("Not yet implemented")
    }
}