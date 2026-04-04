package com.nred.azurum_miner.widget.side_mode;

import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import com.nred.azurum_miner.screen.SideModeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.nred.azurum_miner.util.Helpers.azLoc;
import static com.nred.azurum_miner.util.Helpers.getRelative;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class SideModeWidget<T extends BlockEntity & ISidedBlockEntity> extends GridLayout implements Renderable {
    private static final Identifier BACKGROUND = azLoc("widget/side_mode/background");
    private static boolean item_hidden = false;
    private static boolean fluid_hidden = false;

    public final SideModeSaveButton<T> save_button;
    private final boolean isFluid;

    public SideModeWidget(T blockEntity, boolean isFluid) {
        super();
        this.isFluid = isFluid;
        this.spacing(1);

        SideModeHideButton hideButton = isFluid ?
                new SideModeHideButton(_ -> {
                    fluid_hidden = !fluid_hidden;
                    setHidden(fluid_hidden);
                }, true)
                :
                new SideModeHideButton(_ -> {
                    item_hidden = !item_hidden;
                    setHidden(item_hidden);
                }, false);

        save_button = new SideModeSaveButton<>(blockEntity, isFluid);
        this.addChild(new SideModeButton<>(blockEntity, save_button, Direction.UP, isFluid), 0, 1, newCellSettings().paddingTop(3));
        this.addChild(new SideModeButton<>(blockEntity, save_button, Direction.DOWN, isFluid), 2, 1, newCellSettings().paddingBottom(3));
        this.addChild(new SideModeButton<>(blockEntity, save_button, getRelative(blockEntity.getBlockState().getValue(FACING), Direction.NORTH), isFluid), 1, 1);
        this.addChild(new SideModeButton<>(blockEntity, save_button, getRelative(blockEntity.getBlockState().getValue(FACING), Direction.EAST), isFluid), 1, 2, newCellSettings().paddingRight(3));
        this.addChild(new SideModeButton<>(blockEntity, save_button, getRelative(blockEntity.getBlockState().getValue(FACING), Direction.SOUTH), isFluid), 2, 0, newCellSettings().paddingLeft(3).paddingBottom(3));
        this.addChild(new SideModeButton<>(blockEntity, save_button, getRelative(blockEntity.getBlockState().getValue(FACING), Direction.WEST), isFluid), 1, 0, newCellSettings().paddingLeft(3));

        this.addChild(hideButton, 0, 2, newCellSettings().alignHorizontallyRight().alignVerticallyTop().paddingRight(2).paddingTop(2).paddingLeft(-3).paddingBottom(-3));
        this.addChild(save_button, 2, 2, newCellSettings().align(0.65f, 0.55f));

        if (isFluid) {
            setHidden(fluid_hidden);
        } else {
            setHidden(item_hidden);
        }
    }

    public void setHidden(boolean hidden) {
        visitChildren(child -> {
            if (!(child instanceof SideModeHideButton) && child instanceof Button btn) {
                btn.visible = !hidden;
            }

            if (Minecraft.getInstance().screen instanceof SideModeScreen<?, ?> screen) {
                screen.layout.arrangeElements();
            }
        });
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, float a) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getX(), this.getY(), this.getWidth(), this.getHeight(), isFluid ? 0XFF5276AD : 0XFFD08739);
    }
}