package com.nred.azurum_miner.registration;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.nred.azurum_miner.registration.Registries.DATA_COMPONENT_TYPES;

public class DataComponentRegistration {
    public static final Supplier<DataComponentType<SimpleFluidContent>> SIMPLE_FLUID_COMPONENT = DATA_COMPONENT_TYPES.registerComponentType(
            "simple_fluid_component",
            builder -> builder
                    .persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<SimpleFluidContent>> SIMPLE_FLUID_CONTENT_LIST_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull List<SimpleFluidContent> decode(RegistryFriendlyByteBuf buffer) {
            return Arrays.stream(buffer.readArray(SimpleFluidContent[]::new, buf -> SimpleFluidContent.STREAM_CODEC.decode(new RegistryFriendlyByteBuf(buf, buffer.registryAccess(), ConnectionType.NEOFORGE)))).toList();
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, List<SimpleFluidContent> value) {
            buffer.writeArray(value.toArray(), (buf, ing) -> SimpleFluidContent.STREAM_CODEC.encode(new RegistryFriendlyByteBuf(buf, buffer.registryAccess(), ConnectionType.NEOFORGE), ((SimpleFluidContent) ing)));
        }
    };

    public static final Supplier<DataComponentType<List<SimpleFluidContent>>> FLUID_COMPONENT = DATA_COMPONENT_TYPES.registerComponentType(
            "fluid_component",
            builder -> builder
                    .persistent(SimpleFluidContent.CODEC.listOf())
                    .networkSynchronized(SIMPLE_FLUID_CONTENT_LIST_STREAM_CODEC)
    );

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    }
}