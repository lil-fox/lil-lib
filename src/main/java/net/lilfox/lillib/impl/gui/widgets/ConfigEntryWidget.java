package net.lilfox.lillib.impl.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.lilfox.lillib.api.config.*;
import net.lilfox.lillib.impl.util.LocalizationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Widget representing a single configuration entry in the list.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: <a href="https://github.com/sakura-ryoko/malilib">...</a>
 * Licensed under the GNU Lesser General Public License v3.0
 *
 * <p><b>Fixed version:</b>
 * - Tooltips rendered separately at screen coordinates
 * - Hotkey editing finished on any click
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
    private ConfigHotkeyWidget hotkeyWidget;

    // Layout constants
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 5;
    private static final int NAME_WIDTH = 200;

    /**
     * Creates a config entry widget.
     *
     * @param config The configuration to display
     * @param x The X position
     * @param y The Y position
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
     * Creates widgets with proper ordering:
     * [Value] [Hotkey] [Eye] [Reset]
     */
    private void createWidgets() {
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

        // Type-specific controls
        if (config instanceof IConfigBooleanHotkeyed boolHotkeyConfig) {

            // 2. Eye button (if has effect)
            if (boolHotkeyConfig.hasEffect()) {
                int eyeWidth = 30;
                currentX -= eyeWidth;
                EffectToggleButton effectButton = new EffectToggleButton(
                        currentX, y, eyeWidth, BUTTON_HEIGHT, boolHotkeyConfig
                );
                widgets.add(effectButton);
                currentX -= BUTTON_SPACING;
            }

            // 3. Hotkey button
            int hotkeyWidth = 80;
            currentX -= hotkeyWidth;
            this.hotkeyWidget = new ConfigHotkeyWidget(
                    currentX, y, hotkeyWidth, BUTTON_HEIGHT, boolHotkeyConfig
            );
            widgets.add(this.hotkeyWidget);
            currentX -= BUTTON_SPACING;

            // 4. Boolean value button
            int boolWidth = 60;
            currentX -= boolWidth;
            ConfigBooleanWidget boolWidget = new ConfigBooleanWidget(
                    currentX, y, boolWidth, BUTTON_HEIGHT, boolHotkeyConfig
            );
            widgets.add(boolWidget);

        } else if (config instanceof IConfigBoolean boolConfig) {

            // 2. Eye button (if has effect)
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

        } else if (config instanceof IConfigInteger intConfig) {

            if (intConfig.useSlider()) {
                int sliderWidth = 120;
                currentX -= sliderWidth;
                ConfigSliderWidget sliderWidget = new ConfigSliderWidget(
                        currentX, y, sliderWidth, BUTTON_HEIGHT, intConfig
                );
                widgets.add(sliderWidget);
            } else {
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

        } else if (config instanceof IConfigDouble doubleConfig) {

            if (doubleConfig.useSlider()) {
                int sliderWidth = 120;
                currentX -= sliderWidth;
                ConfigSliderWidget sliderWidget = new ConfigSliderWidget(
                        currentX, y, sliderWidth, BUTTON_HEIGHT, doubleConfig
                );
                widgets.add(sliderWidget);
            } else {
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

        } else if (config instanceof IConfigString stringConfig) {

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

    /**
     * Renders the config entry (without tooltips).
     * <p>
     * Parent applies scroll via matrix transformation, so we render at absolute coordinates.
     * Tooltips are rendered separately via renderTooltip() after matrix pop.
     *
     * @param context The draw context (with scroll transformation already applied)
     * @param mouseX The mouse X position
     * @param mouseY The mouse Y position (adjusted for scroll by parent)
     * @param delta The delta time
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render widgets (buttons)
        for (ClickableWidget widget : widgets) {
            widget.render(context, mouseX, mouseY, delta);
        }

        // Draw config name on the left
        String displayName = config.getDisplayName();
        int nameY = y + (height - textRenderer.fontHeight) / 2;
        context.drawText(textRenderer, displayName, x + 5, nameY, 0xFFFFFFFF, true);

        // Draw modified indicator next to name
        if (config.isModified()) {
            int nameWidth = textRenderer.getWidth(displayName);
            context.drawText(textRenderer, "*", x + 5 + nameWidth + 3, nameY, 0xFFFFFF00, false);
        }
    }

    /**
     * Renders tooltips at screen coordinates (called after matrix pop).
     * <p>
     * This is called by parent screen AFTER restoring matrix transformation,
     * so tooltips appear at correct screen position.
     *
     * @param context The draw context (without scroll transformation)
     * @param mouseX The real mouse X position
     * @param mouseY The real mouse Y position
     * @param scrollOffset The current scroll offset
     */
    public void renderTooltip(DrawContext context, int mouseX, int mouseY, int scrollOffset) {
        // Calculate adjusted Y for checking hover (account for scroll)
        int adjustedMouseY = mouseY + scrollOffset;

        // Draw description tooltip on hover over name area
        String description = config.getDescription();
        if (!description.isEmpty() && isMouseOverName(mouseX, adjustedMouseY)) {
            context.drawTooltip(textRenderer, Text.literal(description), mouseX, mouseY);
        }

        // Draw hotkey conflict tooltip (if applicable and SHIFT is held)
        if (config instanceof IConfigBooleanHotkeyed && MinecraftClient.getInstance().options.sneakKey.isPressed()) {
            if (hotkeyWidget != null) {
                Text conflictTooltip = hotkeyWidget.getConflictTooltip();
                if (conflictTooltip != null && hotkeyWidget.isMouseOver(mouseX, adjustedMouseY)) {
                    context.drawTooltip(textRenderer, conflictTooltip, mouseX, mouseY);
                }
            }
        }
    }

    /**
     * Handles mouse clicks.
     * <p>
     * Parent adjusts mouse position for scroll, so we check against absolute coordinates.
     *
     * @param click The click event (with Y adjusted for scroll by parent)
     * @param doubled Whether this is a double click
     * @return true if the click was handled
     */
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        // Check if click is within widget bounds
        if (!isMouseOver(click.x(), click.y())) {
            return false;
        }

        // Check widgets
        for (Element widget : widgets) {
            if (widget.mouseClicked(click, doubled)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (hotkeyWidget != null && hotkeyWidget.isEditing()) {
            return hotkeyWidget.keyPressed(input);
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width &&
                mouseY >= y && mouseY < y + height;
    }

    /**
     * Checks if mouse is over the name area.
     *
     * @param mouseX The mouse X position
     * @param mouseY The mouse Y position (adjusted for scroll)
     * @return true if mouse is over the name area
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
     * Gets the configuration associated with this widget.
     *
     * @return The configuration
     */
    public IConfigBase getConfig() {
        return config;
    }

    /**
     * Checks if currently editing a hotkey.
     *
     * @return true if editing a hotkey
     */
    public boolean isEditingHotkey() {
        return hotkeyWidget != null && hotkeyWidget.isEditing();
    }

    /**
     * Finishes hotkey editing (called when clicking anywhere).
     */
    public void finishHotkeyEdit() {
        if (hotkeyWidget != null && hotkeyWidget.isEditing()) {
            hotkeyWidget.finishEditing(true);
        }
    }
}