package com.nred.azurum_miner.registration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.config.ClientConfig.*;
import static com.nred.azurum_miner.util.Helpers.azLoc;
import static com.nred.azurum_miner.util.Helpers.getPercentage;
import static net.minecraft.core.registries.Registries.FLUID;
import static net.minecraft.core.registries.Registries.ITEM;

@EventBusSubscriber
public class DataMapRegistration {
    public static final DataMapType<Item, CardTypeData> CARD_TYPE_DATA = DataMapType.builder(azLoc("card_type"), ITEM, CardTypeData.CODEC).synced(CardTypeData.CODEC, true).build();
    public static final DataMapType<Fluid, CoolantTypeData> COOLING_TYPE_DATA = DataMapType.builder(azLoc("cooling_type"), FLUID, CoolantTypeData.CODEC).synced(CoolantTypeData.CODEC, true).build();
    public static final DataMapType<Item, ModifierTypeData> MODIFIER_TYPE_DATA = DataMapType.builder(azLoc("modifier_type"), ITEM, ModifierTypeData.CODEC).synced(ModifierTypeData.CODEC, true).build();

    public record CardTypeData(int max_complexity) implements TooltipDataMap<Item> {
        public static final Codec<CardTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("max_complexity").forGetter(CardTypeData::max_complexity)
        ).apply(instance, CardTypeData::new));

        @Override
        public Tooltip tooltip(Item element) {
            return Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade.card_type_data",
                    Component.translatable(element.getDescriptionId()),
                    Component.translatable(MODID + ".tooltip.upgrade.max_complexity", "§f" + max_complexity).withColor(COMPLEXITY_COLOUR.get())
            ));
        }
    }

    public record CoolantTypeData(int max_heat, double speed, double energy) implements TooltipDataMap<Fluid> {
        public CoolantTypeData(int max_heat) {
            this(max_heat, 0, 0);
        }

        public static final Codec<CoolantTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("max_heat").forGetter(CoolantTypeData::max_heat),
                Codec.DOUBLE.optionalFieldOf("speed", 0.0).forGetter(CoolantTypeData::speed),
                Codec.DOUBLE.optionalFieldOf("energy", 0.0).forGetter(CoolantTypeData::energy)
        ).apply(instance, CoolantTypeData::new));

        @Override
        public Tooltip tooltip(Fluid element) {
            return Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade.coolant_type_data",
                    Component.translatable(element.getFluidType().getDescriptionId()),
                    Component.translatable(MODID + ".tooltip.upgrade.max_heat", "§f" + max_heat).withColor(HEAT_COLOUR.get()),
                    Component.translatable(MODID + ".tooltip.upgrade.speed", getPercentage(speed)).withColor(SPEED_COLOUR.get()),
                    Component.translatable(MODID + ".tooltip.upgrade.energy", getPercentage(energy)).withColor(ENERGY_COLOUR.get())
            ));
        }
    }

    public record ModifierTypeData(int complexity, int heat, double speed, double energy, int max_count) implements TooltipDataMap<Item> {
        public ModifierTypeData(int complexity, int heat, double speed, double energy) {
            this(complexity, heat, speed, energy, 64);
        }

        public static final Codec<ModifierTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("complexity").forGetter(ModifierTypeData::complexity),
                Codec.INT.fieldOf("heat").forGetter(ModifierTypeData::heat),
                Codec.DOUBLE.fieldOf("speed").forGetter(ModifierTypeData::speed),
                Codec.DOUBLE.fieldOf("energy").forGetter(ModifierTypeData::energy),
                Codec.INT.optionalFieldOf("max_count", 64).forGetter(ModifierTypeData::max_count)
        ).apply(instance, ModifierTypeData::new));

        @Override
        public Tooltip tooltip(Item element) {
            return Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade.modifier_type_data",
                    Component.translatable(element.getDescriptionId()),
                    Component.translatable(MODID + ".tooltip.upgrade.complexity", "§f" + complexity).withColor(COMPLEXITY_COLOUR.get()),
                    Component.translatable(MODID + ".tooltip.upgrade.heat", "§f" + heat).withColor(HEAT_COLOUR.get()),
                    Component.translatable(MODID + ".tooltip.upgrade.speed", getPercentage(speed)).withColor(SPEED_COLOUR.get()),
                    Component.translatable(MODID + ".tooltip.upgrade.energy", getPercentage(energy)).withColor(ENERGY_COLOUR.get())
            ));
        }
    }

    public interface TooltipDataMap<T> {
        Tooltip tooltip(T element);
    }

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(CARD_TYPE_DATA);
        event.register(COOLING_TYPE_DATA);
        event.register(MODIFIER_TYPE_DATA);
    }
}