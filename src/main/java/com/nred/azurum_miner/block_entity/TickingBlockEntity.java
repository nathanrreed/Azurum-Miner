package com.nred.azurum_miner.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.nred.azurum_miner.registration.DataComponentRegistration.FLUID_COMPONENT;
import static com.nred.azurum_miner.registration.DataComponentRegistration.SIMPLE_FLUID_COMPONENT;

public abstract class TickingBlockEntity extends BlockEntity implements ITickingBlockEntity, MenuProvider { // TODO BaseContainerBlockEntity implements WorldlyContainer
    public TickingBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        TagValueOutput tagValueOutput = TagValueOutput.createWithoutContext(new ProblemReporter.Collector());
        saveAdditional(tagValueOutput);
        return tagValueOutput.buildResult();
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        if (this instanceof IFluidBlockEntity fluidBlockEntity) {
            fluidBlockEntity.getFluidHandler(null).deserialize(input);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (this instanceof IFluidBlockEntity fluidBlockEntity) {
            fluidBlockEntity.getFluidHandler(null).serialize(output);
        }
    }
}