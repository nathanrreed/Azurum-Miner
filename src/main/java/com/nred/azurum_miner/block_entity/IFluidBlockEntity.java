package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.handler.AwareFluidStacksResourceHandler;
import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import com.nred.azurum_miner.handler.SidedDelegatingResourceHandler;
import com.nred.azurum_miner.widget.side_mode.SideModeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.apache.commons.lang3.IntegerRange;

public interface IFluidBlockEntity extends ISidedBlockEntity {
    CombinedResourceHandler<FluidResource> getFluidHandler();

    IntegerRange getFluidInputRange();

    IntegerRange getFluidOutputRange();

    AwareFluidStacksResourceHandler getInternalFluidHandler();

    BlockPos getBlockPos();

    default ResourceHandler<FluidResource> getFluidHandler(Direction side) {
        if (side == null) {
            return getFluidHandler();
        } else if (getSideFluidMode(side).isBlocked()) {
            return null;
        } else {
            return new SidedDelegatingResourceHandler<>(getFluidHandler(), this, side, SideModeType.FLUID);
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
                    ResourceHandlerUtil.moveStacking(internal, external, this::pushFilter, this.amountOfFluidToTransfer(), null);
                }
            }
        }
    }

    default int amountOfFluidToTransfer() {
        return 2000;
    }
}