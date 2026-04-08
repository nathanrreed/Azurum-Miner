package com.nred.azurum_miner.block_entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.SimpleMapCodec;
import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import com.nred.azurum_miner.widget.side_mode.SideModeType;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import java.util.Map;

public interface ISidedBlockEntity {
    SimpleMapCodec<Direction, ResourceHandlerSideMode> CODEC = Codec.simpleMap(Direction.CODEC, ResourceHandlerSideMode.CODEC, StringRepresentable.keys(ResourceHandlerSideMode.values()));

    default ResourceHandlerSideMode getSideMode(Direction side, SideModeType sideModeType) {
        return switch (sideModeType) {
            case ITEM -> getSideItemMode(side);
            case FLUID -> getSideFluidMode(side);
            case ENERGY -> getSideEnergyMode(side);
        };
    }

    ResourceHandlerSideMode getSideItemMode(Direction side);

    ResourceHandlerSideMode getSideFluidMode(Direction side);

    default ResourceHandlerSideMode getSideEnergyMode(Direction side) {
        return null;
    }

    void setSideMode(Direction side, ResourceHandlerSideMode mode, SideModeType sideModeType);

    Map<Direction, ResourceHandlerSideMode> getSideModes(SideModeType sideModeType);

    void setSideModes(Map<Direction, ResourceHandlerSideMode> sideModeMap, SideModeType sideModeType);
}