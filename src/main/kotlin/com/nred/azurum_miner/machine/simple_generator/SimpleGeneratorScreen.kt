package com.nred.azurum_miner.machine.simple_generator

import com.nred.azurum_miner.machine.MachineScreen
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorEntity.Companion.SimpleGeneratorEnum.PROCESSING_TIME
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorEntity.Companion.SimpleGeneratorEnum.PROGRESS
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorEntity.Companion.get
import com.nred.azurum_miner.util.Helpers.azLoc
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class SimpleGeneratorScreen(menu: SimpleGeneratorMenu, playerInventory: Inventory, title: Component) : MachineScreen<SimpleGeneratorMenu>(menu, playerInventory, title) {
    companion object {
        val BASE: ResourceLocation = azLoc("simple_generator")
    }

    override fun resize(width: Int, imageWidth: Int, height: Int, imageHeight: Int) {
        super.resize(width, imageWidth, height, imageHeight)
        energy = ScreenRectangle(base.right() - 12, base.top() + 8, 4, 63)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blitSprite(BASE, base.left(), base.top(), 0, base.width, base.height)

        if (menu.containerData[PROGRESS] > 0) {
            val varLen = 14 - Mth.ceil(Mth.clamp(menu.containerData[PROGRESS].toDouble() / menu.containerData[PROCESSING_TIME].toDouble(), 0.0, 1.0) * 13.0) + 1
            guiGraphics.blitSprite(FLAME_FILLED, 14, 14, 0, 14 - varLen, base.left() + SimpleGeneratorMenu.slot_x + 2, base.top() + SimpleGeneratorMenu.slot_y + 22 + 14 - varLen, 14, varLen)
        }
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY)
    }
}