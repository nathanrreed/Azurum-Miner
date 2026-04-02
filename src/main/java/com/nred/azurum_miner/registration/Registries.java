package com.nred.azurum_miner.registration;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static net.minecraft.core.registries.Registries.*;

public class Registries {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(MENU, MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(DATA_COMPONENT_TYPE, MODID);

    public static void register(IEventBus modEventBus) {
        BlockRegistration.register(modEventBus);
        ItemRegistration.register(modEventBus);
        CreativeTabRegistration.register(modEventBus);
        AttachmentRegistration.register(modEventBus);
        BlockEntityRegistration.register(modEventBus);
        MenuRegistration.register(modEventBus);
        DataComponentRegistration.register(modEventBus);
    }
}