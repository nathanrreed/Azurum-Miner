package com.nred.azurum_miner.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.nred.azurum_miner.block_entity.IFluidBlockEntity;
import com.nred.azurum_miner.menu.BlockEntityMenu;
import com.nred.azurum_miner.network.FluidTankTransferPayload;
import com.nred.azurum_miner.network.FluidTankTransferPayload.FluidTransferAction;
import com.nred.azurum_miner.tooltip.FluidTooltipComponent;
import com.nred.azurum_miner.util.GuiHelpers;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

import java.util.Optional;

import static com.nred.azurum_miner.util.Helpers.azLoc;

public class FluidWidget<T extends IFluidBlockEntity> extends AbstractWidget {
    private final T blockEntity;
    private final BlockEntityMenu<?> menu;
    private final int index;
    private final RangedResourceHandler<FluidResource> fluidHandler;

    private static final Identifier TANK = azLoc("widget/tank/fluid_tank");

    public FluidWidget(T blockEntity, BlockEntityMenu<?> menu, int x, int y, ScreenRectangle rectangle, int index) {
        super(x + rectangle.left(), y + rectangle.top(), rectangle.width(), rectangle.height(), Component.empty()); // TODO
        this.blockEntity = blockEntity;
        this.menu = menu;
        this.index = index;

        fluidHandler = RangedResourceHandler.ofSingleIndex(blockEntity.getFluidHandler(), index);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        FluidResource fluidResource = fluidHandler.getResource(0);
        GuiHelpers.blitFluid(graphics, RenderPipelines.GUI_TEXTURED, fluidResource, fluidHandler.getAmountAsLong(0), fluidHandler.getCapacityAsLong(0, fluidResource), getX(), getY(), getWidth(), getHeight());
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TANK, getX(), getY(), getWidth(), getHeight());

        if (isHovered()) {
            setTooltip(Tooltip.create(Component.empty(), Optional.of(new FluidTooltipComponent(FluidUtil.getStack(fluidHandler, 0), fluidHandler.getCapacityAsInt(0, fluidHandler.getResource(0)))), null));
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (event.hasShiftDown() && event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            ClientPacketDistributor.sendToServer(new FluidTankTransferPayload(blockEntity.getBlockPos(), FluidTransferAction.VOID, this.index));
        } else if (!this.menu.getCarried().isEmpty()) {
            ResourceHandler<FluidResource> itemHandler = ItemAccess.forPlayerCursor(menu.player, menu).getCapability(Capabilities.Fluid.ITEM);
            if (itemHandler == null) {
                return;
            }

            ClientPacketDistributor.sendToServer(new FluidTankTransferPayload(blockEntity.getBlockPos(), event.button() == InputConstants.MOUSE_BUTTON_LEFT ? FluidTransferAction.FILL : FluidTransferAction.EMPTY, this.index));
        }
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
        return buttonInfo.button() == InputConstants.MOUSE_BUTTON_LEFT || buttonInfo.button() == InputConstants.MOUSE_BUTTON_RIGHT;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // Don't play sound
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}