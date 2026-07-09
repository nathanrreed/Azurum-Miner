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
        addBlock(TANK_BLOCK, "Tank");
        addBlock(UPGRADE_TABLE_BLOCK, "Upgrade Table");
    }

    private void items() {
        addItem(SIMPLE_VOID_PROCESSOR, "Simple Void Processor");
        addItem(VOID_PROCESSOR, "Void Processor");
        addItem(ELABORATE_VOID_PROCESSOR, "Elaborate Void Processor");
        addItem(COMPLEX_VOID_PROCESSOR, "Complex Void Processor");

        addItem(SIMPLE_UPGRADE, "Simple Machine Upgrade");
        addItem(UPGRADE, "Circuit Machine Upgrade");
        addItem(ELABORATE_UPGRADE, "Elaborate Machine Upgrade");
        addItem(COMPLEX_UPGRADE, "Complex Machine Upgrade");

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

        add(MODID + ".tooltip.side_bar.item", "Item Sides Config");
        add(MODID + ".tooltip.side_bar.fluid", "Fluid Sides Config");
        add(MODID + ".tooltip.side_bar.energy", "Energy Sides Config");
        add(MODID + ".tooltip.side_bar.info", "Additional Information");
        add(MODID + ".tooltip.side_bar.rate", "Transfer Rates");
        add(MODID + ".tooltip.side_bar.stats", "Statistics");


        add(MODID + ".tooltip.side_bar.item_rate", "Item Transfer Rate");
        add(MODID + ".tooltip.side_bar.fluid_rate", "Fluid Transfer Rate");
        add(MODID + ".tooltip.side_bar.energy_rate", "Energy Transfer Rate");

        add(MODID + ".tooltip.side_bar.item_rate_per_t", "I: %s/t");
        add(MODID + ".tooltip.side_bar.fluid_rate_per_t", "F: %s/t");
        add(MODID + ".tooltip.side_bar.energy_rate_per_t", "E: %s/t");

        add(MODID + ".tooltip.tank_info", "Left click with a fluid container item on the tank to fill it\n\nRight click with a fluid container item on the tank to empty it\n\nShift + Left click on the tank to empty it");

        add(MODID + ".tooltip.side.input", "Input");
        add(MODID + ".tooltip.side.output", "Output");
        add(MODID + ".tooltip.side.auto_output", "Auto Output");
        add(MODID + ".tooltip.side.input_output", "Input Output");
        add(MODID + ".tooltip.side.none", "None");
        add(MODID + ".tooltip.side.edit", "Enter Batch Edit mode (Changes won't be applied until they are saved)");
        add(MODID + ".tooltip.side.save", "Save Changes");

        upgrades();
    }

    private void upgrades() {
        add(MODID + ".tooltip.upgrade.speed", "Speed: %s");
        add(MODID + ".tooltip.upgrade.energy", "Energy: %s");
        add(MODID + ".tooltip.upgrade.coolant", "Coolant: %s");
        add(MODID + ".tooltip.upgrade.complexity", "Complexity: %s");
        add(MODID + ".tooltip.upgrade.max_complexity", "Max Complexity: %s");
        add(MODID + ".tooltip.upgrade.heat", "Heat: %s");
        add(MODID + ".tooltip.upgrade.max_heat", "Max Heat: %s");

        add(MODID + ".tooltip.upgrade.stat_speed", "S: %s");
        add(MODID + ".tooltip.upgrade.stat_energy", "E: %s");
        add(MODID + ".tooltip.upgrade.stat_complexity", "C: %s/%s");
        add(MODID + ".tooltip.upgrade.stat_heat", "H: %s/%s");

        add(MODID + ".tooltip.upgrade.speed.desc", "Speed: how much faster the machine using the upgrade will function");
        add(MODID + ".tooltip.upgrade.energy.desc", "Energy: how much more energy the machine using the upgrade will need function");
        add(MODID + ".tooltip.upgrade.heat.desc", "Heat: a constraint");
        add(MODID + ".tooltip.upgrade.complexity.desc", "Complexity: a constraint as well as the multiple for what is needed to craft the upgrade");

        add(MODID + ".tooltip.upgrade.card_type_data", "%s\n%s");
        add(MODID + ".tooltip.upgrade.coolant_type_data", "%s\n%s\n%s\n%s");
        add(MODID + ".tooltip.upgrade.modifier_type_data", "%s\n%s\n%s\n%s\n%s");

        add(MODID + ".tooltip.upgrade.card_type", "Card Type");
        add(MODID + ".tooltip.upgrade.card_type.desc", "Sets the complexity of the upgrade");
        add(MODID + ".tooltip.upgrade.coolant_type", "Coolant Type");
        add(MODID + ".tooltip.upgrade.coolant_type.desc", "Sets the type of coolant needed for a machine using this upgrade to run (Air is non)");
        add(MODID + ".tooltip.upgrade.modifier_type", "Modifiers");
        add(MODID + ".tooltip.upgrade.modifier_type.desc", "Modifier addons to improve the upgrade");

        add(MODID + ".tooltip.upgrade_table_info", "Select the parts to use in the upgrade, each have their own pros and cons.\n\nThen Assemble and add the required items to create the upgrade");

        add(MODID + ".tooltip.upgrade_table.assemble", "Assemble");
        add(MODID + ".tooltip.upgrade_table.error.missing_items", "Missing items: ");
        add(MODID + ".tooltip.upgrade_table.error.no_modifiers", "No modifiers selected, press next to see the available options");
        add(MODID + ".tooltip.upgrade_table.error.no_space", "Output full");
        add(MODID + ".tooltip.upgrade_table.error.too_much_heat", "Heat is higher than Max Heat");
        add(MODID + ".tooltip.upgrade_table.error.too_much_complexity", "Complexity is higher than Max Complexity");
    }

    private void screens() {
        add(MODID + ".screen.tank", "Tank");
        add(MODID + ".screen.upgrade_table", "Upgrade Table");
    }

    private void config() {
        add(MODID + ".configuration.colours", "Color Settings");
        add(MODID + ".configuration.colours.tooltip", "Settings for colors used through the mod to help with color differentiation");
        add(MODID + ".configuration.energy_colour", "Energy Color");
        add(MODID + ".configuration.energy_colour.tooltip", "Default color used for energy info in RGB");
        add(MODID + ".configuration.speed_colour", "Speed Color");
        add(MODID + ".configuration.speed_colour.tooltip", "Default color used for speed info in RGB");

        add(MODID + ".configuration.fluid_colour", "Fluid Color");
        add(MODID + ".configuration.fluid_colour.tooltip", "Default color used for isFluid info in RGB");
        add(MODID + ".configuration.capacity_colour", "Capacity Color");
        add(MODID + ".configuration.capacity_colour.tooltip", "Default color used for capacity info in RGB");

        add(MODID + ".configuration.units", "Units Settings");
        add(MODID + ".configuration.use_buckets", "Use Bucket Units");
        add(MODID + ".configuration.use_buckets.tooltip", "Use Buckets instead of mB");

        add(MODID + ".configuration.side_bar", "Sidebar Settings");
        add(MODID + ".configuration.allow_multiple_open", "Allow Multiple Open");
        add(MODID + ".configuration.allow_multiple_open.tooltip", "Allows multiple sidebar elements to be open at the same time");
        add(MODID + ".configuration.show_transfer_rates", "Show Transfer Rates");
        add(MODID + ".configuration.show_transfer_rates.tooltip", "Adds Transfer Rates Element to the sidebar");
        add(MODID + ".configuration.use_right_side", "Right Sided Sidebar");
        add(MODID + ".configuration.use_right_side.tooltip", "Places the sidebar on the right side");

        add(MODID + ".configuration.upgrades", "Upgrade Settings");
        add(MODID + ".configuration.coolant_mb_per_tick", "Coolant usage");
        add(MODID + ".configuration.coolant_mb_per_tick.tooltip", "Amount of coolant in mB needed per tick to operate the machine with the upgrade");
    }
}