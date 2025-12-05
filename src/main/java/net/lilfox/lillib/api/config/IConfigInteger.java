package net.lilfox.lillib.api.config;

import net.lilfox.lillib.api.callback.IConfigValueChangeCallback;

/**
 * Interface for integer configuration options.
 * <p>
 * This interface provides integer value management with support for
 * minimum/maximum constraints, slider display, and value change callbacks.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public interface IConfigInteger extends IConfigBase {
    /**
     * Gets the current integer value of this configuration.
     * 
     * @return The current value
     */
    int getIntegerValue();

    /**
     * Sets the integer value of this configuration.
     * <p>
     * The value will be clamped to the minimum and maximum bounds.
     * This will trigger any registered value change callbacks and automatically
     * save the configuration to file.
     * 
     * @param value The new value to set
     */
    void setIntegerValue(int value);

    /**
     * Gets the default integer value of this configuration.
     * 
     * @return The default value
     */
    int getDefaultIntegerValue();

    /**
     * Gets the minimum allowed value for this configuration.
     * 
     * @return The minimum value
     */
    int getMinValue();

    /**
     * Gets the maximum allowed value for this configuration.
     * 
     * @return The maximum value
     */
    int getMaxValue();

    /**
     * Checks if this configuration should be displayed as a slider in the GUI.
     * 
     * @return {@code true} if a slider should be used, {@code false} for a text field
     */
    boolean useSlider();

    /**
     * Sets a callback to be invoked when this configuration's value changes.
     * <p>
     * The callback receives both the new and old values.
     * 
     * @param callback The callback to register, or {@code null} to remove
     */
    void setValueChangeCallback(IConfigValueChangeCallback<Integer> callback);
}
