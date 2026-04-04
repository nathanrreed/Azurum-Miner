package com.nred.azurum_miner.widget.side_mode;

import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import com.nred.azurum_miner.network.SideModeAllPayload;
import com.nred.azurum_miner.widget.ShrinkingButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Map;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.util.Helpers.azLoc;

public class SideModeSaveButton<T extends BlockEntity & ISidedBlockEntity> extends ShrinkingButton {
    private static final Identifier SAVE = azLoc("widget/side_mode/save");
    private static final Identifier EDIT = azLoc("widget/side_mode/edit");

    public final T blockEntity;
    public final boolean isFluid;
    public boolean editMode = false;
    public Map<Direction, ResourceHandlerSideMode> sideModeMap = ResourceHandlerSideMode.getDefault();

    protected SideModeSaveButton(T blockEntity, boolean isFluid) {
        super(0, 0, 11, 11, Component.empty(), SideModeSaveButton::onPress, Button.DEFAULT_NARRATION);
        this.blockEntity = blockEntity;
        this.isFluid = isFluid;

        setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.side.edit")));
    }

    private static void onPress(Button button) {
        if (button instanceof SideModeSaveButton<?> btn) {
            if (!btn.editMode) {
                btn.editMode = true;
                btn.setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.side.save")));
                btn.sideModeMap.putAll(btn.blockEntity.getSideModes(btn.isFluid));
            } else {
                ClientPacketDistributor.sendToServer(new SideModeAllPayload(btn.sideModeMap, btn.blockEntity.getBlockPos(), btn.isFluid));
                btn.setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.side.edit")));
            }
        }
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int x, int y, float a) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, editMode ? SAVE : EDIT, this.getX(), this.getY(), this.width, this.height, ARGB.setBrightness(editMode ? 0XFFA096ED : 0XFFBABABA, isHovered ? 1f : 0.9f));
    }
}