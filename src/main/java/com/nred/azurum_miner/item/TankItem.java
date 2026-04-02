package com.nred.azurum_miner.item;

import com.nred.azurum_miner.block_entity.TankBlockEntity;
import com.nred.azurum_miner.tooltip.FluidTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.Optional;

import static com.nred.azurum_miner.registration.DataComponentRegistration.SIMPLE_FLUID_COMPONENT;

public class TankItem extends BlockItem {
    public TankItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        return Optional.of(new FluidTooltipComponent(itemStack.getOrDefault(SIMPLE_FLUID_COMPONENT, SimpleFluidContent.EMPTY), TankBlockEntity.CAPACITY));
    }
}