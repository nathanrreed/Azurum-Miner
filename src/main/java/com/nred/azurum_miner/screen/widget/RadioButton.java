package com.nred.azurum_miner.screen.widget;

import com.nred.azurum_miner.util.GuiHelpers;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import static com.nred.azurum_miner.util.Helpers.azLoc;

public abstract class RadioButton extends Button {
    public boolean isSelected = false;

    protected RadioButton(OnPress onPress) {
        super(0, 0, 20, 20, Component.empty(), onPress, Button.DEFAULT_NARRATION);
    }

    protected void extractContents(GuiGraphicsExtractor graphics, int x, int y, float a) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, azLoc("widget/radio_button/slot" + (isSelected ? "_selected" : "")), getX(), getY(), 20, 20);
    }

    public static class ItemRadioButton extends RadioButton {
        public final Item item;

        protected ItemRadioButton(Item item, OnPress onPress) {
            super(onPress);
            this.item = item;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int x, int y, float a) {
            super.extractContents(graphics, x, y, a);
            graphics.fakeItem(item.getDefaultInstance(), getX() + 2, getY() + 2);
        }
    }

    public static class FluidRadioButton extends RadioButton {
        public final Fluid fluid;

        protected FluidRadioButton(Fluid fluid, OnPress onPress) {
            super(onPress);
            this.fluid = fluid;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int x, int y, float a) {
            super.extractContents(graphics, x, y, a);
            GuiHelpers.blitFluid(graphics, RenderPipelines.GUI_TEXTURED, fluid, getX() + 2, getY() + 2, 16, 16, true);
        }
    }
}