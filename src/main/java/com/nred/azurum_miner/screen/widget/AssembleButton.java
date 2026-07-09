package com.nred.azurum_miner.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import static com.nred.azurum_miner.AzurumMiner.MODID;

public class AssembleButton extends SpriteIconButton.CenteredIcon {
    private static final Identifier ERROR_SPRITE = Identifier.withDefaultNamespace("spectator/close");
    private static final Identifier BUTTON_DISABLED_SPRITE = Identifier.withDefaultNamespace("widget/button_disabled");

    public AssembleButton(OnPress onPress) {
        super(20, 20, Component.translatable(MODID + ".tooltip.upgrade_table.assemble"), 18, 18, 1, 0, new WidgetSprites(Identifier.withDefaultNamespace("statistics/item_crafted")), onPress, Component.translatable(MODID + ".tooltip.upgrade_table.assemble"), null, false);
    }

    @Override
    protected boolean extractLoadingStateIfLoading(GuiGraphicsExtractor graphics) {
        if (!this.loading) {
            return false;
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_DISABLED_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ERROR_SPRITE, this.getX() + 2, this.getY() + 2, 16, 16, ARGB.white(this.alpha));

        return true;
    }
}