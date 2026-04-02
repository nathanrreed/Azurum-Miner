package com.nred.azurum_miner.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.List;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.config.Config.CAPACITY_COLOUR;
import static com.nred.azurum_miner.config.Config.FLUID_COLOUR;
import static com.nred.azurum_miner.util.Helpers.getFluidAmount;

public class FluidTooltipComponent implements ClientTooltipComponent, TooltipComponent {
    private final SimpleFluidContent fluidContent;
    private final int capacity;

    public FluidTooltipComponent(SimpleFluidContent fluidContent, int capacity) {
        this.fluidContent = fluidContent;
        this.capacity = capacity;
    }

    @Override
    public int getHeight(Font font) {
        return font.lineHeight * 2 + 1;
    }

    @Override
    public int getWidth(Font font) {
        return getText().stream().mapToInt(font::width).max().getAsInt(); // TODO
    }

    public List<Component> getText() {
        return List.of(
                (fluidContent.isEmpty() ? Component.translatable(MODID + ".tooltip.fluid_empty") : Component.translatable(MODID + ".tooltip.fluid_detail", fluidContent.getFluidType().getDescription(), getFluidAmount(fluidContent.getAmount()))).withColor(FLUID_COLOUR.get()),
                Component.translatable(MODID + ".tooltip.fluid_capacity", getFluidAmount(capacity)).withColor(CAPACITY_COLOUR.get())
        );
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        ClientTooltipComponent.super.extractText(graphics, font, x, y);

        int i = 0;
        for (Component part : getText()) {
            graphics.text(font, part, x, y + font.lineHeight * i++, -1);
        }
    }
}