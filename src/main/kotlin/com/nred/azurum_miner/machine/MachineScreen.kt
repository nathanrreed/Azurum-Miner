package com.nred.azurum_miner.machine

import com.nred.azurum_miner.machine.infuser.InfuserEntity
import com.nred.azurum_miner.machine.infuser.InfuserEntity.Companion.get
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.screen.blitTile
import com.nred.azurum_miner.util.ClearPayload
import com.nred.azurum_miner.util.Helpers.azLoc
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.InventoryMenu
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.network.PacketDistributor
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
        val PLAY: ResourceLocation = azLoc("common/play")
        val PAUSE: ResourceLocation = azLoc("common/pause")
        val TANK: ResourceLocation = azLoc("common/fluid_tank")
        val ENERGY_INNER: ResourceLocation = azLoc("common/energy_inner")
        val ARROW_FILLED: ResourceLocation = azLoc("textures/gui/sprites/common/arrow_filled.png")
        val FLAME_FILLED: ResourceLocation = azLoc("common/flame_filled")
    }

    var base: ScreenRectangle = ScreenRectangle.empty()
    var energy: ScreenRectangle = ScreenRectangle.empty()
    var powerButton: ScreenRectangle = ScreenRectangle.empty()
    var tanks: List<ScreenRectangle> = listOf()
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

        // TANKS
        for (tankId: Int in 0..<tanks.size) {
            val tank = tanks[tankId]
            if (menu.fluidHandler != null) {
                if (!menu.fluidHandler!!.getFluidInTank(tankId).isEmpty) {
                    val varMoltenLen = floor(menu.fluidHandler!!.getFluidAmount(tankId).toDouble() / InfuserEntity.FLUID_SIZE.toDouble() * (tank.height - 2)).toInt()
                    blitTile(guiGraphics, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(menu.fluidHandler!!.getFluidInTank(tankId).fluid).stillTexture), tank.left(), tank.bottom() - varMoltenLen - if (tank.width > 10) 2 else 1, tank.width - 2, varMoltenLen, 16, 16, IClientFluidTypeExtensions.of(menu.fluidHandler!!.getFluidInTank(tankId).fluid).tintColor)
                }

                if (tank.width > 10)
                    guiGraphics.blitSprite(TANK, tank.left() - 1, tank.top() - 1, 4, tank.width, tank.height)
            }
            if (tank.containsPoint(mouseX, mouseY)) {
                val list = mutableListOf(Component.translatable("tooltip.azurum_miner.machine.tank", menu.fluidHandler!!.getFluidInTank(tankId).fluidType.description, DecimalFormat("#,###").format(menu.fluidHandler!!.getFluidAmount(tankId))))
                list += Component.translatable("tooltip.azurum_miner.shift_clear")

                guiGraphics.renderComponentTooltip(font, list.toList(), mouseX, mouseY)
            }
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
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (hasShiftDown()) {
            for (tankId: Int in 0..<tanks.size) {
                if (button == 0 && tanks[tankId].containsPoint(mouseX.toInt(), mouseY.toInt())) {
                    menu.fluidHandler!!.internalExtractFluid(menu.fluidHandler!!.getFluidInTank(tankId), IFluidHandler.FluidAction.EXECUTE)
                    PacketDistributor.sendToServer(ClearPayload(tankId, menu.pos))
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}