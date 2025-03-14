package com.nred.azurum_miner.machine.miner

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.datagen.ModItemTagProvider
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MappingInfo
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum.*
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerVariablesEnum.*
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.calculateEnergyModifier
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.get
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.getModifierVal
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.minerMapping
import com.nred.azurum_miner.screen.GuiCommon.Companion.getBuckets
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.GuiCommon.Companion.getTime
import com.nred.azurum_miner.screen.RenderTab
import com.nred.azurum_miner.util.FilterSetPayload
import com.nred.azurum_miner.util.Helpers.compC
import com.nred.azurum_miner.util.Helpers.compCat
import com.nred.azurum_miner.util.Helpers.componentSplit
import com.nred.azurum_miner.util.MinerFilterPayloadToServer
import com.nred.azurum_miner.util.Payload
import com.nred.azurum_miner.util.TRUE
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.util.CommonColors
import net.minecraft.util.Unit
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.network.PacketDistributor
import org.joml.Vector2i
import java.util.function.Consumer
import kotlin.math.ceil
import kotlin.math.min
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner.INSTANCE as TooltipPositioner

@OnlyIn(Dist.CLIENT)
class MainTab(val menu: MinerMenu) : RenderTab(TITLE) {
    val infoBoxLayout: GridLayout
    val pointsData = menu.pointsContainerData
    val data = menu.containerData
    val font: Font = Minecraft.getInstance().font

    companion object {
        private val TITLE: Component = Component.translatable("tab." + AzurumMiner.ID + ".miner.main.title")
        val POINTS_BUTTON: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/points_button")
        val POINTS_BUTTON_DISABLED: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/points_button_disabled")
        val POINTS_BUTTON_HOVER: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/points_button_hover")
        val POINTS_BUTTON_BARS: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/points_button_bars")

        val buttonSprites = WidgetSprites(POINTS_BUTTON, POINTS_BUTTON_DISABLED, POINTS_BUTTON_HOVER, POINTS_BUTTON_DISABLED)
    }

    init {
        val gridlayout = layout.rowSpacing(0)
        val helper = gridlayout.createRowHelper(6)

        infoBoxLayout = GridLayout()
        val infoBox = infoBoxLayout.createRowHelper(1)
        infoBox.addChild(InfoBox(0, 0, 70, 90, menu), LayoutSettings.defaults().alignHorizontallyLeft().paddingHorizontal(2))

        makePointButton(helper, menu)
    }

    override fun visitChildren(consumer: Consumer<AbstractWidget>) {
        super.visitChildren(consumer)
        infoBoxLayout.visitWidgets(consumer)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        var error: Component? = null
        if (data[ENERGY_NEEDED].toDouble() / data[TICKS_PER_OP].toDouble() > menu.energyStorage.energyStored) {
            error = Component.translatable("tooltip.azurum_miner.miner.error.no_power")
        } else if (data[IS_STOPPED] == 1) {
            error = Component.translatable("tooltip.azurum_miner.miner.error.no_space")
        }

        if (error != null) {
            for ((idx, line) in font.split(error, 150).withIndex()) {
                guiGraphics.drawString(font, line, this.infoBoxLayout.x - 138, this.infoBoxLayout.y + 76 + idx * 9, 0xFF0000, true)
            }
        }
    }

    override fun onSwap() {
        for (slot in this.menu.filterSlots) {
            slot.active = false
        }
    }

