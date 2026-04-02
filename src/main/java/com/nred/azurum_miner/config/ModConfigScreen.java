package com.nred.azurum_miner.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.llamalad7.mixinextras.lib.apache.commons.StringUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringDecomposer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.Nullable;

import java.util.HexFormat;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModConfigScreen extends ConfigurationScreen.ConfigurationSectionScreen {
    public ModConfigScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title) {
        super(parent, type, modConfig, title);
    }

    public ModConfigScreen(Context parentContext, Screen parent, Map<String, Object> valueSpecs, String key, Set<? extends UnmodifiableConfig.Entry> entrySet, Component title) {
        super(parentContext, parent, valueSpecs, key, entrySet, title);
    }

    @Override
    protected @Nullable Element createIntegerValue(String key, ModConfigSpec.ValueSpec spec, Supplier<Integer> source, Consumer<Integer> target) {
        if (key.endsWith("_colour")) {
            return createColourPicker(key, spec, source, target);
        } else {
            return super.createIntegerValue(key, spec, source, target);
        }
    }

    @Override
    protected @Nullable Element createSection(String key, UnmodifiableConfig subconfig, UnmodifiableConfig subsection) {
        Element element = super.createSection(key, subconfig, subsection);
        assert element != null;
        if (element.widget() instanceof Button btn) {
            return new Element(element.name(), element.tooltip(),
                    Button.builder(btn.getMessage(),
                                    button -> minecraft.setScreen(sectionCache.computeIfAbsent(key,
                                            k -> new ModConfigScreen(context, this, subconfig.valueMap(), key, subsection.entrySet(), Component.translatable(getTranslationKey(key))).rebuild())))
                            .tooltip(Tooltip.create(getTooltipComponent(key, null)))
                            .width(btn.getWidth())
                            .build(), element.undoable());
        }
        return element;
    }

    protected @Nullable Element createColourPicker(String key, ModConfigSpec.ValueSpec spec, Supplier<Integer> source, Consumer<Integer> target) {
        final EditBox box = new EditBox(font, Button.SMALL_WIDTH, Button.DEFAULT_HEIGHT, getTranslationComponent(key)) {
            @Override
            public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                super.extractWidgetRenderState(graphics, mouseX, mouseY, a);

                if (this.isVisible()) { // Draws the colour box
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("widget/text_field"), this.getX() + this.getWidth() + 5, this.getY(), Button.DEFAULT_WIDTH - this.getWidth() - 6, this.getHeight());
                    graphics.fill(RenderPipelines.GUI, this.getX() + this.getWidth() + 6, this.getY() + 1, this.getX() + Button.DEFAULT_WIDTH - 2, this.getY() + Button.DEFAULT_HEIGHT - 1, HexFormat.fromHexDigits("ff" + this.getValue().concat("000000").substring(0, 6)));
                    // TODO add button to colour pick?
                }
            }
        };

        box.setMaxLength(6);
        box.setEditable(true);
        box.setFilter(newValueString -> {
            try {
                HexFormat.fromHexDigits(newValueString);
                return true;
            } catch (final IllegalArgumentException e) {
                return false;
            }
        });

        // Colours the string in RBG
        box.addFormatter((text, offset) -> output -> {
            if (offset == 0 && !text.isEmpty()) output.accept(0, Style.EMPTY, '#');
            return StringDecomposer.iterate(text, Style.EMPTY, (p, s, ch) -> output.accept(p, s.withColor(offset + p < 2 ? 0xFFFF4040 : offset + p < 4 ? 0xFF40FF40 : 0xFF4040FF), ch));
        });

        box.setTooltip(Tooltip.create(getTooltipComponent(key, null)));
        box.setValue(HexFormat.of().toHexDigits(source.get()).substring(2));
        box.setResponder(newValueString -> {
            box.setSuggestion((newValueString.isEmpty() ? "#" : "") + StringUtils.repeat('0', 6 - newValueString.length()));

            try {
                final Integer newValue = HexFormat.fromHexDigits("ff" + newValueString.concat("000000").substring(0, 6));
                if (spec.test(newValue)) {
                    if (!newValue.equals(source.get())) {
                        undoManager.add(v -> {
                            target.accept(v);
                            onChanged(key);
                        }, newValue, v -> {
                            target.accept(v);
                            onChanged(key);
                        }, source.get());
                    }
                    return;
                }
            } catch (final NumberFormatException e) {
                // field probably is just empty/partial, ignore that
            }
            box.setTextColor(0xFFFF0000);
        });

        return new Element(getTranslationComponent(key), getTooltipComponent(key, null), box);
    }
}