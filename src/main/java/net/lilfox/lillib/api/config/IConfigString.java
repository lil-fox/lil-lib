package net.lilfox.lillib.api.config;

import net.lilfox.lillib.api.callback.IConfigValueChangeCallback;

/**
 * Interface for string configuration options.
 * <p>
 * This interface provides string value management with support for
 * value change callbacks.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public interface IConfigString extends IConfigBase {
    /**
     * Gets the current string value of this configuration.
     * 
     * @return The current value
     */
    String getStringValue();

    /**
     * Sets the string value of this configuration.
     * <p>
     * This will trigger any registered value change callbacks and automatically
     * save the configuration to file.
     * 
     * @param value The new value to set
     */
    void setStringValue(String value);

    /**
     * Gets the default string value of this configuration.
     * 
     * @return The default value
     */
    String getDefaultStringValue();

    /**
     * Sets a callback to be invoked when this configuration's value changes.
     * <p>
     * The callback receives both the new and old values.
     * 
     * @param callback The callback to register, or {@code null} to remove
     */
    void setValueChangeCallback(IConfigValueChangeCallback<String> callback);
}
