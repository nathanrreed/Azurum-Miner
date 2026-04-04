package com.nred.azurum_miner.screen;

import com.nred.azurum_miner.block_entity.IFluidBlockEntity;
import com.nred.azurum_miner.block_entity.IItemBlockEntity;
import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import com.nred.azurum_miner.menu.BlockEntityMenu;
import com.nred.azurum_miner.widget.side_mode.SideModeWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SideModeScreen<B extends BlockEntity & ISidedBlockEntity, M extends BlockEntityMenu<B>> extends AbstractContainerScreen<M> {
    public SideModeWidget<?> sideModeWidgetItem;
    public SideModeWidget<?> sideModeWidgetFluid;
    public LinearLayout layout;

    public SideModeScreen(M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        layout = new LinearLayout(leftPos - 61, topPos, LinearLayout.Orientation.VERTICAL).spacing(2);

        if (menu.blockEntity instanceof IItemBlockEntity) {
            sideModeWidgetItem = new SideModeWidget<>(menu.blockEntity, false);
            addRenderableOnly(sideModeWidgetItem);
            layout.addChild(sideModeWidgetItem, layout.newCellSettings().alignHorizontallyRight());
        }
        if (menu.blockEntity instanceof IFluidBlockEntity) {
            sideModeWidgetFluid = new SideModeWidget<>(menu.blockEntity, true);
            addRenderableOnly(sideModeWidgetFluid);
            layout.addChild(sideModeWidgetFluid, layout.newCellSettings().alignHorizontallyRight());
        }

        layout.addChild(SpacerElement.width(60));

        this.layout.visitWidgets(this::addRenderableWidget);
        this.layout.arrangeElements();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void repositionElements() {
        super.repositionElements();
        this.layout.arrangeElements();
    }
}