package com.nred.azurum_miner.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import com.nred.azurum_miner.network.SideModePayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import static com.nred.azurum_miner.util.Helpers.azLoc;

public class SlotModeButton<T extends BlockEntity & ISidedBlockEntity> extends Button {
    private static final Identifier SLOT = azLoc("widget/side_mode/direction_slot");
    private final T blockEntity;
    private final SlotModeSaveButton<T> saveButton;
    private final Direction side;
    private final boolean isFluid;

    protected SlotModeButton(int x, int y, T blockEntity, SlotModeSaveButton<T> saveButton, Direction side, boolean isFluid) {
        super(x, y, 18, 18, Component.empty(), null, Button.DEFAULT_NARRATION);
        this.blockEntity = blockEntity;
        this.saveButton = saveButton;
        this.side = side;
        this.isFluid = isFluid;

        setTooltip(Tooltip.create(blockEntity.getSideMode(side, isFluid).getComponent()));
    }

    public ResourceHandlerSideMode getMode() {
        return saveButton.editMode ? saveButton.sideModeMap.get(side) : blockEntity.getSideMode(side, isFluid);
    }

    @Override
    public void onPress(InputWithModifiers input) {
        ResourceHandlerSideMode newMode = getMode().getNext(input.input() == InputConstants.MOUSE_BUTTON_RIGHT);
        setTooltip(Tooltip.create(newMode.getComponent()));
        if (saveButton.editMode) { // Don't send to server
            saveButton.sideModeMap.replace(side, newMode);
        } else {
            ClientPacketDistributor.sendToServer(new SideModePayload(side, blockEntity.getBlockPos(), newMode, isFluid));
        }
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
        return buttonInfo.button() == InputConstants.MOUSE_BUTTON_LEFT || buttonInfo.button() == InputConstants.MOUSE_BUTTON_RIGHT;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int x, int y, float a) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT, this.getX(), this.getY(), this.width, this.height, ARGB.setBrightness(getMode().getColour(), this.isHovered ? 1f : 0.9f));
        graphics.fakeItem(blockEntity.getLevel().getBlockState(blockEntity.getBlockPos().relative(side)).getBlock().asItem().getDefaultInstance(), this.getX() + 1, this.getY() + 1);
    }
}