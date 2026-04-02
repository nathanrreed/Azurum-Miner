package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.handler.RangedFluidStacksResourceHandler;
import com.nred.azurum_miner.handler.ResourceHandlerDirectionMode;
import com.nred.azurum_miner.menu.TankMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import org.apache.commons.lang3.IntegerRange;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.registration.BlockEntityRegistration.TANK_BLOCK_ENTITY;

public class TankBlockEntity extends TickingBlockEntity implements IFluidBlockEntity {
    public TankBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(TANK_BLOCK_ENTITY.get(), worldPosition, blockState);
    }

    public static final int CAPACITY = 32000;
    public static final IntegerRange IN_OUT_RANGE = IntegerRange.of(0, 0);
    protected FluidStacksResourceHandler fluidHandler = new RangedFluidStacksResourceHandler(1, CAPACITY, IN_OUT_RANGE, IN_OUT_RANGE, this);
    protected Map<Direction, ResourceHandlerDirectionMode> sideModes = ResourceHandlerDirectionMode.getDefault();

    @Override
    public FluidStacksResourceHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public ResourceHandlerDirectionMode getSideMode(Direction side) {
        return sideModes.get(side);
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