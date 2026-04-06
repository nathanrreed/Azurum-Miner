package com.nred.azurum_miner.widget.side_bar;

import com.nred.azurum_miner.screen.SidebarScreen;
import com.nred.azurum_miner.widget.ScrollingTextWidget;
import net.minecraft.network.chat.Component;

import static com.nred.azurum_miner.AzurumMiner.MODID;

public class InfoWidget extends CollapsableWidget {
    public InfoWidget(SidebarScreen<?, ?> screen) {
        super(screen, SideBarElementType.INFO);

        this.addChild(new ScrollingTextWidget(0, 0, Math.min(screen.getGuiLeft() - 32, 120), Math.min(screen.height - this.getY() - 32, 122), Component.translatable(MODID + ".tooltip.tank_info"), screen), 0, 0, 1, 2, newCellSettings().padding(2, 2, screen.rightSided ? 0 : -8, 2));
    }
}