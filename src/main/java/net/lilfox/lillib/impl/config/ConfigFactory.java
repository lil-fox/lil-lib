package net.lilfox.lillib.impl.config;

import net.lilfox.lillib.impl.config.options.*;

/**
 * Factory for creating configuration objects with consistent mod ID.
 * <p>
 * This factory simplifies config creation by requiring the mod ID only once
 * during factory instantiation. It provides convenient overloads with sensible
 * defaults for all configuration types.
 * 
 * <p>Default values:
 * <ul>
 *   <li>Boolean: {@code false}</li>
 *   <li>Boolean with Hotkey: {@code false}, empty hotkey</li>
 *   <li>Integer: {@code 0}, range: {@code -10} to {@code 10}</li>
 *   <li>Double: {@code 0.5}, range: {@code 0.0} to {@code 1.0}</li>
 *   <li>String: {@code ""}</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * public class MyConfigs {
 *     private static final ConfigFactory factory = new ConfigFactory("mymod");
 *     
 *     @Config(category = "general")
 *     public static final ConfigBoolean feature = factory.createBoolean("feature");
 *     
 *     @Config(category = "general")
 *     public static final ConfigBooleanHotkeyed openGui = 
 *         factory.createBooleanHotkeyed("openGui", "U,C");
 *     
 *     @Config(category = "tweaks")
 *     public static final ConfigInteger value = factory.createInteger("value");
 * }
 * }</pre>
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigFactory {
    private final String modId;

    /**
     * Creates a new configuration factory.
     * 
     * @param modId The mod ID to use for all configs created by this factory
     * @throws NullPointerException if modId is null
     */
    public ConfigFactory(String modId) {
        if (modId == null) {
            throw new NullPointerException("modId cannot be null");
        }
        this.modId = modId;
    }

    /**
     * Gets the mod ID for this factory.
     * 
     * @return The mod ID
     */
    public String getModId() {
        return modId;
    }

    // ===== Boolean Configs =====

    /**
     * Creates a boolean configuration with default value {@code false}.
     * 
     * @param name The configuration name
     * @return A new ConfigBoolean instance
     */
    public ConfigBoolean createBoolean(String name) {
        return createBoolean(name, false);
    }

    /**
     * Creates a boolean configuration with specified default value.
     * 
     * @param name The configuration name
     * @param defaultValue The default value
     * @return A new ConfigBoolean instance
     */
    public ConfigBoolean createBoolean(String name, boolean defaultValue) {
        ConfigBoolean config = new ConfigBoolean(name, "", defaultValue);
        config.setModId(modId);
        return config;
    }

    // ===== Boolean with Hotkey Configs =====

    /**
     * Creates a hotkeyed boolean configuration with defaults: {@code false}, empty hotkey.
     * 
     * @param name The configuration name
     * @return A new ConfigBooleanHotkeyed instance
     */
    public ConfigBooleanHotkeyed createBooleanHotkeyed(String name) {
        return createBooleanHotkeyed(name, false, "");
    }

    /**
     * Creates a hotkeyed boolean configuration with default value {@code false}.
     * 
     * @param name The configuration name
     * @param defaultHotkey The default hotkey binding (e.g., "CTRL,N")
     * @return A new ConfigBooleanHotkeyed instance
     */
    public ConfigBooleanHotkeyed createBooleanHotkeyed(String name, String defaultHotkey) {
        return createBooleanHotkeyed(name, false, defaultHotkey);
    }

    /**
     * Creates a hotkeyed boolean configuration with specified values.
     * 
     * @param name The configuration name
     * @param defaultValue The default boolean value
     * @param defaultHotkey The default hotkey binding (e.g., "CTRL,N")
     * @return A new ConfigBooleanHotkeyed instance
     */
    public ConfigBooleanHotkeyed createBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey) {
        ConfigBooleanHotkeyed config = new ConfigBooleanHotkeyed(name, "", defaultValue, defaultHotkey);
        config.setModId(modId);
        return config;
    }

    // ===== Integer Configs =====

    /**
     * Creates an integer configuration with defaults: value {@code 0}, range {@code -10} to {@code 10}.
     * 
     * @param name The configuration name
     * @return A new ConfigInteger instance
     */
    public ConfigInteger createInteger(String name) {
        return createInteger(name, 0, -10, 10, false);
    }

    /**
     * Creates an integer configuration with default range {@code -10} to {@code 10}.
     * 
     * @param name The configuration name
     * @param defaultValue The default value
     * @return A new ConfigInteger instance
     */
    public ConfigInteger createInteger(String name, int defaultValue) {
        return createInteger(name, defaultValue, -10, 10, false);
    }

    /**
     * Creates an integer configuration with specified range.
     * 
     * @param name The configuration name
     * @param defaultValue The default value
     * @param minValue The minimum allowed value
     * @param maxValue The maximum allowed value
     * @return A new ConfigInteger instance
     */
    public ConfigInteger createInteger(String name, int defaultValue, int minValue, int maxValue) {
        return createInteger(name, defaultValue, minValue, maxValue, false);
    }

    /**
     * Creates an integer configuration with full customization.
     * 
     * @param name The configuration name
     * @param defaultValue The default value
     * @param minValue The minimum allowed value
     * @param maxValue The maximum allowed value
     * @param useSlider Whether to display as a slider in GUI
     * @return A new ConfigInteger instance
     */
    public ConfigInteger createInteger(String name, int defaultValue, int minValue, int maxValue, boolean useSlider) {
        ConfigInteger config = new ConfigInteger(name, "", defaultValue, minValue, maxValue, useSlider);
        config.setModId(modId);
        return config;
    }

    // ===== Double Configs =====

    /**
     * Creates a double configuration with defaults: value {@code 0.5}, range {@code 0.0} to {@code 1.0}.
     * 
     * @param name The configuration name
     * @return A new ConfigDouble instance
     */
    public ConfigDouble createDouble(String name) {
        return createDouble(name, 0.5, 0.0, 1.0, false);
    }

    /**
     * Creates a double configuration with default range {@code 0.0} to {@code 1.0}.
     * 
     * @param name The configuration name
     * @param defaultValue The default value
     * @return A new ConfigDouble instance
     */
    public ConfigDouble createDouble(String name, double defaultValue) {
        return createDouble(name, defaultValue, 0.0, 1.0, false);
    }

    /**
     * Creates a double configuration with specified range.
     * 
     * @param name The configuration name
     * @param defaultValue The default value
     * @param minValue The minimum allowed value
     * @param maxValue The maximum allowed value
     * @return A new ConfigDouble instance
     */
    public ConfigDouble createDouble(String name, double defaultValue, double minValue, double maxValue) {
        return createDouble(name, defaultValue, minValue, maxValue, false);
    }

    /**
     * Creates a double configuration with full customization.
     * 
     * @param name The configuration name
     * @param defaultValue The default value
     * @param minValue The minimum allowed value
     * @param maxValue The maximum allowed value
     * @param useSlider Whether to display as a slider in GUI
     * @return A new ConfigDouble instance
     */
    public ConfigDouble createDouble(String name, double defaultValue, double minValue, double maxValue, boolean useSlider) {
        ConfigDouble config = new ConfigDouble(name, "", defaultValue, minValue, maxValue, useSlider);
        config.setModId(modId);
        return config;
    }

    // ===== String Configs =====

    /**
     * Creates a string configuration with default value {@code ""}.
     * 
     * @param name The configuration name
     * @return A new ConfigString instance
     */
    public ConfigString createString(String name) {
        return createString(name, "");
    }

    /**
     * Creates a string configuration with specified default value.
     * 
     * @param name The configuration name
     * @param defaultValue The default value
     * @return A new ConfigString instance
     */
    public ConfigString createString(String name, String defaultValue) {
        ConfigString config = new ConfigString(name, "", defaultValue);
        config.setModId(modId);
        return config;
    }
}
