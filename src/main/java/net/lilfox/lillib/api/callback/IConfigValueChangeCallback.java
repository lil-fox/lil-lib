package net.lilfox.lillib.api.callback;

/**
 * Functional interface for configuration value change callbacks.
 * <p>
 * This callback is invoked when a configuration value changes, providing
 * both the new and old values for comparison.
 * 
 * <p>Example usage:
 * <pre>{@code
 * config.setValueChangeCallback((newValue, oldValue) -> {
 *     System.out.println("Value changed from " + oldValue + " to " + newValue);
 * });
 * }</pre>
 * 
 * @param <T> The type of the configuration value (Boolean, Integer, Double, String, etc.)
 * @author lilfox
 * @since 1.0.0
 */
@FunctionalInterface
public interface IConfigValueChangeCallback<T> {
    /**
     * Called when a configuration value changes.
     * <p>
     * This method is invoked after the new value has been set but before
     * the configuration is saved to file.
     * 
     * @param newValue The new value that was set
     * @param oldValue The previous value before the change
     */
    void onValueChanged(T newValue, T oldValue);
}
