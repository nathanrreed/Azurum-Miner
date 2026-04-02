package com.nred.azurum_miner.registration;

import net.neoforged.bus.api.IEventBus;

import static com.nred.azurum_miner.registration.Registries.ATTACHMENT_TYPES;

public class AttachmentRegistration {
// TODO    public static final Supplier<AttachmentType<EssenceCapability>> ESSENCES = ATTACHMENT_TYPES.register("essences", () -> AttachmentType.builder(EssenceCapability::new).serialize(EssenceCapability.CODEC).build());

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }
}