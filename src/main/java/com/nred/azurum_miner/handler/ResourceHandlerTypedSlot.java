package com.nred.azurum_miner.handler;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class ResourceHandlerTypedSlot {
    public static class ResourceHandlerOutputSlot extends ResourceHandlerSlot {
        public ResourceHandlerOutputSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition) {
            super(handler, slotModifier, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    public static class ResourceHandlerInputSlot extends ResourceHandlerSlot {
        public ResourceHandlerInputSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition) {
            super(handler, slotModifier, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPickup(Player player) {
            return true;
        }
    }
}