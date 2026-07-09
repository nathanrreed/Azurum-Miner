package com.nred.azurum_miner.item;

import com.nred.azurum_miner.tooltip.UpgradeTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

import static com.nred.azurum_miner.registration.DataComponentRegistration.UPGRADE_COMPONENT;

public class UpgradeItem extends Item {
    public UpgradeItem(Properties properties) {
        super(properties);
    }

    /**
     * Power: Extra power required by machine
     * Speed: How much faster than usual
     * Complexity: Extra inputs needed to create
     */

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        if (itemStack.get(UPGRADE_COMPONENT) == null) {
            return Optional.empty();
        }
        return Optional.of(new UpgradeTooltipComponent(itemStack.get(UPGRADE_COMPONENT)));
    }
}