package net.lilfox.lillib.api.config;

import net.lilfox.lillib.api.callback.IConfigValueChangeCallback;

/**
 * Interface for boolean configuration options.
 * <p>
 * This interface provides boolean value management with support for value change callbacks
 * and effect binding for visual indicators in the HUD.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public interface IConfigBoolean extends IConfigBase {
    /**
     * Gets the current boolean value of this configuration.
     * 
     * @return The current value
     */
    boolean getBooleanValue();

    /**
     * Sets the boolean value of this configuration.
     * <p>
     * This will trigger any registered value change callbacks and automatically
     * save the configuration to file.
     * 
     * @param value The new value to set
     */
    void setBooleanValue(boolean value);

    /**
     * Gets the default boolean value of this configuration.
     * 
     * @return The default value
     */
    boolean getDefaultBooleanValue();

    /**
     * Toggles the boolean value of this configuration.
     * <p>
     * Equivalent to calling {@code setBooleanValue(!getBooleanValue())}
     */
    void toggleBooleanValue();

    /**
     * Sets a callback to be invoked when this configuration's value changes.
     * <p>
     * The callback receives both the new and old values.
     * 
     * @param callback The callback to register, or {@code null} to remove
     */
    void setValueChangeCallback(IConfigValueChangeCallback<Boolean> callback);

    /**
     * Checks if this configuration has a fake effect bound to it.
     * <p>
     * Fake effects are visual indicators displayed in the HUD when the config is enabled.
     * 
     * @return {@code true} if an effect is bound, {@code false} otherwise
     */
    boolean hasEffect();

    /**
     * Binds a fake effect to this configuration.
     * <p>
     * This is typically called automatically by {@link net.lilfox.lillib.api.effect.EffectRegistry}
     * when registering an effect. Should not be called manually.
     */
    void bindEffect();

    /**
     * Checks if the bound effect should be displayed.
     * <p>
     * This combines three conditions:
     * <ul>
     *   <li>An effect is bound ({@link #hasEffect()})</li>
     *   <li>The effect display is enabled ({@link #getShowEffect()})</li>
     *   <li>The configuration value is {@code true} ({@link #getBooleanValue()})</li>
     * </ul>
     * 
     * @return {@code true} if the effect should be shown, {@code false} otherwise
     */
    boolean shouldShowEffect();

    /**
     * Sets whether the bound effect should be displayed in the HUD.
     * <p>
     * This allows users to hide effects even when the configuration is enabled,
     * to avoid HUD clutter.
     * 
     * @param show {@code true} to show the effect, {@code false} to hide it
     */
    void setShowEffect(boolean show);

    /**
     * Gets whether the bound effect display is enabled.
     * 
     * @return {@code true} if effect display is enabled, {@code false} otherwise
     */
    boolean getShowEffect();

    /**
     * Gets the default value for effect display.
     * 
     * @return The default show effect value (typically {@code true})
     */
    boolean getDefaultShowEffect();
}
