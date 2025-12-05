package net.lilfox.lillib.api.config;

/**
 * Interface for boolean configurations with hotkey support.
 * <p>
 * This interface combines boolean configuration functionality with hotkey bindings,
 * allowing boolean values to be toggled via keyboard shortcuts.
 * 
 * <p>When the hotkey is pressed, the boolean value is toggled and an optional
 * activation callback is invoked.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public interface IConfigBooleanHotkeyed extends IConfigBoolean, IConfigHotkey {
    /**
     * Sets a callback to be invoked when the hotkey is activated.
     * <p>
     * This callback is called after the boolean value is toggled but before
     * the value change callback. It's typically used to perform additional
     * actions when the hotkey is pressed (e.g., opening a GUI).
     * 
     * <p>Note: The callback is only invoked when activated via hotkey,
     * not when the value is changed programmatically or through the GUI.
     * 
     * @param callback The callback to invoke on hotkey activation, or {@code null} to remove
     */
    void setActivationCallback(Runnable callback);

    /**
     * Gets the currently registered activation callback.
     * 
     * @return The activation callback, or {@code null} if none is set
     */
    Runnable getActivationCallback();

    /**
     * Checks if this configuration has an activation callback registered.
     * 
     * @return {@code true} if a callback is set, {@code false} otherwise
     */
    boolean hasActivationCallback();
}
