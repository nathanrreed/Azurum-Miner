package com.nred.azurum_miner.registration;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static com.nred.azurum_miner.util.Helpers.azLoc;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> SHARD = ItemTags.create(azLoc("shard"));
    }
}