    override fun doLayout(rectangle: ScreenRectangle) {
        this.layout.arrangeElements()
        FrameLayout.alignInRectangle(this.layout, rectangle, 0.17f, 0.17f)

        this.infoBoxLayout.arrangeElements()
        FrameLayout.alignInRectangle(this.infoBoxLayout, rectangle, 0.980f, 0.05f)
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

                fun getVals(pointIdx: Int, levelIdx: Int, single: Boolean = false): List<Any> {
                    val map = minerMapping[pointIdx]!!
                    val name = map["name"].toString()
                    var info = (map["info"] as List<*>)[levelIdx - 1]

                    if (name == "efficiency" && levelIdx == 3) {
                        return listOf(getModifierVal("miner.modifiers.${name}.${levelIdx}", 0, true).second, "", "")
                    }

                    if (info !is MappingInfo) {
                        return listOf("", "", "")
                    }

                    val start = data[info.enum]
                    val newValue = getModifierVal("miner.modifiers.${name}.${levelIdx}", start, info.additive)

                    if (info.vals == 1 || single) {
                        return listOf(newValue.second, "", "")
                    } else if (info.vals == 2) {
                        return listOf(info.func.call(start), info.func.call(newValue.first), "")
                    }
                    return listOf(newValue.second, info.func.call(start), info.func.call(newValue.first))
                }

                fun getFEVal(pointIdx: Int, levelIdx: Int): String {
                    val map = minerMapping[pointIdx]!!
                    val name = map["name"].toString()
                    return if (name != "efficiency") getFE(calculateEnergyModifier(data[ENERGY_NEEDED], name, levelIdx, pointsData[3])) else ""
                }

                fun modifierToolTip(): Tooltip {
                    val points = pointsData[num]
                    if (points < 5) {
                        val vals = getVals(num, points + 1)
                        val lines = arrayOf(
                            compC("tooltip.azurum_miner.miner.button${num + 1}.upgrade${points + 1}", if (data[TOTAL_MODIFIER_POINTS] - data[USED_MODIFIER_POINTS] < 1) CommonColors.GRAY else CommonColors.GREEN, vals[0], vals[1], vals[2]), // Upside
                            if (num != 3) compC("tooltip.azurum_miner.miner.button.FEChange", CommonColors.SOFT_RED, getFE(data[ENERGY_NEEDED]), getFEVal(num, points + 1)) else Component.empty() // Power use
                        )

                        return Tooltip.create(compCat(compC("tooltip.azurum_miner.miner.button${num + 1}"), *lines))
                    }
                    return Tooltip.create(compCat(compC("tooltip.azurum_miner.miner.button${num + 1}")))
                }

                fun modifierExtendedToolTip(): Tooltip {
                    val lines = ArrayList<MutableComponent>()

                    for (points in 0..4) {
                        val vals = getVals(num, points + 1, true)
                        lines += compC("tooltip.azurum_miner.miner.button${num + 1}.upgrade${points + 1}.single", if (points % 2 == 0) CommonColors.GREEN else CommonColors.SOFT_YELLOW, vals[0], vals[1], vals[2])
                    }

                    return Tooltip.create(compCat(compC("tooltip.azurum_miner.miner.button${num + 1}"), *lines.toTypedArray()))
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
                            this.tooltip = modifierExtendedToolTip()
                        } else {
                            this.tooltip = modifierToolTip()
                        }
                    } else {
                        this.tooltip = Tooltip.create(Component.translatable("tooltip.azurum_miner.miner.button.disabled", Component.literal("${num + 1}").setStyle(Style.EMPTY.withBold(true)), Component.translatable("tooltip.azurum_miner.miner.button${num + 1}").withColor((0xFF888888).toInt())))
                    }
                }
            }
            gridlayout.addChild(imageBtn, LayoutSettings.defaults().alignHorizontallyLeft().paddingHorizontal(2))
        }
    }
}

class InfoBox(x: Int, y: Int, width: Int, height: Int, val menu: MinerMenu) : AbstractWidget(x, y, width, height, Component.empty()) {
    val data: ContainerData = menu.containerData
    val font: Font = Minecraft.getInstance().font

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        var rect = ScreenRectangle(this.x + 2, this.y + 2, 0, font.lineHeight)
        guiGraphics.renderOutline(this.x, this.y, 70, 107, (0xFF000000).toInt())
        val lines = listOf(
            getTime(data[CURRENT_NEEDED] - data[PROGRESS]),
            getFE(data[ENERGY_NEEDED].toDouble()),
            getFE(data[ENERGY_NEEDED].toDouble() / data[TICKS_PER_OP].toDouble()) + "/t",
            Component.translatable("tooltip.azurum_miner.miner.filter", if (data[HAS_FILTER] == TRUE) data[FILTER_CHANCE] else 0).string,
            Component.translatable("tooltip.azurum_miner.miner.higher", data[HIGHER_TIER_CHANCE]).string,
            Component.translatable("tooltip.azurum_miner.miner.multi", data[MULTI_CHANCE]).string,
            Component.translatable("tooltip.azurum_miner.miner.multiMinMax", data[MULTI_MIN], data[MULTI_MAX]).string,
            Component.translatable("tooltip.azurum_miner.miner.modifiers", (data[TOTAL_MODIFIER_POINTS] - data[USED_MODIFIER_POINTS])).string,
            getBuckets(data[FLUID_NEEDED])
        )

        for ((idx, line) in lines.withIndex()) {
            guiGraphics.drawString(font, line, rect.left(), (font.lineHeight + 3) * idx + rect.top() - 1, 0xFFFFFF)
        }

