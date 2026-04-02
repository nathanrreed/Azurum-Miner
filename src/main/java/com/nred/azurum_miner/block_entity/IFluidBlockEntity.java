package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.handler.ResourceHandlerDirectionMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public interface IFluidBlockEntity {
    FluidStacksResourceHandler getFluidHandler();

    ResourceHandlerDirectionMode getSideMode(Direction side);

    default FluidStacksResourceHandler getFluidHandler(Direction side) {
        return getSideMode(side) == ResourceHandlerDirectionMode.NONE ? null : getFluidHandler();
    }

    default boolean pushFilter(FluidResource fluidResource) {
        return true;
    }

    default void pushFluidToSides(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (getSideMode(direction) == ResourceHandlerDirectionMode.AUTO_OUTPUT) {
                ResourceHandler<FluidResource> external = level.getCapability(Capabilities.Fluid.BLOCK, pos.relative(direction), direction.getOpposite());
                ResourceHandler<FluidResource> internal = getFluidHandler(direction);
                if (internal != null && external != null) {
                    ResourceHandlerUtil.moveStacking(internal, external, this::pushFilter, Integer.MAX_VALUE, null);
                }
            }
        }
    }
}