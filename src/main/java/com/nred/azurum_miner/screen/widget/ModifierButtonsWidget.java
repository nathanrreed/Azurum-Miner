package com.nred.azurum_miner.screen.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.nred.azurum_miner.menu.UpgradeTableMenu;
import com.nred.azurum_miner.network.UpgradeTableSelectPayload;
import com.nred.azurum_miner.registration.DataMapRegistration.ModifierTypeData;
import com.nred.azurum_miner.screen.UpgradeSchematicTableScreen;
import com.nred.azurum_miner.util.DataMapUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import oshi.util.tuples.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static com.nred.azurum_miner.registration.DataMapRegistration.MODIFIER_TYPE_DATA;

public class ModifierButtonsWidget implements Layout {
    public final GridLayout gridLayout = new GridLayout();
    private final ScrollableLayout scrollableLayout;
    private final UpgradeTableMenu menu;

    public ModifierButtonsWidget(List<Pair<Item, ModifierTypeData>> elements, UpgradeTableMenu menu) {
        this.menu = menu;

        RowHelper rowHelper = gridLayout.createRowHelper(10);
        for (Pair<Item, ModifierTypeData> element : elements.stream().sorted(Comparator.comparing(e -> e.getB().complexity())).toList()) {
            Button button;
            if (element.getA() instanceof Item item) {
                button = new ItemButton(new ItemStack(item, menu.blockEntity.modifiers.get(item) + 1), DataMapUtil.dataMapItem(MODIFIER_TYPE_DATA, item).max_count(), this::onPress);
            } else {
                continue;
            }
            button.setTooltip(element.getB().tooltip(element.getA()));
            rowHelper.addChild(button);
        }
        scrollableLayout = new ScrollableLayout(Minecraft.getInstance(), gridLayout, 40);
    }

    private void onPress(Button button, InputWithModifiers input) {
        if (button instanceof ItemButton btn) {
            if (input.hasControlDown()) {
                btn.stack.setCount(((input.input() == InputConstants.MOUSE_BUTTON_LEFT) ? btn.maxSize + 1 : 1));
            } else {
                btn.stack.setCount(Mth.clamp(btn.stack.getCount() + ((input.input() == InputConstants.MOUSE_BUTTON_LEFT) ? 1 : -1), 1, btn.maxSize + 1));
            }

            ClientPacketDistributor.sendToServer(new UpgradeTableSelectPayload(menu.blockEntity.getBlockPos(), UpgradeTableSelectPayload.SelectType.MODIFIER, btn.stack));
            menu.blockEntity.modifiers.replace(btn.stack.getItem(), btn.stack.count() - 1);

            if (Minecraft.getInstance().gui.screen() instanceof UpgradeSchematicTableScreen screen) {
                screen.calculateStats();
            }
        }
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> layoutElementVisitor) {
        gridLayout.visitChildren(layoutElementVisitor);
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> widgetVisitor) {
        gridLayout.visitWidgets(widgetVisitor);
    }

    @Override
    public void removeChildren() {
        gridLayout.removeChildren();
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
        return Math.min(40, scrollableLayout.getHeight());
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