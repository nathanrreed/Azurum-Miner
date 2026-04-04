package com.nred.azurum_miner.block_entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.SimpleMapCodec;
import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import java.util.Map;

public interface ISidedBlockEntity {
    SimpleMapCodec<Direction, ResourceHandlerSideMode> CODEC = Codec.simpleMap(Direction.CODEC, ResourceHandlerSideMode.CODEC, StringRepresentable.keys(ResourceHandlerSideMode.values()));

    default ResourceHandlerSideMode getSideMode(Direction side, boolean isFluid) {
        return isFluid ? getSideFluidMode(side) : getSideItemMode(side);
    }

    ResourceHandlerSideMode getSideItemMode(Direction side);

    ResourceHandlerSideMode getSideFluidMode(Direction side);

    void setSideMode(Direction side, ResourceHandlerSideMode mode, boolean isFluid);

    Map<Direction, ResourceHandlerSideMode> getSideModes(boolean isFluid);

    void setSideModes(Map<Direction, ResourceHandlerSideMode> sideModeMap, boolean isFluid);
}