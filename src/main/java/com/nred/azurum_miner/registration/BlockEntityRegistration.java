package com.nred.azurum_miner.registration;

import com.nred.azurum_miner.block_entity.TankBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;

import java.util.function.Supplier;

import static com.nred.azurum_miner.registration.BlockRegistration.TANK_BLOCK;
import static com.nred.azurum_miner.registration.Registries.BLOCK_ENTITY_TYPES;

public class BlockEntityRegistration {
    public static final Supplier<BlockEntityType<TankBlockEntity>> TANK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "tank_block_entity", () -> new BlockEntityType<>(TankBlockEntity::new, false, TANK_BLOCK.get())
    );

    public static void register(IEventBus modEventBus) {
        OreRegistration.init();
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}