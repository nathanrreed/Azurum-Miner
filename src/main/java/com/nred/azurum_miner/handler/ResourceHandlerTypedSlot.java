package com.nred.azurum_miner.handler;

import com.nred.azurum_miner.menu.SlotsInfo.ItemSlotInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class ResourceHandlerTypedSlot {
    private abstract static class ResourceHandlerBackgroundSlot extends ResourceHandlerSlot {
        public ResourceHandlerBackgroundSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int index, ItemSlotInfo info) {
            super(handler, slotModifier, index, info.x(), info.y());
            if (info.background() != null) {
                setBackground(info.background());
            }
        }
    }

    public static class ResourceHandlerOutputSlot extends ResourceHandlerBackgroundSlot {
        public ResourceHandlerOutputSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int index, ItemSlotInfo info) {
            super(handler, slotModifier, index, info);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    public static class ResourceHandlerInputSlot extends ResourceHandlerBackgroundSlot {
        public ResourceHandlerInputSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int index, ItemSlotInfo info) {
            super(handler, slotModifier, index, info);
        }

        @Override
        public boolean mayPickup(Player player) {
            return true;
        }
    }
}