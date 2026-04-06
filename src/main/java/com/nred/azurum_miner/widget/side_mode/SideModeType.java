package com.nred.azurum_miner.widget.side_mode;

import com.nred.azurum_miner.widget.side_bar.SideBarElementType;
import net.minecraft.util.StringRepresentable;

public enum SideModeType implements StringRepresentable {
    ITEM, FLUID, ENERGY;

    public static final StringRepresentable.EnumCodec<SideModeType> CODEC = StringRepresentable.fromEnum(SideModeType::values);

    public SideBarElementType getSideBarElementType() {
        return switch (this) {
            case ITEM -> SideBarElementType.ITEM;
            case FLUID -> SideBarElementType.FLUID;
            case ENERGY -> SideBarElementType.ENERGY;
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}