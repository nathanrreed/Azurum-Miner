package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.handler.AwareFluidStacksResourceHandler;
import com.nred.azurum_miner.handler.AwareItemStacksResourceHandler;
import com.nred.azurum_miner.handler.AwareRangedResourceHandler;
import com.nred.azurum_miner.handler.AwareRangedResourceHandler.IOState;
import com.nred.azurum_miner.menu.TankMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.commons.lang3.IntegerRange;
import org.jspecify.annotations.Nullable;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.registration.BlockEntityRegistration.TANK_BLOCK_ENTITY;

public class TankBlockEntity extends SidedTickingBlockEntity implements IItemBlockEntity, IFluidBlockEntity, IInfoBlockEntity {
    public TankBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(TANK_BLOCK_ENTITY.get(), worldPosition, blockState);
    }

    public static final int CAPACITY = 32000;
    public static final IntegerRange FLUID_IN_RANGE = IntegerRange.of(0, 0); // TODO REMOVE
    public static final IntegerRange FLUID_OUT_RANGE = IntegerRange.of(0, 0);
    public static final IntegerRange ITEM_IN_RANGE = IntegerRange.of(0, 1);
    public static final IntegerRange ITEM_OUT_RANGE = IntegerRange.of(2, 3);

    protected AwareFluidStacksResourceHandler fluidHandler = new AwareFluidStacksResourceHandler(1, CAPACITY, this);
    protected AwareItemStacksResourceHandler itemHandler = new AwareItemStacksResourceHandler(4, this) {
        /*
         * 0 -> To Empty
         * 1 -> To Fill
         * 2 -> Filled
         * 3 -> Emptied
         */
        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (ITEM_OUT_RANGE.contains(index)) return true;
            ResourceHandler<FluidResource> itemFluidHandler = ItemAccess.forStack(resource.toStack()).getCapability(Capabilities.Fluid.ITEM);
            if (itemFluidHandler == null) return false;
            FluidResource itemFluidResource = FluidResource.EMPTY;
            int itemFluidResourceIndex = 0;
            for (int i = 0; i < itemFluidHandler.size(); i++) {
                FluidResource fluidResource = itemFluidHandler.getResource(i);
                if (!fluidResource.isEmpty()) {
                    itemFluidResource = fluidResource;
                    itemFluidResourceIndex = i;
                    break;
                }
            }

            FluidResource fluidResource = fluidHandler.getResource(0);

            if (index == 0 && !itemFluidResource.isEmpty() && (fluidResource.isEmpty() || fluidResource.is(itemFluidResource.getFluid()))) {
                return true;
            } else if (index == 1 && (itemFluidHandler.getAmountAsInt(itemFluidResourceIndex) < itemFluidHandler.getCapacityAsInt(itemFluidResourceIndex, itemFluidResource) && !fluidResource.isEmpty())) {
                return true;
            }
            return false;
        }

        @Override
        protected int getCapacity(int index, ItemResource resource) {
            return ITEM_IN_RANGE.contains(index) ? 1 : super.getCapacity(index, resource);
        }
    };

    @Override
    public CombinedResourceHandler<FluidResource> getFluidHandler() {
        if (FLUID_IN_RANGE.containsRange(FLUID_OUT_RANGE)) {
            return new CombinedResourceHandler<>(fluidHandler);
        } else {
            return new CombinedResourceHandler<>(new AwareRangedResourceHandler<>(fluidHandler, FLUID_IN_RANGE, IOState.INPUT), new AwareRangedResourceHandler<>(fluidHandler, FLUID_OUT_RANGE, IOState.OUTPUT));
        }
    }

    @Override
    public AwareFluidStacksResourceHandler getInternalFluidHandler() {
        return fluidHandler;
    }

    @Override
    public CombinedResourceHandler<ItemResource> getItemHandler() {
        if (ITEM_IN_RANGE.containsRange(ITEM_OUT_RANGE)) {
            return new CombinedResourceHandler<>(itemHandler);
        } else {
            return new CombinedResourceHandler<>(new AwareRangedResourceHandler<>(itemHandler, ITEM_IN_RANGE, IOState.INPUT), new AwareRangedResourceHandler<>(itemHandler, ITEM_OUT_RANGE, IOState.OUTPUT));
        }
    }

    @Override
    public IntegerRange getItemInputRange() {
        return ITEM_IN_RANGE;
    }

    @Override
    public IntegerRange getItemOutputRange() {
        return ITEM_OUT_RANGE;
    }

    @Override
    public IntegerRange getFluidInputRange() {
        return FLUID_IN_RANGE;
    }

    @Override
    public IntegerRange getFluidOutputRange() {
        return FLUID_OUT_RANGE;
    }

    @Override
    public AwareItemStacksResourceHandler getInternalItemHandler() {
        return itemHandler;
    }

    @Override
    public void serverTick() {
        super.serverTick();


        // Empty
        try (var tx = Transaction.openRoot()) {
            ResourceHandler<FluidResource> itemFluidHandler = ItemAccess.forHandlerIndexStrict(itemHandler, 0).getCapability(Capabilities.Fluid.ITEM);
            if (itemFluidHandler != null) {
                boolean will_be_empty = itemFluidHandler.getAmountAsInt(0) <= FluidType.BUCKET_VOLUME;

                if (will_be_empty) { // item will be empty this time so pre-move it because empty fluid containers aren't valid in slot 0
                    try (Transaction itemTransferTransaction = Transaction.open(tx)) {
                        if (ResourceHandlerUtil.moveStacking(RangedResourceHandler.of(itemHandler, 0, 1), RangedResourceHandler.of(itemHandler, 2, 3), _ -> true, 1, itemTransferTransaction) > 0) itemTransferTransaction.commit();
                    }
                }

                ResourceHandler<FluidResource> itemOutputFluidHandler = ItemAccess.forHandlerIndexStrict(itemHandler, 2).getCapability(Capabilities.Fluid.ITEM);
                try (Transaction fluidItemTransaction = Transaction.open(tx)) {
                    if (ResourceHandlerUtil.moveStacking(will_be_empty ? itemOutputFluidHandler : itemFluidHandler, fluidHandler, _ -> true, FluidType.BUCKET_VOLUME, fluidItemTransaction) > 0) fluidItemTransaction.commit();
                }

                tx.commit();
            }
        }

        // Fill
        try (var tx = Transaction.openRoot()) {
            ResourceHandler<FluidResource> itemFluidHandler = ItemAccess.forHandlerIndexStrict(itemHandler, 1).getCapability(Capabilities.Fluid.ITEM);
            if (itemFluidHandler != null) {
                boolean will_be_full = itemFluidHandler.getAmountAsInt(0) + FluidType.BUCKET_VOLUME >= itemFluidHandler.getCapacityAsInt(0, itemFluidHandler.getResource(0)) || fluidHandler.getResource(0).isEmpty();

                if (will_be_full) { // Full or tank is empty
                    try (Transaction itemTransferTransaction = Transaction.open(tx)) {
                        if (ResourceHandlerUtil.moveStacking(RangedResourceHandler.of(itemHandler, 1, 2), RangedResourceHandler.of(itemHandler, 3, 4), _ -> true, 1, itemTransferTransaction) > 0) itemTransferTransaction.commit();
                    }
                }

                ResourceHandler<FluidResource> itemOutputFluidHandler = ItemAccess.forHandlerIndexStrict(itemHandler, 3).getCapability(Capabilities.Fluid.ITEM);
                try (Transaction fluidItemTransaction = Transaction.open(tx)) {
                    if (ResourceHandlerUtil.moveStacking(fluidHandler, will_be_full ? itemOutputFluidHandler : itemFluidHandler, _ -> true, FluidType.BUCKET_VOLUME, fluidItemTransaction) > 0) fluidItemTransaction.commit();
                }


                tx.commit();
            }
        }
    }

    @Override // TODO move?
    public @Nullable AbstractContainerMenu createMenu(int container_id, Inventory inventory, Player player) {
        return new TankMenu(container_id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(MODID + ".screen.tank");
    }
}