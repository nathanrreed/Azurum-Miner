package com.nred.azurum_miner.screen;

import com.nred.azurum_miner.block_entity.TankBlockEntity;
import com.nred.azurum_miner.menu.TankMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class TankScreen extends SideModeScreen<TankBlockEntity, TankMenu> {
    private static final Identifier BG_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/loom.png");

    public TankScreen(TankMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) { // TODO move to super
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_LOCATION, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}