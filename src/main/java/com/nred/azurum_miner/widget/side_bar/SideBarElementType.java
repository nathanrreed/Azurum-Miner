package com.nred.azurum_miner.widget.side_bar;

import com.nred.azurum_miner.widget.side_mode.SideModeType;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.nred.azurum_miner.config.Config.ALLOW_MULTIPLE_OPEN;
import static com.nred.azurum_miner.util.Helpers.azLoc;

public enum SideBarElementType implements StringRepresentable {
    ITEM, FLUID, ENERGY, INFO;

    public static final ArrayList<Boolean> open_list = new ArrayList<>(Arrays.stream(values()).map(_ -> Boolean.FALSE).toList());

    public int offsetY() {
        return switch (this) {
            case ITEM -> -1;
            default -> 0;
        };
    }

    public boolean isItemIcon() {
        return switch (this) {
            case ITEM, FLUID -> true;
            case INFO, ENERGY -> false;
        };
    }

    public ItemStack itemIcon() {
        return (switch (this) {
            case ITEM -> Items.CHEST;
            case FLUID -> Items.WATER_BUCKET;
            default -> throw new IllegalStateException("Unexpected value: " + this);
        }).getDefaultInstance();
    }

    public SideModeType getSideModeType() {
        return switch (this) {
            case ITEM -> SideModeType.ITEM;
            case FLUID -> SideModeType.FLUID;
            case ENERGY -> SideModeType.ENERGY;
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }

    public Identifier spriteIcon() {
        return switch (this) {
            case INFO -> azLoc("widget/side_bar/info");
            case ENERGY -> azLoc("widget/side_bar/energy"); // TODO
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }

    public LayoutSettings getCellSettings(LayoutSettings settings, Boolean right_sided) {

        if (right_sided) {
            settings.alignHorizontallyLeft().alignVerticallyTop().padding(2, 2, paddingOutside(), paddingBottom());
        } else {
            settings.alignHorizontallyRight().alignVerticallyTop().padding(paddingOutside(), 2, 2, paddingBottom());
        }
        return settings;
    }

    public int paddingOutside() {
        return switch (this) {
            case ITEM, FLUID, ENERGY -> -3;
            default -> 0;
        };
    }

    public int paddingBottom() {
        return switch (this) {
            case ITEM, FLUID, ENERGY -> -3;
            default -> 2;
        };
    }

    public int getBackgroundColour() {
        return switch (this) {
            case ITEM -> 0xFFD08739;
            case FLUID -> 0xFF5276AD;
            case ENERGY -> 0xFFad5252;
            case INFO -> 0xFFa5aeac;
        };
    }

    public Boolean getOpen() {
        return open_list.get(this.ordinal());
    }

    public void toggleOpen() {
        boolean temp = !getOpen();
        if (!ALLOW_MULTIPLE_OPEN.get()) {
            Collections.fill(open_list, Boolean.FALSE);
        }
        open_list.set(this.ordinal(), temp);
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}