package com.nred.azurum_miner.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class ShrinkingStringWidget extends StringWidget {
    private static final Font font = Minecraft.getInstance().font;

    public ShrinkingStringWidget(Component message, Component tooltip) {
        super(message, font);
        this.setTooltip(Tooltip.create(tooltip));
    }

    @Override
    public int getHeight() {
        return this.visible ? super.getHeight() : 0;
    }

    @Override
    public int getWidth() {
        return this.visible ? super.getWidth() + 4 : 0;
    }
}