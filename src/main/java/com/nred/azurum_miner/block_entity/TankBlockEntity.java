package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.handler.RangedFluidStacksResourceHandler;
import com.nred.azurum_miner.handler.RangedItemStacksResourceHandler;
import com.nred.azurum_miner.menu.TankMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.IntegerRange;
import org.jspecify.annotations.Nullable;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.registration.BlockEntityRegistration.TANK_BLOCK_ENTITY;

public class TankBlockEntity extends SidedTickingBlockEntity implements IItemBlockEntity, IFluidBlockEntity {
    public TankBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(TANK_BLOCK_ENTITY.get(), worldPosition, blockState);
    }

    public static final int CAPACITY = 32000;
    public static final IntegerRange FLUID_IN_OUT_RANGE = IntegerRange.of(0, 0);
    public static final IntegerRange ITEM_IN_RANGE = IntegerRange.of(0, 0);
    public static final IntegerRange ITEM_OUT_RANGE = IntegerRange.of(1, 1);
    protected RangedFluidStacksResourceHandler fluidHandler = new RangedFluidStacksResourceHandler(1, CAPACITY, FLUID_IN_OUT_RANGE, FLUID_IN_OUT_RANGE, this);
    protected RangedItemStacksResourceHandler itemHandler = new RangedItemStacksResourceHandler(ITEM_IN_RANGE, ITEM_OUT_RANGE, this);

    @Override
    public RangedFluidStacksResourceHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public RangedItemStacksResourceHandler getItemHandler() {
        return itemHandler;
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