package com.nred.azurum_miner.datagen;

import com.nred.azurum_miner.util.OreMaterial;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.registration.BlockRegistration.*;
import static com.nred.azurum_miner.registration.ItemRegistration.*;
import static com.nred.azurum_miner.registration.OreRegistration.ORE_MATERIALS;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.azurum_miner", "Azurum Miner");

        config();
        blocks();
        items();
        tooltips();
        screens();

        for (OreMaterial oreMaterial : ORE_MATERIALS) {
            oreMaterial.addTranslations(this::addBlock, this::addItem);
        }
    }

    private void blocks() {
        addBlock(CONGLOMERATE_OF_ORE, "Conglomerate of Ores");
        addBlock(CONGLOMERATE_OF_ORE_BLOCK, "Conglomerate of Ores Block");
        addBlock(ENERGIZED_OBSIDIAN, "Energized Obsidian");
        addBlock(TANK_BLOCK, "Tank"); // TODO
    }

    private void items() {
        addItem(SIMPLE_VOID_PROCESSOR, "Simple Void Processor");
        addItem(VOID_PROCESSOR, "Void Processor");
        addItem(ELABORATE_VOID_PROCESSOR, "Elaborate Void Processor");
        addItem(COMPLEX_VOID_PROCESSOR, "Complex Void Processor");

        addItem(PIPETTE, "Pipette");

        addItem(CONGLOMERATE_OF_ORE_SHARD, "Conglomerate of Ores Shard");
        addItem(NETHER_DIAMOND, "Nether Infused Diamond");
        addItem(ENDER_DIAMOND, "Ender Infused Diamond");
        addItem(ENERGIZED_SHARD, "Energized Shard");
        addItem(VOID_SHARD, "Void Shard");
        addItem(DIMENSIONAL_MATRIX, "Dimensional Matrix");
        addItem(EMPTY_DIMENSIONAL_MATRIX, "Empty Dimensional Matrix");
        addItem(SEED_CRYSTAL, "Seed Crystal");
    }

    private void tooltips() {
        add(MODID + ".tooltip.fluid_detail", "%s: %s");
        add(MODID + ".tooltip.fluid_capacity", "Capacity: %s");
        add(MODID + ".tooltip.fluid_empty", "Empty");
    }

    private void screens() {
        add(MODID + ".screen.tank", "Tank");
    }

    private void config() {
        add(MODID + ".configuration.colours", "Color Settings");
        add(MODID + ".configuration.colours.tooltip", "Settings for colors used through the mod to help with color differentiation");
        add(MODID + ".configuration.energy_colour", "Energy Color");
        add(MODID + ".configuration.energy_colour.tooltip", "Default color used for energy info in RGB");
        add(MODID + ".configuration.speed_colour", "Speed Color");
        add(MODID + ".configuration.speed_colour.tooltip", "Default color used for speed info in RGB");

        add(MODID + ".configuration.fluid_colour", "Fluid Color");
        add(MODID + ".configuration.fluid_colour.tooltip", "Default color used for fluid info in RGB");
        add(MODID + ".configuration.capacity_colour", "Capacity Color");
        add(MODID + ".configuration.capacity_colour.tooltip", "Default color used for capacity info in RGB");

        add(MODID + ".configuration.units", "Units Settings");
        add(MODID + ".configuration.use_buckets", "Use Bucket Units");
        add(MODID + ".configuration.use_buckets.tooltip", "Use Buckets instead of mB");
    }
}