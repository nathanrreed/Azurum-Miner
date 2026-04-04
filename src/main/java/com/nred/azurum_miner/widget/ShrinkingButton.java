package com.nred.azurum_miner.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public abstract class ShrinkingButton extends Button {
    protected ShrinkingButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, message, onPress, createNarration);
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