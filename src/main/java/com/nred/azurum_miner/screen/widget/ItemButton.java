package com.nred.azurum_miner.screen.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class ItemButton extends Button {
    public final ItemStack stack;
    public final int maxSize;
    private final OnPressInput onPressInput;

    protected ItemButton(ItemStack stack, int maxSize, OnPressInput onPressInput) {
        super(0, 0, 18, 18, Component.empty(), null, Button.DEFAULT_NARRATION);
        this.onPressInput = onPressInput;
        this.stack = stack;
        this.maxSize = maxSize;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int x, int y, float a) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("container/slot"), getX(), getY(), 18, 18);
        graphics.fakeItem(stack, getX() + 1, getY() + 1);
        graphics.itemDecorations(Minecraft.getInstance().font, stack, getX() + 1, getY() + 1, "" + (stack.getCount() - 1));
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.onPressInput.onPress(this, input);
    }

    public interface OnPressInput {
        void onPress(final Button button, InputWithModifiers input);
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
        return buttonInfo.button() == InputConstants.MOUSE_BUTTON_LEFT || buttonInfo.button() == InputConstants.MOUSE_BUTTON_RIGHT;
    }
}