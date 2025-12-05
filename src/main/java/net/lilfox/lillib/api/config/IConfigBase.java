package net.lilfox.lillib.api.config;

/**
 * Base interface for all configuration types.
 * <p>
 * This interface provides common functionality for configuration options including
 * name management, category assignment, localization, and modification tracking.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public interface IConfigBase {
    /**
     * Gets the internal name of this configuration.
     * <p>
     * This is typically the field name from the configuration class.
     * 
     * @return The configuration name
     */
    String getName();

    /**
     * Gets the category this configuration belongs to.
     * <p>
     * Categories are used to organize configurations into tabs in the GUI.
     * 
     * @return The category name
     */
    String getCategory();

    /**
     * Gets the display name for this configuration.
     * <p>
     * This retrieves the localized name using the key: {@code modId.config.name}
     * 
     * @return The localized display name
     */
    String getDisplayName();

    /**
     * Gets the nice name for notifications.
     * <p>
     * This retrieves the localized nice name using the key: {@code modId.config.name.nice}
     * Used for hotbar notifications when the config value changes.
     * 
     * @return The localized nice name
     */
    String getNiceName();

    /**
     * Gets the description tooltip for this configuration.
     * <p>
     * This retrieves the localized description using the key: {@code modId.config.name.desc}
     * 
     * @return The localized description
     */
    String getDescription();

    /**
     * Checks if this configuration has been modified from its default value.
     * <p>
     * This is used to determine which configurations need to be saved to the config file.
     * 
     * @return {@code true} if the value differs from default, {@code false} otherwise
     */
    boolean isModified();

    /**
     * Resets this configuration to its default value.
     */
    void resetToDefault();

    /**
     * Gets the mod ID this configuration belongs to.
     * <p>
     * Used for localization key generation and file naming.
     * 
     * @return The mod ID
     */
    String getModId();

    /**
     * Sets the mod ID for this configuration.
     * <p>
     * This is typically called automatically by the ConfigManager during initialization.
     * 
     * @param modId The mod ID to set
     */
    void setModId(String modId);
}
