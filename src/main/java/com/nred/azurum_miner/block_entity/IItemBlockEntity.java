package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.handler.RangedItemStacksResourceHandler;
import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import com.nred.azurum_miner.handler.SidedItemStacksResourceHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public interface IItemBlockEntity extends ISidedBlockEntity {
    RangedItemStacksResourceHandler getItemHandler();

    default ItemStacksResourceHandler getItemHandler(Direction side) {
        if (side == null) {
            return getItemHandler();
        } else if (getSideItemMode(side).isBlocked()) {
            return null;
        } else {
            return new SidedItemStacksResourceHandler(getItemHandler(), this, side);
        }
    }

    default boolean pushFilter(ItemResource itemResource) {
        return true;
    }

    default void autoOutputItemToSides(Level level, BlockPos pos) {
        for (Direction direction : Direction.allShuffled(level.getRandom())) {
            if (getSideItemMode(direction) == ResourceHandlerSideMode.AUTO_OUTPUT) {
                ResourceHandler<ItemResource> external = level.getCapability(Capabilities.Item.BLOCK, pos.relative(direction), direction.getOpposite());
                ResourceHandler<ItemResource> internal = getItemHandler(direction);
                if (internal != null && external != null) {
                    ResourceHandlerUtil.moveStacking(internal, external, this::pushFilter, 8, null); // TODO change speed?
                }
            }
        }
    }
}