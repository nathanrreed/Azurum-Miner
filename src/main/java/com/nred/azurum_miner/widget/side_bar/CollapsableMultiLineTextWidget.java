package com.nred.azurum_miner.widget.side_bar;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.network.chat.Component;

public class CollapsableMultiLineTextWidget extends MultiLineTextWidget {
    public CollapsableMultiLineTextWidget(Component message, Font font) {
        super(message, font);
    }

    @Override
    public int getHeight() {
        return this.visible ? super.getHeight() : 0;
    }

    @Override
    public int getWidth() {
        return this.visible ? super.getWidth() : 0;
    }
}