        for ((idx, line) in lines.withIndex()) {
            rect = ScreenRectangle(rect.left(), (font.lineHeight + 3) * idx + this.y + 1, font.width(line), rect.height)
            if (rect.containsPoint(mouseX, mouseY)) {
                guiGraphics.renderTooltip(font, componentSplit("tooltip.azurum_miner.miner.info$idx"), mouseX, mouseY)
            }
        }
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        // TODO
    }
}

@OnlyIn(Dist.CLIENT)
class OptionsTab(val menu: MinerMenu) : RenderTab(TITLE) {
    companion object {
        private val TITLE: Component = Component.translatable("tab." + AzurumMiner.ID + ".miner.options.title")
    }

    val editBoxes = ArrayList<FilterEditBox>()
    val filterBoxes = ArrayList<FilterBox>()

    init {
        val gridlayout = layout.rowSpacing(7).columnSpacing(6).createRowHelper(3)

        for (i in 0..2) {
            editBoxes += FilterEditBox(160, 18, i, menu)
            filterBoxes += FilterBox(i, menu.containerData, editBoxes.last())
            gridlayout.addChild(filterBoxes.last())
            gridlayout.addChild(editBoxes.last())
            filterBoxes += FilterBox(i, menu.containerData, editBoxes.last(), true)
            gridlayout.addChild(filterBoxes.last())
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
    }

    override fun onSwap() {
        for (slot in this.menu.filterSlots) {
            slot.active = true
        }

        for (edit in editBoxes) {
            edit.active = this.menu.containerData[NUM_FILTERS] > 2
            edit.setEditable(edit.active)
            if (!edit.active) {
                edit.tooltip = Tooltip.create(Component.translatable("tooltip.azurum_miner.miner.filters_required"))
            } else {
                edit.tooltip = Tooltip.create(Component.literal("Ex. c:ores/diamond"))
            }
        }

        for (box in filterBoxes) {
            if (box.ingredientSlot) {
                box.active = this.menu.containerData[NUM_FILTERS] > 2
            } else {
                box.active = this.menu.containerData[NUM_FILTERS] > box.idx
            }

            if (!box.active) {
                box.tooltip = Tooltip.create(Component.translatable("tooltip.azurum_miner.miner.filters_required"))
            } else {
                box.tooltip = null
            }
        }
    }

    override fun doLayout(rectangle: ScreenRectangle) {
        this.layout.arrangeElements()
        FrameLayout.alignInRectangle(this.layout, rectangle, 0.82f, 0.165f)
    }
}

@OnlyIn(Dist.CLIENT)
class FilterEditBox(width: Int, height: Int, val idx: Int, val menu: MinerMenu) : EditBox(Minecraft.getInstance().font, width, height, Component.literal(menu.filters[idx])) {
    val SPRITES = WidgetSprites(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner/text_field"), ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner/text_field_disabled"), ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner/text_field_highlighted"))
    var ingredients: Ingredient = Ingredient.EMPTY
    val foundTags = ArrayList<TagKey<Item>>()
    var init = false

    init {
        // Update to Server on change
        this.setResponder { PacketDistributor.sendToServer(MinerFilterPayloadToServer(this.idx, it, this.menu.pos)) }
        PacketDistributor.sendToServer(MinerFilterPayloadToServer(this.idx, this.value, this.menu.pos, true)) // Get saved data

        this.setTextColor(0xFFFFFFFF.toInt())
        this.setTextColorUneditable(0xFFBBBBBB.toInt())

        for (i in 0..this.menu.tier) {
            this.foundTags += ModItemTagProvider.oreTierTag[i]
        }
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        guiGraphics.blitSprite(this.SPRITES.get(this.isActive, this.isFocused), this.x, this.y, 0, this.getWidth(), this.getHeight())

        if (!init && this.value == "" && this.menu.filters[this.idx] != "") {
            this.value = this.menu.filters[this.idx]
            init = true
        }

        if (ResourceLocation.tryParse(this.value) != null) {
            this.ingredients = Ingredient.of(Ingredient.of(ItemTags.create(ResourceLocation.parse(this.value))).items.filter { it -> foundTags.any { tag -> it.`is`(tag) } }.stream())
        } else {
            this.ingredients = Ingredient.EMPTY
        }

        if (this.value.isEmpty()) {
            guiGraphics.drawString(Minecraft.getInstance().font, "Set filter with Tag", this.x + 4, this.y + (this.height - 8) / 2, 0xFFBBBBBB.toInt(), this.textShadow)
        }

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun getInnerWidth(): Int {
        return this.width - 8
    }

    override fun isBordered(): Boolean {
        return false
    }
}

class FilterBox(val idx: Int, val data: ContainerData, val editBox: FilterEditBox, val ingredientSlot: Boolean = false) : AbstractWidget(0, 0, 18, 18, Component.empty()) {
    val SLOT: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/slot")
    var index = 0
    var frame = 0
    var dontUse = false
    val minecraft: Minecraft = Minecraft.getInstance()

