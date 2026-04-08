package com.nred.azurum_miner.widget.side_bar;

import com.nred.azurum_miner.block_entity.IEnergyBlockEntity;
import com.nred.azurum_miner.block_entity.IFluidBlockEntity;
import com.nred.azurum_miner.block_entity.IItemBlockEntity;
import com.nred.azurum_miner.screen.SidebarScreen;
import com.nred.azurum_miner.widget.ShrinkingStringWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.util.Helpers.getEnergyAmount;
import static com.nred.azurum_miner.util.Helpers.getFluidAmount;

public class TransferInfoWidget extends CollapsableWidget {
    public TransferInfoWidget(BlockEntity blockEntity, SidebarScreen<?, ?> screen) {
        ArrayList<ShrinkingStringWidget> list = new ArrayList<>();

        if (blockEntity instanceof IItemBlockEntity itemBlockEntity) {
            list.add(new ShrinkingStringWidget(Component.translatable(MODID + ".tooltip.side_bar.item_rate_per_t", itemBlockEntity.amountOfItemsToTransfer()), Component.translatable(MODID + ".tooltip.side_bar.item_rate")));
        }
        if (blockEntity instanceof IFluidBlockEntity fluidBlockEntity) {
            list.add(new ShrinkingStringWidget(Component.translatable(MODID + ".tooltip.side_bar.fluid_rate_per_t", getFluidAmount(fluidBlockEntity.amountOfFluidToTransfer())), Component.translatable(MODID + ".tooltip.side_bar.fluid_rate")));
        }
        if (blockEntity instanceof IEnergyBlockEntity energyBlockEntity) {
            list.add(new ShrinkingStringWidget(Component.translatable(MODID + ".tooltip.side_bar.energy_rate_per_t", getEnergyAmount(energyBlockEntity.amountOfEnergyToTransfer())), Component.translatable(MODID + ".tooltip.side_bar.energy_rate")));
        }

        super(screen, SideBarElementType.RATE, list.size() - 1);

        int i = 0;
        for (ShrinkingStringWidget widget : list) {
            LayoutSettings settings;
            if (i == 0) {
                settings = newCellSettings().alignVerticallyBottom().padding(4, 4, 0, 2);
            } else {
                settings = newCellSettings().alignVerticallyTop().padding(4, 2, 0, 2);
            }

            if (i == list.size() - 1) {
                settings.paddingBottom(4);
            }

            this.addChild(widget, i, 0, 1, 2, settings);

            i++;
        }
    }
}