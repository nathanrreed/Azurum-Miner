package com.nred.azurum_miner.screen;

import com.nred.azurum_miner.block_entity.UpgradeTableBlockEntity;
import com.nred.azurum_miner.block_entity.UpgradeTableBlockEntity.UpgradeStats;
import com.nred.azurum_miner.menu.UpgradeTableMenu;
import com.nred.azurum_miner.network.UpgradeTableSelectPayload;
import com.nred.azurum_miner.screen.widget.AssembleButton;
import com.nred.azurum_miner.screen.widget.ItemButton;
import com.nred.azurum_miner.screen.widget.ModifierButtonsWidget;
import com.nred.azurum_miner.screen.widget.RadioButtonsWidget;
import com.nred.azurum_miner.util.DataMapUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import oshi.util.tuples.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.nred.azurum_miner.AzurumMiner.MODID;
import static com.nred.azurum_miner.config.ClientConfig.*;
import static com.nred.azurum_miner.menu.UpgradeTableMenu.OUTPUT_SLOT;
import static com.nred.azurum_miner.registration.DataMapRegistration.*;
import static com.nred.azurum_miner.util.DataMapUtil.fluidDataMap;
import static com.nred.azurum_miner.util.DataMapUtil.itemDataMap;
import static com.nred.azurum_miner.util.Helpers.azLoc;
import static com.nred.azurum_miner.util.Helpers.getPercentage;

