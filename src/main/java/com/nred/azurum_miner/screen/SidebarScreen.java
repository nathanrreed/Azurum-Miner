package com.nred.azurum_miner.screen;

import com.nred.azurum_miner.block_entity.*;
import com.nred.azurum_miner.menu.BlockEntityMenu;
import com.nred.azurum_miner.menu.SlotLookup;
import com.nred.azurum_miner.widget.FluidWidget;
import com.nred.azurum_miner.widget.side_bar.*;
import com.nred.azurum_miner.widget.side_mode.SideModeWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

import static com.nred.azurum_miner.config.ClientConfig.SHOW_TRANSFER_RATES;
import static com.nred.azurum_miner.config.ClientConfig.USE_RIGHT_SIDE;

public class SidebarScreen<B extends BlockEntity & ISidedBlockEntity, M extends BlockEntityMenu<B>> extends AbstractContainerScreen<M> {
    private final Identifier background;
    @Nullable
    private final SlotLookup fluidSlotLookup;
    public LinearLayout layout;
    public Boolean rightSided = USE_RIGHT_SIDE.get();

    public SidebarScreen(M menu, Inventory inventory, Component title, Identifier background, @Nullable SlotLookup fluidSlot) {
        super(menu, inventory, title);
        this.background = background;
        this.fluidSlotLookup = fluidSlot;
    }

    @Override
    protected void init() {
        super.init();
        rightSided = USE_RIGHT_SIDE.get();

        create_sidebar();

        if (fluidSlotLookup != null && menu.blockEntity instanceof IFluidBlockEntity fluidBlockEntity) {
            for (int i = 0; i < fluidSlotLookup.getFluidSize(); i++) {
                addRenderableWidget(new FluidWidget<>(fluidBlockEntity, menu, leftPos, topPos, fluidSlotLookup.getFluidRect(i), i));
            }
        }
    }

    public void create_sidebar() {
        layout = new LinearLayout(leftPos, topPos, LinearLayout.Orientation.VERTICAL).spacing(2);

        if (rightSided) {
            layout.defaultCellSetting().alignHorizontallyLeft();
        } else {
            layout.defaultCellSetting().alignHorizontallyRight();
        }

        if (menu.blockEntity instanceof IItemBlockEntity) {
            addRenderableOnly(new SideModeWidget<>(menu.blockEntity, this, SideBarElementType.ITEM));
        }
        if (menu.blockEntity instanceof IFluidBlockEntity) {
            addRenderableOnly(new SideModeWidget<>(menu.blockEntity, this, SideBarElementType.FLUID));
        }
        if (menu.blockEntity instanceof IEnergyBlockEntity) {
            addRenderableOnly(new SideModeWidget<>(menu.blockEntity, this, SideBarElementType.ENERGY));
        }

        if (SHOW_TRANSFER_RATES.get() && !this.renderables.isEmpty()) {
            addRenderableOnly(new TransferInfoWidget(menu.blockEntity, this));
        }

        // TODO make stats ?

        if (menu.blockEntity instanceof IInfoBlockEntity) {
            addRenderableOnly(new InfoWidget(this));
        }

        this.layout.visitWidgets(this::addRenderableWidget);
        arrangeSidebar();
    }

    @Override
    public void repositionElements() {
        super.repositionElements();
        arrangeSidebar();
    }

    public void arrangeSidebar() {
        this.renderables.forEach(renderable -> {
            if (renderable instanceof CollapsableWidget collapsableWidget) {
                collapsableWidget.visitChildren(child -> {
                    if (!(child instanceof SideBarHideButton) && child instanceof AbstractWidget widget) {
                        widget.visible = collapsableWidget.type.getOpen();
                        widget.active = collapsableWidget.type.getOpen();
                    }
                });
            }
        });

        this.layout.arrangeElements();
        if (this.rightSided) {
            layout.setX(leftPos + imageWidth + 2);
        } else {
            layout.setX(leftPos - layout.getWidth() - 2);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, this.background, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}