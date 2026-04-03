package com.nred.azurum_miner.block_entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.SimpleMapCodec;
import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Map;

public interface ISidedBlockEntity {
    Map<Direction, ResourceHandlerSideMode> itemSideModes = ResourceHandlerSideMode.getDefault(); // TODO clean up?
    Map<Direction, ResourceHandlerSideMode> fluidSideModes = ResourceHandlerSideMode.getDefault();
    SimpleMapCodec<Direction, ResourceHandlerSideMode> CODEC = Codec.simpleMap(Direction.CODEC, ResourceHandlerSideMode.CODEC, StringRepresentable.keys(ResourceHandlerSideMode.values()));

    void setChanged();

    default ResourceHandlerSideMode getSideMode(Direction side, boolean isFluid) {
        return (isFluid ? fluidSideModes : itemSideModes).get(side);
    }

    default void setSideMode(Direction side, ResourceHandlerSideMode mode, boolean isFluid) {
        (isFluid ? fluidSideModes : itemSideModes).replace(side, mode);
        setChanged();
    }

    default Map<Direction, ResourceHandlerSideMode> getSideModes(boolean isFluid) {
        return (isFluid ? fluidSideModes : itemSideModes);
    }

    default void setSideModes(Map<Direction, ResourceHandlerSideMode> sideModeMap, boolean isFluid) {
        (isFluid ? fluidSideModes : itemSideModes).putAll(sideModeMap);
        setChanged();
    }

    default void loadSideModes(ValueInput input) {
        itemSideModes.putAll(input.read("item_side_modes", CODEC.codec()).get());
        fluidSideModes.putAll(input.read("fluid_side_modes", CODEC.codec()).get());
    }

    default void saveSideModes(ValueOutput output) {
        output.store("item_side_modes", CODEC.codec(), itemSideModes);
        output.store("fluid_side_modes", CODEC.codec(), fluidSideModes);
    }
}