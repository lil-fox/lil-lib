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
 * <p><b>Layout fixed version:</b>
 * - Config name displayed on left
 * - Hotkey and eye buttons swapped
 * - Proper button spacing with margins
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

    // Layout constants
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 5;
    private static final int NAME_WIDTH = 200;  // Space for config name on left

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
     * <p>
     * Layout from right to left:
     * [Reset Button] [Hotkey Button] [Eye Button] [Value Widget] [Config Name]
     */
    private void createWidgets() {
        // Start from the right side
        int currentX = x + width;

        // 1. Reset button (rightmost)
        int resetWidth = 60;
        currentX -= resetWidth;
        ButtonWidget resetButton = ButtonWidget.builder(
                Text.literal(LocalizationHelper.getLibTranslation("button.reset")),
                button -> config.resetToDefault()
        ).dimensions(currentX, y, resetWidth, BUTTON_HEIGHT).build();
        widgets.add(resetButton);

        currentX -= BUTTON_SPACING;

        // Type-specific controls (right to left)
        if (config instanceof IConfigBooleanHotkeyed) {
            IConfigBooleanHotkeyed boolHotkeyConfig = (IConfigBooleanHotkeyed) config;

            // 2. Hotkey button
            int hotkeyWidth = 80;
            currentX -= hotkeyWidth;
            ConfigHotkeyWidget hotkeyWidget = new ConfigHotkeyWidget(
                    currentX, y, hotkeyWidth, BUTTON_HEIGHT, boolHotkeyConfig
            );
            widgets.add(hotkeyWidget);
            currentX -= BUTTON_SPACING;

            // 3. Effect toggle button (eye) - if has effect
            if (boolHotkeyConfig.hasEffect()) {
                int eyeWidth = 30;
                currentX -= eyeWidth;
                EffectToggleButton effectButton = new EffectToggleButton(
                        currentX, y, eyeWidth, BUTTON_HEIGHT, boolHotkeyConfig
                );
                widgets.add(effectButton);
                currentX -= BUTTON_SPACING;
            }

            // 4. Boolean value button
            int boolWidth = 60;
            currentX -= boolWidth;
            ConfigBooleanWidget boolWidget = new ConfigBooleanWidget(
                    currentX, y, boolWidth, BUTTON_HEIGHT, boolHotkeyConfig
            );
            widgets.add(boolWidget);

        } else if (config instanceof IConfigBoolean) {
            IConfigBoolean boolConfig = (IConfigBoolean) config;

            // 2. Effect toggle button (eye) - if has effect
            if (boolConfig.hasEffect()) {
                int eyeWidth = 30;
                currentX -= eyeWidth;
                EffectToggleButton effectButton = new EffectToggleButton(
                        currentX, y, eyeWidth, BUTTON_HEIGHT, boolConfig
                );
                widgets.add(effectButton);
                currentX -= BUTTON_SPACING;
            }

            // 3. Boolean value button
            int boolWidth = 60;
            currentX -= boolWidth;
            ConfigBooleanWidget boolWidget = new ConfigBooleanWidget(
                    currentX, y, boolWidth, BUTTON_HEIGHT, boolConfig
            );
            widgets.add(boolWidget);

        } else if (config instanceof IConfigInteger) {
            IConfigInteger intConfig = (IConfigInteger) config;

            if (intConfig.useSlider()) {
                // Slider
                int sliderWidth = 120;
                currentX -= sliderWidth;
                ConfigSliderWidget sliderWidget = new ConfigSliderWidget(
                        currentX, y, sliderWidth, BUTTON_HEIGHT, intConfig
                );
                widgets.add(sliderWidget);
            } else {
                // Text field
                int fieldWidth = 80;
                currentX -= fieldWidth;
                TextFieldWidget textField = new TextFieldWidget(
                        textRenderer, currentX, y, fieldWidth, BUTTON_HEIGHT, Text.empty()
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
                int sliderWidth = 120;
                currentX -= sliderWidth;
                ConfigSliderWidget sliderWidget = new ConfigSliderWidget(
                        currentX, y, sliderWidth, BUTTON_HEIGHT, doubleConfig
                );
                widgets.add(sliderWidget);
            } else {
                // Text field
                int fieldWidth = 80;
                currentX -= fieldWidth;
                TextFieldWidget textField = new TextFieldWidget(
                        textRenderer, currentX, y, fieldWidth, BUTTON_HEIGHT, Text.empty()
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
            int fieldWidth = 120;
            currentX -= fieldWidth;
            TextFieldWidget textField = new TextFieldWidget(
                    textRenderer, currentX, y, fieldWidth, BUTTON_HEIGHT, Text.empty()
            );
            textField.setMaxLength(256);
            textField.setText(stringConfig.getStringValue());
            textField.setChangedListener(stringConfig::setStringValue);
            widgets.add(textField);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw config name on the left
        String displayName = config.getDisplayName();
        int nameY = y + (height - textRenderer.fontHeight) / 2;
        context.drawText(textRenderer, displayName, x + 5, nameY, 0xFFFFFF, false);

        // Draw modified indicator next to name
        if (config.isModified()) {
            int nameWidth = textRenderer.getWidth(displayName);
            context.drawText(textRenderer, "*", x + 5 + nameWidth + 3, nameY, 0xFFFF00, false);
        }

        // Render widgets (buttons)
        for (ClickableWidget widget : widgets) {
            widget.render(context, mouseX, mouseY, delta);
        }

        // Draw description tooltip on hover over name area
        String description = config.getDescription();
        if (!description.isEmpty() && isMouseOverName(mouseX, mouseY)) {
            context.drawTooltip(textRenderer, Text.literal(description), mouseX, mouseY);
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
            if (widget.mouseClicked(click, doubled)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Custom key pressed handler for compatibility.
     *
     * @param input The key input
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

    /**
     * Checks if mouse is over the config name area (left side).
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @return true if over name area
     */
    private boolean isMouseOverName(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + NAME_WIDTH &&
                mouseY >= y && mouseY < y + height;
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