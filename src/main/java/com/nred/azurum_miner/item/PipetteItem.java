package com.nred.azurum_miner.item;

import com.nred.azurum_miner.tooltip.FluidTooltipComponent;
import com.nred.azurum_miner.util.Helpers;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.Optional;

import static com.nred.azurum_miner.registration.DataComponentRegistration.SIMPLE_FLUID_COMPONENT;

public class PipetteItem extends Item {
    public PipetteItem(Properties properties) {
        super(properties);
    }

    public static final int CAPACITY = 8000;

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !stack.getOrDefault(SIMPLE_FLUID_COMPONENT, SimpleFluidContent.EMPTY).isEmpty();
    }

    @Override
    public int getBarColor(ItemStack stack) {
        Fluid fluid = stack.get(SIMPLE_FLUID_COMPONENT).getFluid();
        return Helpers.getColorTint(fluid, 0xFFFF3000);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Mth.clamp(Math.round((float) stack.get(SIMPLE_FLUID_COMPONENT).getAmount() * (float) MAX_BAR_WIDTH / (float) CAPACITY), 0, MAX_BAR_WIDTH);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        return Optional.of(new FluidTooltipComponent(itemStack.getOrDefault(SIMPLE_FLUID_COMPONENT, SimpleFluidContent.EMPTY), CAPACITY));
    }
}