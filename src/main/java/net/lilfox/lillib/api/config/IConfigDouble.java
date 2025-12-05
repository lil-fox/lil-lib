package net.lilfox.lillib.api.config;

import net.lilfox.lillib.api.callback.IConfigValueChangeCallback;

/**
 * Interface for double (decimal) configuration options.
 * <p>
 * This interface provides double value management with support for
 * minimum/maximum constraints, slider display, and value change callbacks.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public interface IConfigDouble extends IConfigBase {
    /**
     * Gets the current double value of this configuration.
     * 
     * @return The current value
     */
    double getDoubleValue();

    /**
     * Sets the double value of this configuration.
     * <p>
     * The value will be clamped to the minimum and maximum bounds.
     * This will trigger any registered value change callbacks and automatically
     * save the configuration to file.
     * 
     * @param value The new value to set
     */
    void setDoubleValue(double value);

    /**
     * Gets the default double value of this configuration.
     * 
     * @return The default value
     */
    double getDefaultDoubleValue();

    /**
     * Gets the minimum allowed value for this configuration.
     * 
     * @return The minimum value
     */
    double getMinValue();

    /**
     * Gets the maximum allowed value for this configuration.
     * 
     * @return The maximum value
     */
    double getMaxValue();

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
    void setValueChangeCallback(IConfigValueChangeCallback<Double> callback);
}
