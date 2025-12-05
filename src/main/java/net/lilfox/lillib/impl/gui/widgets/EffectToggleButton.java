package net.lilfox.lillib.impl.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.lilfox.lillib.api.config.IConfigBoolean;
import net.lilfox.lillib.impl.util.LocalizationHelper;

/**
 * Toggle button for showing/hiding fake effects in HUD.
 * <p>
 * Only displayed for configs that have effects bound to them.
 * Shows as 👁 (eye) icon - green when shown, gray when hidden.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class EffectToggleButton extends ButtonWidget {
    private final IConfigBoolean config;
    private static final Text EYE_VISIBLE = Text.literal("👁").formatted(Formatting.GREEN);
    private static final Text EYE_HIDDEN = Text.literal("👁").formatted(Formatting.GRAY);

    /**
     * Creates a new effect toggle button.
     * 
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param config The boolean configuration with effect
     */
    public EffectToggleButton(int x, int y, int width, int height, IConfigBoolean config) {
        super(x, y, width, height, getButtonText(config), 
              button -> onPress(config), DEFAULT_NARRATION_SUPPLIER);
        this.config = config;
        updateTooltip();
    }

    /**
     * Handles button press to toggle effect visibility.
     * 
     * @param config The configuration
     */
    private static void onPress(IConfigBoolean config) {
        config.setShowEffect(!config.getShowEffect());
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Update button appearance based on current state
        this.setMessage(getButtonText(config));
        updateTooltip();
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    /**
     * Updates the tooltip based on current state.
     */
    private void updateTooltip() {
        String key = config.getShowEffect() ? "effect.hide" : "effect.show";
        this.setTooltip(Tooltip.of(Text.literal(LocalizationHelper.getLibTranslation(key))));
    }

    /**
     * Gets the button text based on show effect state.
     * 
     * @param config The configuration
     * @return The button text (eye icon)
     */
    private static Text getButtonText(IConfigBoolean config) {
        return config.getShowEffect() ? EYE_VISIBLE : EYE_HIDDEN;
    }

    /**
     * Checks if this button should be visible.
     * <p>
     * Only visible if config has an effect bound.
     * 
     * @return true if should be visible
     */
    public boolean shouldBeVisible() {
        return config.hasEffect();
    }
}
