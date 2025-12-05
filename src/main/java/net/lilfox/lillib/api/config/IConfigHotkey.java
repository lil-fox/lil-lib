package net.lilfox.lillib.api.config;

/**
 * Interface for hotkey configuration support.
 * <p>
 * This interface provides hotkey binding management for configuration options.
 * Hotkeys are stored as comma-separated key sequences (e.g., "CTRL,N,M").
 * 
 * @author lilfox
 * @since 1.0.0
 */
public interface IConfigHotkey {
    /**
     * Gets the current hotkey binding as a string.
     * <p>
     * The format is comma-separated key names in the order they should be pressed.
     * An empty string means no hotkey is bound.
     * 
     * @return The hotkey string (e.g., "CTRL,N" or "")
     */
    String getHotkey();

    /**
     * Sets the hotkey binding.
     * <p>
     * The hotkey should be in comma-separated format. An empty string unbinds the hotkey.
     * This will automatically save the configuration and update conflict detection.
     * 
     * @param hotkey The hotkey string to set (e.g., "CTRL,SHIFT,A")
     */
    void setHotkey(String hotkey);

    /**
     * Gets the default hotkey binding.
     * 
     * @return The default hotkey string
     */
    String getDefaultHotkey();

    /**
     * Checks if this hotkey conflicts with any other registered hotkeys.
     * <p>
     * Two hotkeys conflict if they have identical key sequences in the same order.
     * 
     * @return {@code true} if conflicts exist, {@code false} otherwise
     */
    boolean hasConflicts();

    /**
     * Gets a list of conflicting hotkey configurations.
     * <p>
     * Returns all configurations that have the same hotkey binding as this one.
     * 
     * @return Array of conflicting config names in format "ModName > Category > ConfigName"
     */
    String[] getConflicts();
}
