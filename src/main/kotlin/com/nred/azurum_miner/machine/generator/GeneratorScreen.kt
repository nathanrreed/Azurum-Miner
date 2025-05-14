package com.nred.azurum_miner.machine.generator

import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.machine.MachineScreen
import com.nred.azurum_miner.machine.generator.GeneratorEntity.Companion.GeneratorEnum.*
import com.nred.azurum_miner.machine.generator.GeneratorEntity.Companion.get
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.util.ClearPayload
import com.nred.azurum_miner.util.FALSE
import com.nred.azurum_miner.util.Helpers
import com.nred.azurum_miner.util.Helpers.azLoc
import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenAxis
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.CommonColors
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.network.PacketDistributor

class GeneratorScreen(menu: GeneratorMenu, playerInventory: Inventory, title: Component) : MachineScreen<GeneratorMenu>(menu, playerInventory, title) {
    companion object {
        val BASE: ResourceLocation = azLoc("generator")
        val LOCK: ResourceLocation = ResourceLocation.withDefaultNamespace("container/cartography_table/locked")
        val EMPTY_INGOT = Minecraft.getInstance().getTextureAtlas(ResourceLocation.withDefaultNamespace("textures/atlas/blocks.png")).apply(ResourceLocation.withDefaultNamespace("item/empty_slot_ingot"))
    }

    var details: ScreenRectangle = ScreenRectangle.empty()
    var fuelRect: ScreenRectangle = ScreenRectangle.empty()
    var baseRect: ScreenRectangle = ScreenRectangle.empty()

