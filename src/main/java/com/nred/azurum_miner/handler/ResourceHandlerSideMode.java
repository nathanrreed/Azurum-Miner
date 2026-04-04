package com.nred.azurum_miner.handler;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nred.azurum_miner.AzurumMiner.MODID;

public enum ResourceHandlerSideMode implements StringRepresentable {
    INPUT, OUTPUT, AUTO_OUTPUT, INPUT_OUTPUT, NONE;

    public static final StringRepresentable.EnumCodec<ResourceHandlerSideMode> CODEC = StringRepresentable.fromEnum(ResourceHandlerSideMode::values);

    public boolean allowInput() {
        return this == INPUT || this == INPUT_OUTPUT;
    }

    public boolean allowOutput() {
        return this == OUTPUT || this == AUTO_OUTPUT || this == INPUT_OUTPUT;
    }

    public boolean isBlocked() {
        return this == NONE;
    }

    public int getColour() {
        return switch (this) { // TODO move to config
            case INPUT -> 0xFF99FF33;
            case OUTPUT -> 0xFF33FFFF;
            case AUTO_OUTPUT -> 0xFF9933FF;
            case INPUT_OUTPUT -> 0xFFFF3399;
            case NONE -> 0xFFA0A0A0;
        };
    }

    public Component getComponent() {
        return Component.translatable(MODID + ".tooltip.side." + getSerializedName()).withColor(getColour());
    }

    public ResourceHandlerSideMode getNext(boolean prev) {
        return prev ? getPrev() : getNext();
    }

    public ResourceHandlerSideMode getNext() {
        return switch (this) {
            case INPUT -> OUTPUT;
            case OUTPUT -> AUTO_OUTPUT;
            case AUTO_OUTPUT -> INPUT_OUTPUT;
            case INPUT_OUTPUT -> NONE;
            case NONE -> INPUT;
        };
    }

    public ResourceHandlerSideMode getPrev() {
        return switch (this) {
            case INPUT -> NONE;
            case OUTPUT -> INPUT;
            case AUTO_OUTPUT -> OUTPUT;
            case INPUT_OUTPUT -> AUTO_OUTPUT;
            case NONE -> INPUT_OUTPUT;
        };
    }

    public static Map<Direction, ResourceHandlerSideMode> getDefault() {
        return Direction.stream().collect(Collectors.toMap(Function.identity(), _ -> INPUT));
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}