package com.nred.azurum_miner.widget.side_mode;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import static com.nred.azurum_miner.AzurumMiner.MODID;

public class SideModeHideButton extends Button {
    private final boolean isFluid;

    protected SideModeHideButton(OnPress onPress, boolean isFluid) {
        super(0, 0, 16, 16, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.isFluid = isFluid;
        setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.side." + (isFluid ? "fluid" : "item"))));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fakeItem((isFluid ? Items.WATER_BUCKET : Items.CHEST).getDefaultInstance(), this.getX(), this.getY() + (isFluid ? 0 : -1));
    }
}