    override fun resize(width: Int, imageWidth: Int, height: Int, imageHeight: Int) {
        super.resize(width, imageWidth, height, imageHeight)
        energy = ScreenRectangle(base.right() - 11, base.top() + 6, 4, 67)
        details = ScreenRectangle(base.getCenterInAxis(ScreenAxis.HORIZONTAL) - 40, base.top() + 19, 80, 45)
        fuelRect = ScreenRectangle(base.left() + base.width / 6 - 8, base.top() + 20, 16, 16)
        baseRect = ScreenRectangle(base.left() + base.width / 6 - 8, base.top() + 47, 16, 16)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blitSprite(BASE, base.left(), base.top(), 0, base.width, base.height)

        val hasBase = menu.containerData[HAS_BASE]
        val hasFuel = menu.containerData[HAS_FUEL]
        val matrixSlot = menu.itemHandler!!.getStackInSlot(MATRIX_SLOT)

        guiGraphics.setColor(1f, 1f, 1f, 0.4f) // Ghosts

        if (hasFuel == FALSE && menu.itemHandler!!.getStackInSlot(FUEL_SLOT).isEmpty)
            guiGraphics.blit(base.left() + base.width / 6 - 8, base.top() + 20, 0, 16, 16, EMPTY_INGOT)

        guiGraphics.renderFakeItem(ModItems.DIMENSIONAL_MATRIX.toStack(), base.left() + base.width / 6 * 5 - 8, base.top() + 20)
        guiGraphics.setColor(1f, 1f, 1f, 1f)

        val fuelSlotSave = menu.itemHandler!!.getStackInSlot(FUEL_SLOT_SAVE).copy()
        fuelSlotSave.set(DataComponents.MAX_DAMAGE, menu.containerData[FUEL_LASTS])
        fuelSlotSave.set(DataComponents.DAMAGE, menu.containerData[FUEL_CURR])

        if (!fuelSlotSave.isEmpty) {
            guiGraphics.renderFakeItem(fuelSlotSave, fuelRect.left(), fuelRect.top())
            guiGraphics.renderItemDecorations(font, fuelSlotSave, fuelRect.left(), fuelRect.top())
            guiGraphics.blitSprite(LOCK, fuelRect.left() + 13, fuelRect.top() + 12, 151, 6, 8)
            if (fuelRect.containsPoint(mouseX, mouseY)) {
                guiGraphics.renderComponentTooltip(font, Helpers.itemComponentSplit("tooltip.azurum_miner.generator.clear"), mouseX, mouseY)
            }
        }
        val baseSlotSave = menu.itemHandler!!.getStackInSlot(BASE_SLOT_SAVE).copy()
        baseSlotSave.set(DataComponents.MAX_DAMAGE, menu.containerData[BASE_LASTS])
        baseSlotSave.set(DataComponents.DAMAGE, menu.containerData[BASE_CURR])
        if (!baseSlotSave.isEmpty) {
            guiGraphics.renderFakeItem(baseSlotSave, baseRect.left(), baseRect.top())
            guiGraphics.renderItemDecorations(font, baseSlotSave, baseRect.left(), baseRect.top())
            guiGraphics.blitSprite(LOCK, baseRect.left() + 13, baseRect.top() + 12, 151, 6, 8)
            if (baseRect.containsPoint(mouseX, mouseY)) {
                guiGraphics.renderComponentTooltip(font, Helpers.itemComponentSplit("tooltip.azurum_miner.generator.clear"), mouseX, mouseY)
            }
        }

        if (hasFuel == FALSE || hasBase == FALSE || matrixSlot.isEmpty || menu.energyStorage!!.energyStored >= menu.energyStorage!!.maxEnergyStored) {
            guiGraphics.drawCenteredString(font, "Error {!}", details.getCenterInAxis(ScreenAxis.HORIZONTAL), details.top() + 19, 0xFFFF0000.toInt())
            if (details.containsPoint(mouseX, mouseY)) {
                val list = ArrayList<Component>()
                if (matrixSlot.isEmpty) {
                    list += Component.translatable("tooltip.azurum_miner.generator.error_matrix").withColor(0xFFFF0000.toInt())
                    guiGraphics.renderOutline(base.left() + base.width / 6 * 5 - 9, base.top() + 19, 18, 18, 0xFFFF0000.toInt())
                }
                if (hasBase == FALSE && menu.itemHandler!!.getStackInSlot(BASE_SLOT).isEmpty) {
                    list += Component.translatable("tooltip.azurum_miner.generator.error_base").withColor(0xFFFF0000.toInt())
                    guiGraphics.renderOutline(base.left() + base.width / 6 - 9, base.top() + 46, 18, 18, 0xFFFF0000.toInt())
                }

                if (hasFuel == FALSE && menu.itemHandler!!.getStackInSlot(FUEL_SLOT).isEmpty) {
                    list += Component.translatable("tooltip.azurum_miner.generator.error_fuel").withColor(0xFFFF0000.toInt())
                    guiGraphics.renderOutline(base.left() + base.width / 6 - 9, base.top() + 19, 18, 18, 0xFFFF0000.toInt())
                }

                if (menu.energyStorage!!.energyStored >= menu.energyStorage!!.maxEnergyStored) {
                    list += Component.translatable("tooltip.azurum_miner.generator.error_full").withColor(0xFFFF0000.toInt())
                }

                guiGraphics.renderComponentTooltip(font, list, mouseX, mouseY)
            }
        } else {
            val fuelPower = menu.containerData[FUEL_POWER]

            val buffer = Unpooled.buffer()
            buffer.writeInt(menu.containerData[BASE_MULT])
            val baseMultiplier = ByteBufCodecs.FLOAT.decode(buffer)

            guiGraphics.drawCenteredString(font, getFE(fuelPower) + "/t", details.getCenterInAxis(ScreenAxis.HORIZONTAL), details.top() + 6, CommonColors.SOFT_YELLOW)
            guiGraphics.drawCenteredString(font, Component.translatable("tooltip.azurum_miner.generator.base", String.format("%.1fx", baseMultiplier)), details.getCenterInAxis(ScreenAxis.HORIZONTAL), details.top() + 19, 0xFF7777FF.toInt())
            guiGraphics.drawCenteredString(font, getFE(fuelPower * baseMultiplier) + "/t", details.getCenterInAxis(ScreenAxis.HORIZONTAL), details.top() + 32, 0xFF77FF77.toInt())
        }

        super.renderBg(guiGraphics, partialTick, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && hasControlDown()) {
            if (fuelRect.containsPoint(mouseX.toInt(), mouseY.toInt())) {
                PacketDistributor.sendToServer(ClearPayload(FUEL_SLOT_SAVE, menu.pos))
                (menu.itemHandler as ItemStackHandler).setStackInSlot(FUEL_SLOT_SAVE, ItemStack.EMPTY)
            }
            if (baseRect.containsPoint(mouseX.toInt(), mouseY.toInt())) {
                (menu.itemHandler as ItemStackHandler).setStackInSlot(BASE_SLOT_SAVE, ItemStack.EMPTY)
                PacketDistributor.sendToServer(ClearPayload(BASE_SLOT_SAVE, menu.pos))
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}