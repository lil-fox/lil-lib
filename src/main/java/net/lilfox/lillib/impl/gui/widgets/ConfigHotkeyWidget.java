package net.lilfox.lillib.impl.gui.widgets;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.lilfox.lillib.api.config.IConfigHotkey;
import net.lilfox.lillib.impl.hotkey.HotkeySequence;
import net.lilfox.lillib.impl.util.LocalizationHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Widget for editing hotkey configurations.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 *
 * <p><b>Fixed version:</b>
 * - Public finishEditing() method for external control
 * - Click anywhere finishes editing
 *
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigHotkeyWidget extends ButtonWidget {
    private final IConfigHotkey config;
    private boolean isEditing;
    private List<Integer> capturedKeys;
    private String originalHotkey;

    /**
     * Creates a new hotkey config widget.
     *
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param config The hotkey configuration
     */
    public ConfigHotkeyWidget(int x, int y, int width, int height, IConfigHotkey config) {
        super(x, y, width, height, getDisplayText(config),
                button -> ((ConfigHotkeyWidget) button).onClick(), DEFAULT_NARRATION_SUPPLIER);
        this.config = config;
        this.isEditing = false;
        this.capturedKeys = new ArrayList<>();
    }

    /**
     * Handles button click to start editing.
     */
    private void onClick() {
        if (!isEditing) {
            startEditing();
        }
    }

    /**
     * Starts editing mode for hotkey capture.
     */
    private void startEditing() {
        isEditing = true;
        capturedKeys.clear();
        originalHotkey = config.getHotkey();
        updateMessage();
    }

    /**
     * Finishes editing and optionally saves the hotkey.
     * <p>
     * This method is public so parent screen can call it on any click.
     *
     * @param save Whether to save the captured hotkey
     */
    public void finishEditing(boolean save) {
        if (!isEditing) {
            return;
        }

        if (save && !capturedKeys.isEmpty()) {
            HotkeySequence sequence = new HotkeySequence(capturedKeys);
            config.setHotkey(sequence.getStringRepresentation());
        } else if (!save) {
            // Restore original
            config.setHotkey(originalHotkey);
        }

        isEditing = false;
        capturedKeys.clear();
        updateMessage();
    }

    /**
     * Handles key press during editing.
     *
     * @param input The key input
     * @return true if handled
     */
    public boolean keyPressed(KeyInput input) {
        if (!isEditing) {
            return false;
        }

        // ESC cancels editing
        if (input.getKeycode() == GLFW.GLFW_KEY_ESCAPE) {
            finishEditing(false);
            return true;
        }

        // Add key to sequence
        if (input.getKeycode() != GLFW.GLFW_KEY_UNKNOWN) {
            if (!capturedKeys.contains(input.getKeycode())) {
                capturedKeys.add(input.getKeycode());
                updateMessage();
            }
        }

        return true;
    }

    /**
     * Handles mouse click.
     * <p>
     * Note: Parent screen handles finishing edit on any click,
     * so this method only needs to handle starting edit.
     *
     * @param click The click event
     * @param doubled Whether this is a double click
     * @return true if handled
     */
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        // If not editing, let button handle click normally (start editing)
        if (!isEditing) {
            return super.mouseClicked(click, doubled);
        }

        // If editing, parent screen will handle finishing
        return false;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        updateMessage();
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    /**
     * Updates the button message based on current state.
     */
    private void updateMessage() {
        if (isEditing) {
            if (capturedKeys.isEmpty()) {
                this.setMessage(Text.literal(LocalizationHelper.getLibTranslation("hotkey.listening"))
                        .formatted(Formatting.YELLOW));
            } else {
                HotkeySequence sequence = new HotkeySequence(capturedKeys);
                this.setMessage(Text.literal(sequence.getStringRepresentation())
                        .formatted(Formatting.AQUA));
            }
        } else {
            this.setMessage(getDisplayText(config));
        }
    }

    /**
     * Gets the display text for a hotkey config.
     *
     * @param config The configuration
     * @return The display text
     */
    private static Text getDisplayText(IConfigHotkey config) {
        String hotkey = config.getHotkey();

        if (hotkey == null || hotkey.isEmpty()) {
            return Text.literal(LocalizationHelper.getLibTranslation("hotkey.none"))
                    .formatted(Formatting.GRAY);
        }

        // Check for conflicts
        if (config.hasConflicts()) {
            return Text.literal(hotkey).formatted(Formatting.GOLD);
        }

        return Text.literal(hotkey).formatted(Formatting.WHITE);
    }

    /**
     * Checks if currently editing a hotkey.
     *
     * @return true if editing
     */
    public boolean isEditing() {
        return isEditing;
    }

    /**
     * Gets the conflict tooltip if conflicts exist.
     *
     * @return Conflict tooltip text, or null if no conflicts
     */
    public Text getConflictTooltip() {
        if (!config.hasConflicts()) {
            return null;
        }

        String[] conflicts = config.getConflicts();
        StringBuilder tooltip = new StringBuilder();
        tooltip.append(LocalizationHelper.getLibTranslation("hotkey.conflicts")).append(":\n");

        for (String conflict : conflicts) {
            tooltip.append("- ").append(conflict).append("\n");
        }

        return Text.literal(tooltip.toString()).formatted(Formatting.GOLD);
    }
}