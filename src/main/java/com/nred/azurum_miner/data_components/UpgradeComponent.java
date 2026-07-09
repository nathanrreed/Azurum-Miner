package com.nred.azurum_miner.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nred.azurum_miner.block_entity.UpgradeTableBlockEntity.UpgradeStats;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.fluids.FluidStack;

public record UpgradeComponent(double speed, double energy, FluidStack fluidStack) implements TooltipComponent {
    public UpgradeComponent(UpgradeStats stats, FluidStack fluidStack) {
        this(stats.speed(), stats.energy(), fluidStack);
    }

    public static MapCodec<UpgradeComponent> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Codec.DOUBLE.fieldOf("speed").forGetter(UpgradeComponent::speed),
                    Codec.DOUBLE.fieldOf("energy").forGetter(UpgradeComponent::energy),
                    FluidStack.OPTIONAL_CODEC.fieldOf("fluidStack").forGetter(UpgradeComponent::fluidStack)
            ).apply(inst, UpgradeComponent::new)
    );
    public static StreamCodec<RegistryFriendlyByteBuf, UpgradeComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, UpgradeComponent::speed,
            ByteBufCodecs.DOUBLE, UpgradeComponent::energy,
            FluidStack.OPTIONAL_STREAM_CODEC, UpgradeComponent::fluidStack,
            UpgradeComponent::new
    );
}