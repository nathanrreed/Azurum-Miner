package com.nred.azurum_miner.widget.side_bar;

import com.nred.azurum_miner.screen.SidebarScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import static com.nred.azurum_miner.util.Helpers.azLoc;

public class CollapsableWidget extends GridLayout implements Renderable {
    private static final Identifier BACKGROUND = azLoc("widget/side_mode/background");
    public final SidebarScreen<?, ?> screen;
    public final SideBarElementType type;
    public final SideBarHideButton sideBarHideButton;

    public CollapsableWidget(SidebarScreen<?, ?> screen, SideBarElementType type) {
        super();
        this.screen = screen;
        this.type = type;
        this.spacing(1);

        screen.layout.addChild(this);

        this.sideBarHideButton = new SideBarHideButton(screen, type);

        this.addChild(sideBarHideButton, 0, screen.rightSided ? 0 : 2, type.getCellSettings(newCellSettings(), screen.rightSided));
    }

    @Override
    public <T extends LayoutElement> T addChild(T child, int row, int column, int rows, int columns, LayoutSettings cellSettings) {
        T element = super.addChild(child, row, column, rows, columns, cellSettings);
        this.screen.arrangeSidebar();
        return element;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, float a) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getX(), this.getY(), this.getWidth(), this.getHeight(), type.getBackgroundColour());
    }
}