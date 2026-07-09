package com.nred.azurum_miner.tooltip;

import com.nred.azurum_miner.data_components.UpgradeComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.config.ClientConfig.*;
import static com.nred.azurum_miner.config.ServerConfig.UPGRADE_MB_PER_TICK;
import static com.nred.azurum_miner.util.Helpers.getPercentage;

public class UpgradeTooltipComponent implements ClientTooltipComponent, TooltipComponent {
    private final UpgradeComponent upgradeContent;

    public UpgradeTooltipComponent(UpgradeComponent upgradeContent) {
        this.upgradeContent = upgradeContent;
    }

    @Override
    public int getHeight(Font font) {
        return font.lineHeight * 3 + 1;
    }

    @Override
    public int getWidth(Font font) {
        return getText().stream().mapToInt(font::width).max().getAsInt();
    }

    public List<Component> getText() {
        return List.of(
                Component.translatable(MODID + ".tooltip.upgrade.speed", getPercentage(upgradeContent.speed())).withColor(SPEED_COLOUR.get()),
                Component.translatable(MODID + ".tooltip.upgrade.energy", getPercentage(upgradeContent.energy())).withColor(ENERGY_COLOUR.get()),
                Component.translatable(MODID + ".tooltip.upgrade.coolant", upgradeContent.fluidStack().getFluidType().getDescription().copy().withStyle(ChatFormatting.WHITE).append(" " + UPGRADE_MB_PER_TICK.get() + "mB/t")).withColor(FLUID_COLOUR.get())
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