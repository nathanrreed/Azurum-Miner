package com.nred.azurum_miner.block_entity;

import com.nred.azurum_miner.data_components.UpgradeComponent;
import com.nred.azurum_miner.handler.AwareItemStacksResourceHandler;
import com.nred.azurum_miner.menu.UpgradeTableMenu;
import com.nred.azurum_miner.util.DataMapUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;
import oshi.util.tuples.Triplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.registration.BlockEntityRegistration.UPGRADE_TABLE_BLOCK_ENTITY;
import static com.nred.azurum_miner.registration.DataComponentRegistration.UPGRADE_COMPONENT;
import static com.nred.azurum_miner.registration.DataMapRegistration.*;
import static com.nred.azurum_miner.registration.ItemRegistration.SIMPLE_UPGRADE;

public class UpgradeTableBlockEntity extends TickingBlockEntity implements IInfoBlockEntity {
    public UpgradeTableBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(UPGRADE_TABLE_BLOCK_ENTITY.get(), worldPosition, blockState);

        modifiers = getDefaultModifiers();
    }

    public ItemStack card = SIMPLE_UPGRADE.get().getDefaultInstance();
    public FluidStack coolant = new FluidStack(Fluids.EMPTY, 1);
    public final Map<Item, Integer> modifiers;

    private Map<Item, Integer> getDefaultModifiers() {
        Map<Item, Integer> modifiers = new HashMap<>();
        for (Item item : DataMapUtil.dataMapItems(MODIFIER_TYPE_DATA)) {
            modifiers.put(item, 0);
        }

        return modifiers;
    }

    public AwareItemStacksResourceHandler itemHandler = new AwareItemStacksResourceHandler(1, this);

    @Override
    public void serverTick() {
        super.serverTick();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int container_id, Inventory inventory, Player player) {
        return new UpgradeTableMenu(container_id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(MODID + ".screen.upgrade_table");
    }

    public UpgradeStats calculateStats(CardTypeData cardTypeData, CoolantTypeData coolantTypeData, Map<Item, ModifierTypeData> modifierTypeDataList) {
        double speed = coolantTypeData.speed();
        double energy = coolantTypeData.energy();
        int complexity = 0;
        int max_complexity = cardTypeData.max_complexity();
        int heat = 0;
        int max_heat = coolantTypeData.max_heat();

        for (Map.Entry<Item, ModifierTypeData> modifierTypeData : modifierTypeDataList.entrySet()) {
            speed += modifierTypeData.getValue().speed() * modifiers.get(modifierTypeData.getKey());
            energy += modifierTypeData.getValue().energy() * modifiers.get(modifierTypeData.getKey());
            complexity += modifierTypeData.getValue().complexity() * modifiers.get(modifierTypeData.getKey());
            heat += modifierTypeData.getValue().heat() * modifiers.get(modifierTypeData.getKey());
        }

        return new UpgradeStats(speed, energy, complexity, max_complexity, heat, max_heat);
    }

    public Triplet<List<ItemStack>, Boolean, Boolean> hasErrors(Player player) {
        UpgradeStats stats = getStats();
        ArrayList<ItemStack> list = new ArrayList<>();
        if (!player.getInventory().contains(card)) {
            list.add(card);
        }

        for (Map.Entry<Item, Integer> modifier : modifiers.entrySet()) {
            int count = 0;
            for (ItemStack itemStack : player.getInventory()) {
                if (!itemStack.isEmpty() && itemStack.is(modifier.getKey())) {
                    count += itemStack.getCount();
                }
            }
            int amount = Mth.floor(modifier.getValue() * (stats.complexity / 10.0));
            if (count < amount) {
                list.add(new ItemStack(modifier.getKey(), amount - count));
            }
        }

        return new Triplet<>(list, stats.max_heat < stats.heat, stats.max_complexity < stats.complexity);
    }

    public UpgradeStats getStats() {
        HashMap<Item, ModifierTypeData> modifiersData = new HashMap<>();
        modifiers.keySet().forEach(item -> {
            modifiersData.put(item, DataMapUtil.dataMapItem(MODIFIER_TYPE_DATA, item));
        });
        return calculateStats(DataMapUtil.dataMapItem(CARD_TYPE_DATA, card.getItem()), DataMapUtil.dataMapFluid(COOLING_TYPE_DATA, coolant.getFluid()), modifiersData);
    }

    public void createOutput(Player player) {
        try (Transaction tx = Transaction.openRoot()) {
            UpgradeStats stats = getStats();
            itemHandler.insert(ItemResource.of(card.getItem(), DataComponentPatch.builder().set(UPGRADE_COMPONENT.get(), new UpgradeComponent(stats, coolant)).build()), 1, tx);

            var hasErrors = hasErrors(player);
            if (!hasErrors.getA().isEmpty() || hasErrors.getB() || hasErrors.getC()) {
                return;
            }

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                if (ItemStack.isSameItemSameComponents(player.getInventory().getItem(i), card)) {
                    player.getInventory().removeItem(i, 1);
                }
            }

            for (Map.Entry<Item, Integer> modifier : modifiers.entrySet()) {
                int count = Mth.floor(modifier.getValue() * (stats.complexity / 10.0));
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack itemStack = player.getInventory().getItem(i);
                    if (!itemStack.isEmpty() && itemStack.is(modifier.getKey())) {
                        ItemStack result = player.getInventory().removeItem(i, count);
                        if (result.getCount() < count) {
                            count -= result.getCount();
                        } else {
                            break;
                        }
                    }
                }
            }
            tx.commit();
        }

        setChanged();
    }

    @Override
    public Component getInfoText() {
        return Component.translatable(MODID + ".tooltip.upgrade_table_info");
    }

    public record UpgradeStats(double speed, double energy, int complexity, int max_complexity, int heat, int max_heat) {
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemHandler.serialize(output);
        output.store("card", ItemStack.OPTIONAL_CODEC, card);
        output.store("coolant", FluidStack.OPTIONAL_CODEC, coolant);
        output.store("modifiers", ItemStack.OPTIONAL_CODEC.listOf(), modifiers.entrySet().stream().filter(e -> e.getValue() > 0).map(e -> new ItemStack(e.getKey(), e.getValue())).toList());
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        itemHandler.deserialize(input);
        card = input.read("card", ItemStack.OPTIONAL_CODEC).orElse(SIMPLE_UPGRADE.get().getDefaultInstance());
        coolant = input.read("coolant", FluidStack.OPTIONAL_CODEC).orElse(new FluidStack(Fluids.EMPTY, 1));
        modifiers.putAll(input.read("modifiers", ItemStack.OPTIONAL_CODEC.listOf()).orElse(List.of()).stream().collect(Collectors.toMap(ItemStack::getItem, ItemStack::getCount)));
    }
}