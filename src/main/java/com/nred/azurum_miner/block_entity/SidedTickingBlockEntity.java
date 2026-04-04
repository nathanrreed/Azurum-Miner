package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.handler.ResourceHandlerSideMode;
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
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        if (this instanceof IFluidBlockEntity fluidBlockEntity) {
            List<SimpleFluidContent> fluidContent = components.getOrDefault(FLUID_COMPONENT.get(), components.has(SIMPLE_FLUID_COMPONENT.get()) ? List.of(components.get(SIMPLE_FLUID_COMPONENT.get())) : List.of());
            for (int i = 0; i < fluidContent.size(); i++) {
                fluidBlockEntity.getFluidHandler(null).set(i, FluidResource.of(fluidContent.get(i).getFluid()), fluidContent.get(i).getAmount());
            }
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (this instanceof IFluidBlockEntity fluidBlockEntity) {
            FluidStacksResourceHandler fluidHandler = fluidBlockEntity.getFluidHandler(null);
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

    public void setSideMode(Direction side, ResourceHandlerSideMode mode, boolean isFluid) {
        (isFluid ? fluidSideModes : itemSideModes).replace(side, mode);
        setChanged();
    }

    public Map<Direction, ResourceHandlerSideMode> getSideModes(boolean isFluid) {
        return (isFluid ? fluidSideModes : itemSideModes);
    }

    public void setSideModes(Map<Direction, ResourceHandlerSideMode> sideModeMap, boolean isFluid) {
        (isFluid ? fluidSideModes : itemSideModes).putAll(sideModeMap);
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
            fluidBlockEntity.getFluidHandler(null).deserialize(input);
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
            fluidBlockEntity.getFluidHandler(null).serialize(output);
            output.store("fluid_side_modes", CODEC.codec(), fluidSideModes);
        }

        if (this instanceof IItemBlockEntity) {
            output.store("item_side_modes", CODEC.codec(), itemSideModes);
        }
    }
}