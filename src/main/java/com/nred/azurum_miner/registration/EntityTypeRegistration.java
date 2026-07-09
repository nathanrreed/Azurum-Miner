package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.entity.EmptyMatrixItemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;

import java.util.function.Supplier;

import static com.nred.azurum_miner.registration.Registries.ENTITY_TYPES;

public class EntityTypeRegistration {
    public static final Supplier<EntityType<EmptyMatrixItemEntity>> EMPTY_DIMENSIONAL_MATRIX_TYPE = ENTITY_TYPES.registerEntityType("empty_dimensional_matrix_entity", EmptyMatrixItemEntity::new, MobCategory.MISC, builder -> builder.sized(0.25F, 0.25F).eyeHeight(0.2125F).clientTrackingRange(8));

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}