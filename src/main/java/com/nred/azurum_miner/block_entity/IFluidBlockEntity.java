package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.handler.RangedFluidStacksResourceHandler;
import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import com.nred.azurum_miner.handler.SidedFluidStacksResourceHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public interface IFluidBlockEntity extends ISidedBlockEntity {
    RangedFluidStacksResourceHandler getFluidHandler();

    default FluidStacksResourceHandler getFluidHandler(Direction side) {
        if (side == null) {
            return getFluidHandler();
        } else if (getSideFluidMode(side).isBlocked()) {
            return null;
        } else {
            return new SidedFluidStacksResourceHandler(getFluidHandler(), this, side);
        }
    }

    default boolean pushFilter(FluidResource fluidResource) {
        return true;
    }

    default void autoOutputFluidToSides(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (getSideFluidMode(direction) == ResourceHandlerSideMode.AUTO_OUTPUT) {
                ResourceHandler<FluidResource> external = level.getCapability(Capabilities.Fluid.BLOCK, pos.relative(direction), direction.getOpposite());
                ResourceHandler<FluidResource> internal = getFluidHandler(direction);
                if (internal != null && external != null) {
                    ResourceHandlerUtil.moveStacking(internal, external, this::pushFilter, 2000, null); // TODO customize amount?
                }
            }
        }
    }
}