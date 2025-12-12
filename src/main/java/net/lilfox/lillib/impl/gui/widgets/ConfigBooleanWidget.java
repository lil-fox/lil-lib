package net.lilfox.lillib.impl.gui.widgets;

import net.lilfox.lillib.api.config.IConfigBoolean;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Widget for boolean configuration values.
 * <p>
 * Displays as a button with green "TRUE" or red "FALSE".
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: <a href="https://github.com/sakura-ryoko/malilib">...</a>
 * Licensed under the GNU Lesser General Public License v3.0
 *
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigBooleanWidget extends ButtonWidget {
    private final IConfigBoolean config;

    /**
     * Creates a new boolean config widget.
     * 
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param config The boolean configuration
     */
    public ConfigBooleanWidget(int x, int y, int width, int height, IConfigBoolean config) {
        super(x, y, width, height, getBooleanText(config.getBooleanValue()), 
              button -> onPress(config), DEFAULT_NARRATION_SUPPLIER);
        this.config = config;
    }

    /**
     * Handles button press to toggle the boolean value.
     * 
     * @param config The configuration to toggle
     */
    private static void onPress(IConfigBoolean config) {
        config.toggleBooleanValue();
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Update button text to match current value
        this.setMessage(getBooleanText(config.getBooleanValue()));
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    /**
     * Gets the formatted text for a boolean value.
     * <p>
     * GREEN "TRUE" for true, RED "FALSE" for false.
     * These are NOT localized.
     * 
     * @param value The boolean value
     * @return The formatted text
     */
    private static Text getBooleanText(boolean value) {
        if (value) {
            return Text.literal("TRUE").formatted(Formatting.GREEN);
        } else {
            return Text.literal("FALSE").formatted(Formatting.RED);
        }
    }
}
