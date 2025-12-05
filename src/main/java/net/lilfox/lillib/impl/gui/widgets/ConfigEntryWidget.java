package net.lilfox.lillib.impl.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.lilfox.lillib.api.config.*;
import net.lilfox.lillib.impl.util.LocalizationHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Widget representing a single configuration entry in the list.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 *
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigEntryWidget implements Drawable, Element {
    private final IConfigBase config;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final List<ClickableWidget> widgets;
    private final TextRenderer textRenderer;

    /**
     * Creates a new config entry widget.
     *
     * @param config The configuration
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param textRenderer The text renderer
     */
    public ConfigEntryWidget(IConfigBase config, int x, int y, int width, int height, TextRenderer textRenderer) {
        this.config = config;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textRenderer = textRenderer;
        this.widgets = new ArrayList<>();

        createWidgets();
    }

    /**
     * Creates the appropriate widgets for this config type.
     */
    private void createWidgets() {
        int controlsX = x + width - 240; // Start controls from right side
        int buttonWidth = 60;
        int buttonHeight = 20;
        int spacing = 5;
        int currentX = controlsX;

        // Reset button (always present)
        ButtonWidget resetButton = ButtonWidget.builder(
                Text.literal(LocalizationHelper.getLibTranslation("button.reset")),
                button -> config.resetToDefault()
        ).dimensions(currentX + 180, y, buttonWidth, buttonHeight).build();
        widgets.add(resetButton);

        // Type-specific controls
        if (config instanceof IConfigBooleanHotkeyed) {
            IConfigBooleanHotkeyed boolHotkeyConfig = (IConfigBooleanHotkeyed) config;

            // Boolean button
            ConfigBooleanWidget boolWidget = new ConfigBooleanWidget(
                    currentX, y, buttonWidth, buttonHeight, boolHotkeyConfig
            );
            widgets.add(boolWidget);
            currentX += buttonWidth + spacing;

            // Effect toggle button (if has effect)
            if (boolHotkeyConfig.hasEffect()) {
                EffectToggleButton effectButton = new EffectToggleButton(
                        currentX, y, 30, buttonHeight, boolHotkeyConfig
                );
                widgets.add(effectButton);
                currentX += 30 + spacing;
            }

            // Hotkey button
            ConfigHotkeyWidget hotkeyWidget = new ConfigHotkeyWidget(
                    currentX, y, 80, buttonHeight, boolHotkeyConfig
            );
            widgets.add(hotkeyWidget);

        } else if (config instanceof IConfigBoolean) {
            IConfigBoolean boolConfig = (IConfigBoolean) config;

            // Boolean button
            ConfigBooleanWidget boolWidget = new ConfigBooleanWidget(
                    currentX, y, buttonWidth, buttonHeight, boolConfig
            );
            widgets.add(boolWidget);
            currentX += buttonWidth + spacing;

            // Effect toggle button (if has effect)
            if (boolConfig.hasEffect()) {
                EffectToggleButton effectButton = new EffectToggleButton(
                        currentX, y, 30, buttonHeight, boolConfig
                );
                widgets.add(effectButton);
            }

        } else if (config instanceof IConfigInteger) {
            IConfigInteger intConfig = (IConfigInteger) config;

            if (intConfig.useSlider()) {
                // Slider
                ConfigSliderWidget sliderWidget = new ConfigSliderWidget(
                        currentX, y, 120, buttonHeight, intConfig
                );
                widgets.add(sliderWidget);
            } else {
                // Text field
                TextFieldWidget textField = new TextFieldWidget(
                        textRenderer, currentX, y, 80, buttonHeight, Text.empty()
                );
                textField.setMaxLength(10);
                textField.setText(String.valueOf(intConfig.getIntegerValue()));
                textField.setChangedListener(text -> {
                    try {
                        int value = Integer.parseInt(text);
                        intConfig.setIntegerValue(value);
                    } catch (NumberFormatException ignored) {
                    }
                });
                widgets.add(textField);
            }

        } else if (config instanceof IConfigDouble) {
            IConfigDouble doubleConfig = (IConfigDouble) config;

            if (doubleConfig.useSlider()) {
                // Slider
                ConfigSliderWidget sliderWidget = new ConfigSliderWidget(
                        currentX, y, 120, buttonHeight, doubleConfig
                );
                widgets.add(sliderWidget);
            } else {
                // Text field
                TextFieldWidget textField = new TextFieldWidget(
                        textRenderer, currentX, y, 80, buttonHeight, Text.empty()
                );
                textField.setMaxLength(16);
                textField.setText(String.valueOf(doubleConfig.getDoubleValue()));
                textField.setChangedListener(text -> {
                    try {
                        double value = Double.parseDouble(text);
                        doubleConfig.setDoubleValue(value);
                    } catch (NumberFormatException ignored) {
                    }
                });
                widgets.add(textField);
            }

        } else if (config instanceof IConfigString) {
            IConfigString stringConfig = (IConfigString) config;

            // Text field
            TextFieldWidget textField = new TextFieldWidget(
                    textRenderer, currentX, y, 120, buttonHeight, Text.empty()
            );
            textField.setMaxLength(256);
            textField.setText(stringConfig.getStringValue());
            textField.setChangedListener(stringConfig::setStringValue);
            widgets.add(textField);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw config name
        String displayName = config.getDisplayName();
        context.drawText(textRenderer, displayName, x + 5, y + 5, 0xFFFFFF, false);

        // Draw modified indicator
        if (config.isModified()) {
            String modifiedText = "*";
            context.drawText(textRenderer, modifiedText, x + 5 + textRenderer.getWidth(displayName) + 3, y + 5, 0xFFFF00, false);
        }

        // Render widgets
        for (ClickableWidget widget : widgets) {
            widget.render(context, mouseX, mouseY, delta);
        }

        // Draw description tooltip
        String description = config.getDescription();
        if (!description.isEmpty() && isMouseOver(mouseX, mouseY)) {
            // Check if mouse is over the name area (not over buttons)
            if (mouseX < x + width - 250) {
                context.drawTooltip(textRenderer, Text.literal(description), mouseX, mouseY);
            }
        }

        // Draw hotkey conflict tooltip (if applicable and SHIFT is held)
        if (config instanceof IConfigBooleanHotkeyed && MinecraftClient.getInstance().options.sneakKey.isPressed()) {
            for (ClickableWidget widget : widgets) {
                if (widget instanceof ConfigHotkeyWidget) {
                    ConfigHotkeyWidget hotkeyWidget = (ConfigHotkeyWidget) widget;
                    Text conflictTooltip = hotkeyWidget.getConflictTooltip();
                    if (conflictTooltip != null && widget.isMouseOver(mouseX, mouseY)) {
                        context.drawTooltip(textRenderer, conflictTooltip, mouseX, mouseY);
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        for (Element widget : widgets) {
            if (widget.mouseClicked(click,doubled)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Custom key pressed handler for compatibility.
     *
     * @param keyCode The key code
     * @param scanCode The scan code
     * @param modifiers The modifiers
     * @return true if handled
     */
    @Override
    public boolean keyPressed(KeyInput input) {
        for (ClickableWidget widget : widgets) {
            if (widget instanceof ConfigHotkeyWidget hotkeyWidget) {
                if (hotkeyWidget.keyPressed(input)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    /**
     * Gets the configuration this widget represents.
     *
     * @return The configuration
     */
    public IConfigBase getConfig() {
        return config;
    }

    /**
     * Checks if any hotkey widget is currently editing.
     *
     * @return true if editing a hotkey
     */
    public boolean isEditingHotkey() {
        for (ClickableWidget widget : widgets) {
            if (widget instanceof ConfigHotkeyWidget) {
                if (((ConfigHotkeyWidget) widget).isEditing()) {
                    return true;
                }
            }
        }
        return false;
    }
}