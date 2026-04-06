package com.nred.azurum_miner.screen;

import com.nred.azurum_miner.block_entity.TankBlockEntity;
import com.nred.azurum_miner.menu.SlotLookup;
import com.nred.azurum_miner.menu.TankMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static com.nred.azurum_miner.util.Helpers.azLoc;

public class TankScreen extends SidebarScreen<TankBlockEntity, TankMenu> {
    private static final Identifier BG_LOCATION = azLoc("textures/gui/container/tank.png");

    public TankScreen(TankMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, BG_LOCATION, SlotLookup.TANK);
    }
}