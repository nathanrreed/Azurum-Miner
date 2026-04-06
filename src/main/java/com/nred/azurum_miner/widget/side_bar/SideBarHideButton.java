package com.nred.azurum_miner.widget.side_bar;

import com.nred.azurum_miner.screen.SidebarScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import static com.nred.azurum_miner.AzurumMiner.MODID;

public class SideBarHideButton extends Button {
    private final SidebarScreen<?, ?> screen;
    private final SideBarElementType type;

    protected SideBarHideButton(SidebarScreen<?, ?> screen, SideBarElementType type) {
        super(0, 0, 16, 16, Component.empty(), SideBarHideButton::onPressed, Button.DEFAULT_NARRATION);
        this.screen = screen;
        this.type = type;
        setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.side_bar." + type.getSerializedName())));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (type.isItemIcon()) {
            graphics.fakeItem(type.itemIcon(), this.getX(), this.getY() + type.offsetY());
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, type.spriteIcon(), this.getX(), this.getY() + type.offsetY(), 16, 16);
        }
    }

    private static void onPressed(Button button) {
        if (button instanceof SideBarHideButton btn) {
            btn.type.toggleOpen();
            btn.screen.arrangeSidebar();
        }
    }
}