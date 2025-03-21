package com.nred.azurum_miner.machine

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.infuser.InfuserEntity
import com.nred.azurum_miner.machine.infuser.InfuserEntity.Companion.get
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.blitTile
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.InventoryMenu
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.floor

open class MachineScreen<T : MachineMenu>(menu: T, playerInventory: Inventory, title: Component) : AbstractContainerScreen<T>(menu, playerInventory, title) {
    override fun init() {
        super.init()

        resize(width, imageWidth, height, imageHeight)
        font.width(this.title.string)
        this.titleLabelX = base.width / 2 - font.width(this.title.string) / 2 - 5
    }

    companion object {
        val PLAY: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/play")
        val PAUSE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/pause")
        val TANK: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/fluid_tank")
        val ENERGY_INNER: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/energy_inner")
        val ARROW_FILLED: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "textures/gui/sprites/common/arrow_filled.png")
        val FLAME_FILLED: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "common/flame_filled")
    }

    var base: ScreenRectangle = ScreenRectangle.empty()
    var energy: ScreenRectangle = ScreenRectangle.empty()
    var powerButton: ScreenRectangle = ScreenRectangle.empty()
    var tank: ScreenRectangle = ScreenRectangle.empty()
    var progressBar: ScreenRectangle = ScreenRectangle.empty()

    private var x = 0
    private var y = 0

    override fun resize(minecraft: Minecraft, width: Int, height: Int) {
        super.resize(minecraft, width, height)
        resize(this.width, imageWidth, this.height, imageHeight)
    }

    open fun resize(width: Int, imageWidth: Int, height: Int, imageHeight: Int) {
        x = (width - imageWidth) / 2
        y = (height - imageHeight) / 2
        base = ScreenRectangle(x, y, imageWidth, imageHeight)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        // Draw Energy
        val varLen = ceil(menu.energyStorage!!.energyStored.toDouble() / menu.energyStorage!!.maxEnergyStored.toDouble() * (energy.height.toDouble())).toInt()
        guiGraphics.blitSprite(ENERGY_INNER, energy.left(), energy.bottom() - varLen, 4, energy.width, varLen)

        // TANK
        if (menu.fluidHandler != null) {
            if (!menu.fluidHandler!!.getFluidInTank(0).isEmpty) {
                val varMoltenLen = floor(menu.fluidHandler!!.getFluidAmount(0).toDouble() / InfuserEntity.FLUID_SIZE.toDouble() * (tank.height - 2)).toInt()
                blitTile(guiGraphics, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(menu.fluidHandler!!.getFluidInTank(0).fluid).stillTexture), tank.left(), tank.bottom() - varMoltenLen - 2, tank.width - 2, varMoltenLen, 16, 16, IClientFluidTypeExtensions.of(menu.fluidHandler!!.getFluidInTank(0).fluid).tintColor)
            }

            guiGraphics.blitSprite(TANK, tank.left() - 1, tank.top() - 1, 4, tank.width, tank.height)
        }

        if (energy.containsPoint(mouseX, mouseY)) {
            if (hasShiftDown())
                guiGraphics.renderTooltip(font, Component.literal(String.format("%,d FE", menu.energyStorage!!.energyStored)), mouseX, mouseY)
            else
                guiGraphics.renderTooltip(font, Component.literal(getFE(menu.energyStorage!!.energyStored.toDouble())), mouseX, mouseY)
        }
        if (powerButton.containsPoint(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.translatable("tooltip.azurum_miner.machine." + if (menu.containerData[MinerEnum.IS_ON] == 1) "on" else "off"), mouseX, mouseY)
        }
        if (tank.containsPoint(mouseX, mouseY)) {
            val list = mutableListOf(Component.translatable("tooltip.azurum_miner.machine.tank", menu.fluidHandler!!.getFluidInTank(0).fluidType.description, DecimalFormat("#,###").format(menu.fluidHandler!!.getFluidAmount(0))))

            if (!menu.fluidHandler!!.canOutput(0)) {
                list += Component.translatable("tooltip.azurum_miner.shift_clear")
            }

            guiGraphics.renderComponentTooltip(font, list.toList(), mouseX, mouseY)
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}