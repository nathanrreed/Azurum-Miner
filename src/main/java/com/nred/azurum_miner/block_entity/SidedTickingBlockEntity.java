package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
import com.nred.azurum_miner.widget.side_mode.SideModeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.nred.azurum_miner.block_entity.ISidedBlockEntity.CODEC;
import static com.nred.azurum_miner.registration.DataComponentRegistration.FLUID_COMPONENT;
import static com.nred.azurum_miner.registration.DataComponentRegistration.SIMPLE_FLUID_COMPONENT;

public abstract class SidedTickingBlockEntity extends TickingBlockEntity {
    public final Map<Direction, ResourceHandlerSideMode> itemSideModes;
    public final Map<Direction, ResourceHandlerSideMode> fluidSideModes;
    public final Map<Direction, ResourceHandlerSideMode> energySideModes;

    public SidedTickingBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        if (this instanceof IItemBlockEntity) {
            itemSideModes = ResourceHandlerSideMode.getDefault();
        } else {
            itemSideModes = null;
        }
        if (this instanceof IFluidBlockEntity) {
            fluidSideModes = ResourceHandlerSideMode.getDefault();
        } else {
            fluidSideModes = null;
        }
        if (this instanceof IEnergyBlockEntity) {
            energySideModes = ResourceHandlerSideMode.getDefaultEnergy();
        } else {
            energySideModes = null;
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        if (this instanceof IFluidBlockEntity fluidBlockEntity) {
            List<SimpleFluidContent> fluidContent = components.getOrDefault(FLUID_COMPONENT.get(), components.has(SIMPLE_FLUID_COMPONENT.get()) ? List.of(components.get(SIMPLE_FLUID_COMPONENT.get())) : List.of());
            for (int i = 0; i < fluidContent.size(); i++) {
                fluidBlockEntity.getInternalFluidHandler().set(i, FluidResource.of(fluidContent.get(i).getFluid()), fluidContent.get(i).getAmount());
            }
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (this instanceof IFluidBlockEntity fluidBlockEntity) {
            FluidStacksResourceHandler fluidHandler = fluidBlockEntity.getInternalFluidHandler();
            ArrayList<SimpleFluidContent> fluidContents = new ArrayList<>(fluidHandler.size());
            for (int i = 0; i < fluidHandler.size(); i++) {
                fluidContents.add(SimpleFluidContent.copyOf(fluidHandler.getResource(i).toStack(fluidHandler.getAmountAsInt(i))));
            }

            if (fluidHandler.size() == 1) {
                components.set(SIMPLE_FLUID_COMPONENT.get(), fluidContents.getFirst());
            } else {
                components.set(FLUID_COMPONENT.get(), fluidContents);
            }
        }
    }

    public ResourceHandlerSideMode getSideItemMode(Direction side) {
        return itemSideModes.get(side);
    }

    public ResourceHandlerSideMode getSideFluidMode(Direction side) {
        return fluidSideModes.get(side);
    }

    public void setSideMode(Direction side, ResourceHandlerSideMode mode, SideModeType sideModeType) {
        switch (sideModeType) {
            case ITEM -> itemSideModes.replace(side, mode);
            case FLUID -> fluidSideModes.replace(side, mode);
            case ENERGY -> energySideModes.replace(side, mode);
        }
        setChanged();
    }

    public Map<Direction, ResourceHandlerSideMode> getSideModes(SideModeType sideModeType) {
        return switch (sideModeType) {
            case ITEM -> itemSideModes;
            case FLUID -> fluidSideModes;
            case ENERGY -> energySideModes;
        };
    }

    public void setSideModes(Map<Direction, ResourceHandlerSideMode> sideModeMap, SideModeType sideModeType) {
        switch (sideModeType) {
            case ITEM -> itemSideModes.putAll(sideModeMap);
            case FLUID -> fluidSideModes.putAll(sideModeMap);
            case ENERGY -> energySideModes.putAll(sideModeMap);
        }
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        if (this instanceof IFluidBlockEntity fluidBlockEntity) {
            fluidBlockEntity.getInternalFluidHandler().deserialize(input);
            fluidSideModes.putAll(input.read("fluid_side_modes", CODEC.codec()).get());
        }

        if (this instanceof IItemBlockEntity) {
            itemSideModes.putAll(input.read("item_side_modes", CODEC.codec()).get());
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (this instanceof IFluidBlockEntity fluidBlockEntity) {
            fluidBlockEntity.getInternalFluidHandler().serialize(output);
            output.store("fluid_side_modes", CODEC.codec(), fluidSideModes);
        }

        if (this instanceof IItemBlockEntity) {
            output.store("item_side_modes", CODEC.codec(), itemSideModes);
        }
    }
}