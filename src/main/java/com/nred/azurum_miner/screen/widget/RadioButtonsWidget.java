package com.nred.azurum_miner.screen.widget;

import com.nred.azurum_miner.menu.UpgradeTableMenu;
import com.nred.azurum_miner.network.UpgradeTableSelectPayload;
import com.nred.azurum_miner.network.UpgradeTableSelectPayload.SelectType;
import com.nred.azurum_miner.registration.DataMapRegistration;
import com.nred.azurum_miner.screen.UpgradeSchematicTableScreen;
import com.nred.azurum_miner.screen.widget.RadioButton.FluidRadioButton;
import com.nred.azurum_miner.screen.widget.RadioButton.ItemRadioButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.fluids.FluidStack;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.function.Consumer;

public class RadioButtonsWidget implements Layout {
    public final GridLayout gridLayout = new GridLayout();
    public final ScrollableLayout scrollableLayout;
    private final UpgradeTableMenu menu;
    private RadioButton selected;
    private static final short maxHeight = 68;

    public <T, U extends DataMapRegistration.TooltipDataMap<T>> RadioButtonsWidget(List<Pair<T, U>> elements, UpgradeTableMenu menu) {
        this.menu = menu;

        gridLayout.defaultCellSetting().alignHorizontallyCenter();

        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(4);
        for (Pair<T, U> element : elements) {
            RadioButton radioButton;
            if (element.getA() instanceof Item item) {
                radioButton = new ItemRadioButton(item, this::onPress);
                if (menu.blockEntity.card.is(item)) {
                    radioButton.isSelected = true;
                    selected = radioButton;
                }
            } else if (element.getA() instanceof Fluid fluid) {
                radioButton = new FluidRadioButton(fluid, this::onPress);
                if (menu.blockEntity.coolant.is(fluid)) {
                    radioButton.isSelected = true;
                    selected = radioButton;
                }
            } else {
                continue;
            }
            radioButton.setTooltip(element.getB().tooltip(element.getA()));
            rowHelper.addChild(radioButton);
        }

        scrollableLayout = new ScrollableLayout(Minecraft.getInstance(), gridLayout, maxHeight);
        scrollableLayout.setMinHeight(maxHeight);
    }

    private void onPress(Button btn) {
        if (btn instanceof RadioButton button && !button.equals(selected)) {
            selected.isSelected = false;
            button.isSelected = true;
            selected = button;

            UpgradeTableSelectPayload payload;
            if (btn instanceof ItemRadioButton itemBtn) {
                payload = new UpgradeTableSelectPayload(menu.blockEntity.getBlockPos(), SelectType.CARD, itemBtn.item);
                menu.blockEntity.card = new ItemStack(itemBtn.item);
            } else if (btn instanceof FluidRadioButton fluidBtn) {
                payload = new UpgradeTableSelectPayload(menu.blockEntity.getBlockPos(), SelectType.COOLANT, fluidBtn.fluid);
                menu.blockEntity.coolant = new FluidStack(fluidBtn.fluid, 1);
            } else {
                return;
            }

            ClientPacketDistributor.sendToServer(payload);

            if (Minecraft.getInstance().gui.screen() instanceof UpgradeSchematicTableScreen screen) {
                screen.calculateStats();
            }
        }
    }

    public RadioButton getSelected() {
        return selected;
    }

    public Item getItem() {
        if (selected instanceof ItemRadioButton itemRadioButton) {
            return itemRadioButton.item;
        }
        return Items.AIR;
    }

    public Fluid getFluid() {
        if (selected instanceof FluidRadioButton fluidRadioButton) {
            return fluidRadioButton.fluid;
        }
        return Fluids.EMPTY;
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> layoutElementVisitor) {
        scrollableLayout.visitChildren(layoutElementVisitor);
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> widgetVisitor) {
        scrollableLayout.visitWidgets(widgetVisitor);
    }

    @Override
    public void removeChildren() {
        scrollableLayout.removeChildren();
    }

    @Override
    public void arrangeElements() {
        scrollableLayout.arrangeElements();
    }

    @Override
    public int getWidth() {
        return scrollableLayout.getWidth();
    }

    @Override
    public int getHeight() {
        return scrollableLayout.getHeight();
    }

    @Override
    public void setX(int x) {
        this.scrollableLayout.setX(x);
        this.scrollableLayout.arrangeElements();
    }

    @Override
    public void setY(int y) {
        this.scrollableLayout.setY(y);
        this.scrollableLayout.arrangeElements();
    }

    @Override
    public int getX() {
        return this.scrollableLayout.getX();
    }

    @Override
    public int getY() {
        return this.scrollableLayout.getY();
    }
}