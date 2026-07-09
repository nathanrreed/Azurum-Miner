package com.nred.azurum_miner.widget.side_bar;

import com.nred.azurum_miner.block_entity.IInfoBlockEntity;
import com.nred.azurum_miner.screen.SidebarScreen;
import com.nred.azurum_miner.widget.ScrollingTextWidget;

public class InfoWidget extends CollapsableWidget {
    public InfoWidget(SidebarScreen<?, ?> screen) {
        super(screen, SideBarElementType.INFO);

        this.addChild(new ScrollingTextWidget(0, 0, Math.min(screen.getLeftPos() - 32, 120), Math.min(screen.height - this.getY() - 32, 122), ((IInfoBlockEntity) screen.getMenu().blockEntity).getInfoText(), screen), 0, 0, 1, 2, newCellSettings().padding(2, 2, screen.rightSided ? 0 : -8, 2));
    }
}