public class UpgradeSchematicTableScreen extends SidebarScreen<UpgradeTableBlockEntity, UpgradeTableMenu> {
    public UpgradeSchematicTableScreen(UpgradeTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, azLoc("textures/gui/container/upgrade_table.png"), 230, 219, null);
    }

    public boolean modifiersScreen = false;
    private GridLayout layout;
    private LinearLayout modifiersLayout;
    private GridLayout statsLayout;
    public Button nextButton;
    public AssembleButton assembleButton;
    public Map<String, StringWidget> statMap = new HashMap<>(4);
    public Function<Double, Component> speedFunc = (speed) -> Component.translatable(MODID + ".tooltip.upgrade.stat_speed", getPercentage(speed)).withColor(SPEED_COLOUR.get());
    public Function<Double, Component> energyFunc = (energy) -> Component.translatable(MODID + ".tooltip.upgrade.stat_energy", getPercentage(energy)).withColor(ENERGY_COLOUR.get());
    public BiFunction<Integer, Integer, Component> complexityFunc = (complexity, max_complexity) -> Component.translatable(MODID + ".tooltip.upgrade.stat_complexity", Component.literal("" + complexity).withStyle(complexity > max_complexity ? ChatFormatting.RED : ChatFormatting.WHITE), max_complexity).withColor(COMPLEXITY_COLOUR.get());
    public BiFunction<Integer, Integer, Component> heatFunc = (heat, max_heat) -> Component.translatable(MODID + ".tooltip.upgrade.stat_heat", Component.literal("" + heat).withStyle(heat > max_heat ? ChatFormatting.RED : ChatFormatting.WHITE), max_heat).withColor(HEAT_COLOUR.get());

    public RadioButtonsWidget cardType = new RadioButtonsWidget(itemDataMap(CARD_TYPE_DATA, Comparator.comparingInt(e -> e.getB().max_complexity())), menu);
    public RadioButtonsWidget coolingType = new RadioButtonsWidget(fluidDataMap(COOLING_TYPE_DATA, Comparator.comparing(Pair::getB, (a, b) -> {
        int power = Double.compare(a.energy(), b.energy());
        int speed = Double.compare(a.speed(), b.speed());
        if (speed != 0 || power != 0) {
            return -1;
        }
        return Integer.compare(a.max_heat(), b.max_heat());
    })), menu);
    public ModifierButtonsWidget modifiers = new ModifierButtonsWidget(itemDataMap(MODIFIER_TYPE_DATA), menu);

    @Override
    protected void init() {
        super.init();

        modifiersLayout = new LinearLayout(this.leftPos + 8, this.topPos + 11, LinearLayout.Orientation.VERTICAL);
        layout = new GridLayout(this.leftPos + 8, this.topPos + 11).spacing(2);
        LayoutSettings center = layout.newCellSettings().alignHorizontallyCenter().alignVerticallyMiddle();

        statsLayout = new GridLayout(leftPos + 25, topPos + 108).spacing(5);
        statsLayout.defaultCellSetting().alignHorizontallyRight();
        statMap.put("speed", new StringWidget(speedFunc.apply(0.0), minecraft.font));
        statMap.get("speed").setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade.speed.desc")));
        statMap.put("energy", new StringWidget(energyFunc.apply(0.0), minecraft.font));
        statMap.get("energy").setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade.energy.desc")));
        statMap.put("heat", new StringWidget(heatFunc.apply(0, 0), minecraft.font));
        statMap.get("heat").setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade.heat.desc")));
        statMap.put("complexity", new StringWidget(complexityFunc.apply(0, 0), minecraft.font));
        statMap.get("complexity").setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade.complexity.desc")));

        LayoutSettings stat_left = statsLayout.newCellSettings().alignHorizontallyLeft().paddingLeft(8).paddingTop(-1);
        statsLayout.addChild(statMap.get("speed"), 0, 0, stat_left);
        statsLayout.addChild(statMap.get("energy"), 1, 0, stat_left);
        statsLayout.addChild(statMap.get("heat"), 0, 1, stat_left);
        statsLayout.addChild(statMap.get("complexity"), 1, 1, stat_left);
        statsLayout.visitWidgets(this::addRenderableWidget);

        layout.defaultCellSetting().alignHorizontallyCenter().alignVerticallyBottom();
        StringWidget cardString = new StringWidget(Component.translatable(MODID + ".tooltip.upgrade.card_type"), font);
        cardString.setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade.card_type.desc")));
        layout.addChild(cardString, 0, 0, center.paddingBottom(2));
        layout.addChild(cardType, 1, 0, center);
        StringWidget coolantString = new StringWidget(Component.translatable(MODID + ".tooltip.upgrade.coolant_type"), font);
        coolantString.setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade.coolant_type.desc")));
        layout.addChild(coolantString, 0, 1, center.paddingBottom(2));
        layout.addChild(coolingType, 1, 1, center);

        layout.addChild(SpacerElement.width(imageWidth / 2 - 11), 3, 0);
        layout.addChild(SpacerElement.width(imageWidth / 2 - 11), 3, 1);

        layout.visitWidgets(this::addRenderableWidget);
        layout.arrangeElements();

        StringWidget modifiersString = new StringWidget(Component.translatable(MODID + ".tooltip.upgrade.modifier_type"), font);
        modifiersString.setTooltip(Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade.modifier_type.desc")));
        modifiersLayout.addChild(modifiersString, center.paddingBottom(2));
        modifiersLayout.addChild(modifiers, center);
        modifiersLayout.addChild(SpacerElement.width(imageWidth - 22));
        modifiersLayout.visitWidgets(this::addRenderableWidget);
        setVisible(modifiersLayout, false);

        modifiersLayout.arrangeElements();

        nextButton = new PageButton(leftPos + 200, topPos + 87, true, (btn) -> {
            setVisible(modifiersLayout, !modifiersScreen);
            setVisible(layout, modifiersScreen);
            modifiersScreen = !modifiersScreen;
        }, true);
        addRenderableWidget(nextButton);

        assembleButton = new AssembleButton((btn) -> {
            setVisible(modifiersLayout, false);
            setVisible(layout, true);
            modifiersScreen = false;
            ClientPacketDistributor.sendToServer(new UpgradeTableSelectPayload(menu.blockEntity.getBlockPos(), UpgradeTableSelectPayload.SelectType.ASSEMBLE));
            setErrors();
        });
        assembleButton.setPosition(leftPos + 156, topPos + 108);

        addRenderableWidget(assembleButton);

        calculateStats();
    }

    private void setVisible(Layout layout, boolean visible) {
        layout.visitWidgets(e -> {
            e.active = visible;
            e.visible = visible;
        });
    }

    public void calculateStats() {
        HashMap<Item, ModifierTypeData> modifiersData = new HashMap<>();
        modifiers.visitChildren(e -> {
            if (e instanceof ItemButton btn) {
                modifiersData.put(btn.stack.getItem(), DataMapUtil.dataMapItem(MODIFIER_TYPE_DATA, btn.stack.getItem()));
            }
        });

        UpgradeStats stats = menu.blockEntity.calculateStats(DataMapUtil.dataMapItem(CARD_TYPE_DATA, cardType.getItem()), DataMapUtil.dataMapFluid(COOLING_TYPE_DATA, coolingType.getFluid()), modifiersData);
        statMap.get("speed").setMessage(speedFunc.apply(stats.speed()));
        statMap.get("energy").setMessage(energyFunc.apply(stats.energy()));
        statMap.get("complexity").setMessage(complexityFunc.apply(stats.complexity(), stats.max_complexity()));
        statMap.get("heat").setMessage(heatFunc.apply(stats.heat(), stats.max_heat()));

        this.statsLayout.arrangeElements();

        setErrors();
    }

    public void setErrors() {
        if (menu.blockEntity.modifiers.values().stream().reduce(0, Integer::sum) == 0) {
            assembleButton.setLoading(true, Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade_table.error.no_modifiers").withStyle(ChatFormatting.RED)));
        } else {
            var hasErrors = menu.blockEntity.hasErrors(menu.player);
            List<ItemStack> missing = hasErrors.getA();
            if (hasErrors.getB()) {
                assembleButton.setLoading(true, Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade_table.error.too_much_heat").withStyle(ChatFormatting.RED)));
            } else if (hasErrors.getC()) {
                assembleButton.setLoading(true, Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade_table.error.too_much_complexity").withStyle(ChatFormatting.RED)));
            } else if (!missing.isEmpty()) {
                MutableComponent component = Component.translatable(MODID + ".tooltip.upgrade_table.error.missing_items").withStyle(ChatFormatting.RED);
                for (int i = 0; i < Math.min(4, missing.size()); i++) {
                    component.append("\n- " + missing.get(i).getCount() + "x ");
                    component.append(Component.translatable(missing.get(i).getItem().getDescriptionId()).withStyle(ChatFormatting.WHITE));
                }
                if (missing.size() > 4) {
                    component.append("\n...");
                }
                assembleButton.setLoading(true, Tooltip.create(component));
            } else if (!menu.blockEntity.itemHandler.getResource(OUTPUT_SLOT).isEmpty()) {
                assembleButton.setLoading(true, Tooltip.create(Component.translatable(MODID + ".tooltip.upgrade_table.error.no_space").withStyle(ChatFormatting.RED)));
            } else {
                assembleButton.setLoading(false);
            }
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        setErrors();
    }

    @Override
    public void repositionElements() {
        super.repositionElements();

        this.modifiersLayout.arrangeElements();
        this.modifiersLayout.setPosition(leftPos + 8, topPos + 11);

        this.layout.arrangeElements();
        this.layout.setPosition(leftPos + 8, topPos + 11);

        this.statsLayout.setPosition(leftPos + 25, topPos + 108);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        if (modifiersScreen) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, this.background, this.leftPos + 115, this.topPos + 7, 15F, 7F, 2, 91, 256, 256);
        }
    }
}