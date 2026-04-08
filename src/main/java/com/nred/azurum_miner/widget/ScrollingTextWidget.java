package com.nred.azurum_miner.widget;

import com.nred.azurum_miner.screen.SidebarScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.network.chat.Component;

import static com.nred.azurum_miner.config.ClientConfig.USE_RIGHT_SIDE;

public class ScrollingTextWidget extends FittingMultiLineTextWidget {
    private static final Font font = Minecraft.getInstance().font;
    private final SidebarScreen<?, ?> screen;

    public ScrollingTextWidget(int x, int y, int width, int height, Component message, SidebarScreen<?, ?> screen) {
        super(x, y, width, height, message, font);
        this.screen = screen;
        setHeight(Math.min(height, contentHeight()));
    }

    @Override
    public int getRight() {
        return super.getRight();
    }

    @Override
    protected int scrollBarX() {
        return screen.rightSided ? this.getX() + 5 : super.scrollBarX();
    }

    @Override
    public int scrollBarY() {
        return this.maxScrollAmount() == 0
                ? this.getY() + 18
                : Math.max(this.getY() + 18, (int) this.scrollAmount() * (this.height - 20 - this.scrollerHeight()) / this.maxScrollAmount() + this.getY() + 18);
    }

    @Override
    protected int getInnerLeft() {
        return screen.rightSided ? this.getX() + 18 : super.getInnerLeft();
    }

    @Override
    protected int totalInnerPadding() {
        return USE_RIGHT_SIDE.get() ? 22 : super.totalInnerPadding() + 2; // Called in super constructor
    }

    @Override
    protected int contentHeight() {
        return this.getInnerHeight() + super.totalInnerPadding();
    }

    @Override
    public int getHeight() {
        return this.visible ? super.getHeight() : 0;
    }

    @Override
    public int getWidth() {
        return this.visible ? super.getWidth() : 0;
    }

    @Override
    protected void extractBorder(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
    }

    @Override
    protected void extractScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.enableScissor(scrollBarX(), getY() + 18, scrollBarX() + scrollbarWidth(), getY() + getHeight() - 2);
        super.extractScrollbar(graphics, mouseX, mouseY);
        graphics.disableScissor();
    }
}