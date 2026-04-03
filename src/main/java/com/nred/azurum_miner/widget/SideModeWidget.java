package com.nred.azurum_miner.widget;

import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.nred.azurum_miner.util.Helpers.azLoc;

public class SideModeWidget<T extends BlockEntity & ISidedBlockEntity> extends AbstractWidget implements ContainerEventHandler {
    private final List<Button> buttons;

    private static final Identifier BACKGROUND = azLoc("widget/side_mode/background");


    public SideModeWidget(int x, int y, T blockEntity, boolean isFluid) {
        super(x, y, 60, 60, Component.empty());

        SlotModeSaveButton<T> save_button = new SlotModeSaveButton<>(this.getX() + 46, this.getY() + 45, blockEntity, isFluid);
        buttons = List.of(
                new SlotModeButton<>(this.getX() + 21, this.getY() + 3, blockEntity, save_button, Direction.UP, isFluid),
                new SlotModeButton<>(this.getX() + 21, this.getY() + 39, blockEntity, save_button, Direction.DOWN, isFluid),
                new SlotModeButton<>(this.getX() + 21, this.getY() + 21, blockEntity, save_button, Direction.NORTH, isFluid),
                new SlotModeButton<>(this.getX() + 3, this.getY() + 21, blockEntity, save_button, Direction.EAST, isFluid),
                new SlotModeButton<>(this.getX() + 3, this.getY() + 39, blockEntity, save_button, Direction.SOUTH, isFluid),
                new SlotModeButton<>(this.getX() + 39, this.getY() + 21, blockEntity, save_button, Direction.WEST, isFluid),
                save_button
        );
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int x, int y, float a) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getX(), this.getY(), this.width, this.height);

        for (Button button : buttons) {
            button.extractRenderState(graphics, x, y, a);
        }
    }

    /**
     * @see net.minecraft.client.gui.components.events.ContainerEventHandler#mouseClicked
     * */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        Optional<GuiEventListener> child = this.getChildAt(event.x(), event.y());
        if (child.isEmpty()) {
            return false;
        } else {
            GuiEventListener widget = child.get();
            if (widget.mouseClicked(event, doubleClick) && widget.shouldTakeFocusAfterInteraction()) {
                this.setFocused(widget);
                if (event.button() == 0) {
                    this.setDragging(true);
                }
            }

            return true;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return buttons;
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean b) {
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return null;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener guiEventListener) {
    }
}