    init {
        editBox.menu.filterSlots[idx].active = false
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        guiGraphics.blitSprite(SLOT, this.x, this.y, 18, 18)

        if (!this.active || (this.dontUse && this.editBox.active)) {
            guiGraphics.fill(this.x + 1, this.y + 1, this.x + 17, this.y + 17, 100, 0x70323232.toInt())
        }

        if (ingredientSlot) {
            if (frame < 100) {
                frame++
            } else {
                frame = 0
                if (index < editBox.ingredients.items.size - 1) {
                    index++
                } else {
                    index = 0
                }
            }
            guiGraphics.renderItem(editBox.ingredients.items.getOrElse(this.index) { ItemStack(Items.BARRIER, 1) }, this.x + 1, this.y + 1)

            if (ScreenRectangle(this.x, this.y, 16, 16).containsPoint(mouseX, mouseY) && this.tooltip == null) {
                if (this.editBox.ingredients.hasNoItems()) {
                    if (!editBox.value.isEmpty()) {
                        guiGraphics.renderTooltip(minecraft.font, Component.translatable("tooltip.azurum_miner.miner.filters_no_tag_for", editBox.value), mouseX, mouseY)
                    } else {
                        guiGraphics.renderTooltip(minecraft.font, Component.translatable("tooltip.azurum_miner.miner.filters_no_tag"), mouseX, mouseY)
                    }
                } else {
                    // Max size is 8 x 8 (64 items)
                    val size = Vector2i((if (editBox.ingredients.items.size < 8) editBox.ingredients.items.size else 8) * 16, min(ceil(editBox.ingredients.items.size / 8.0).toInt(), 8) * 16)

                    val screen = minecraft.screen!!
                    val position = TooltipPositioner.positionTooltip(screen.width, screen.height, mouseX, mouseY, size.x, size.y)
                    TooltipRenderUtil.renderTooltipBackground(guiGraphics, position.x(), position.y(), size.x, size.y, 9000)

                    guiGraphics.pose().pushPose()
                    guiGraphics.pose().translate(0f, 0f, 9000f)
                    for ((idx, item) in editBox.ingredients.items.withIndex()) {
                        guiGraphics.renderItem(item, position.x() + (16 * (idx % 8)), position.y() + (16 * (idx / 8)))
                    }
                    guiGraphics.pose().popPose()
                }
            }
        } else {
            this.dontUse = !this.editBox.ingredients.hasNoItems() && this.editBox.menu.containerData[NUM_FILTERS] > 2
            if (this.active) {
                if (this.dontUse) {
                    this.tooltip = Tooltip.create(Component.translatable("tooltip.azurum_miner.miner.filters_has_tag", editBox.value))
                    this.editBox.menu.filterSlots[idx].dontUse = true
                    this.editBox.menu.filterSlots[idx].item.set(DataComponents.HIDE_TOOLTIP, Unit.INSTANCE)
                } else {
                    this.tooltip = null
                }
            } else {
                this.editBox.menu.filterSlots[idx].dontUse = false
                this.editBox.menu.filterSlots[idx].item.remove(DataComponents.HIDE_TOOLTIP)
            }
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (this.isMouseOver(mouseX, mouseY) && this.editBox.menu.filterSlots[idx].active) {
            if (!this.editBox.menu.carried.isEmpty) {
                if (this.editBox.menu.filterSlots[idx].mayPlace(this.editBox.menu.carried)) {
                    this.editBox.menu.filterSlots[this.idx].set(this.editBox.menu.carried.copy())
                    this.minecraft.player!!.connection.send(FilterSetPayload(this.editBox.menu.carried.copy(), this.idx))
                }
            } else {
                this.editBox.menu.filterSlots[this.idx].set(ItemStack.EMPTY)
                this.minecraft.player!!.connection.send(FilterSetPayload(ItemStack.EMPTY, this.idx))
            }
            return true
        }
        return false
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        // TODO
    }
}