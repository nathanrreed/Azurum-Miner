package com.nred.azurum_miner.handler;

import net.minecraft.core.Direction;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ResourceHandlerDirectionMode {
    INPUT, OUTPUT, AUTO_OUTPUT, INPUT_OUTPUT, NONE;

    public boolean allowInput() {
        return this == INPUT || this == INPUT_OUTPUT;
    }

    public boolean allowOutput() {
        return this == OUTPUT || this == AUTO_OUTPUT || this == INPUT_OUTPUT;
    }

    public static Map<Direction, ResourceHandlerDirectionMode> getDefault() {
        return Direction.stream().collect(Collectors.toMap(Function.identity(), _ -> INPUT));
    }
}