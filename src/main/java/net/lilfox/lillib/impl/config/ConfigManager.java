package net.lilfox.lillib.impl.config;

import net.lilfox.lillib.api.config.IConfigBase;
import net.lilfox.lillib.api.config.IConfigBooleanHotkeyed;
import net.lilfox.lillib.impl.config.options.ConfigBooleanHotkeyed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager for all configurations.
 * <p>
 * This singleton class manages configuration parsing, storage, and serialization
 * for all mods using lillib.
 *
 * <p><b>Updated for hybrid approach (0.8):</b>
 * <ul>
 *   <li>Works with pre-created config objects from {@link ConfigFactory}</li>
 *   <li>Uses {@link ConfigParser} to extract metadata from {@code @Config} annotations</li>
 *   <li>Supports both {@code parseClass()} and manual {@code register()} methods</li>
 * </ul>
 *
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("lillib");
    private static ConfigManager INSTANCE;

    // Map of modId -> list of configs
    private final Map<String, List<IConfigBase>> configsByMod = new ConcurrentHashMap<>();

    // Map of modId -> category -> list of configs (for GUI)
    private final Map<String, Map<String, List<IConfigBase>>> configsByCategory = new ConcurrentHashMap<>();

    // Map of modId -> configName -> config (for quick lookup)
    private final Map<String, Map<String, IConfigBase>> configsByName = new ConcurrentHashMap<>();

    private ConfigManager() {
    }

    /**
     * Gets the singleton instance.
     *
     * @return The ConfigManager instance
     */
    public static ConfigManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigManager();
        }
        return INSTANCE;
    }

    /**
     * Creates a builder for parsing and loading configurations.
     *
     * @param modId The mod ID
     * @return A new builder instance
     */
    public static Builder create(String modId) {
        return new Builder(modId);
    }

    /**
     * Sets a hotkey activation callback.
     * <p>
     * Convenience method for setting callbacks on hotkeyed boolean configs.
     *
     * @param config The hotkeyed boolean config
     * @param callback The callback to invoke on activation
     */
    public static void setHotkeyCallback(IConfigBooleanHotkeyed config, Runnable callback) {
        config.setActivationCallback(callback);
    }

    /**
     * Registers configurations for a mod.
     * <p>
     * This method organizes configs by category and name for easy lookup.
     *
     * @param modId The mod ID
     * @param configs The list of configurations
     */
    public void registerConfigs(String modId, List<IConfigBase> configs) {
        configsByMod.put(modId, configs);

        // Organize by category
        Map<String, List<IConfigBase>> byCategory = new HashMap<>();
        Map<String, IConfigBase> byName = new HashMap<>();

        for (IConfigBase config : configs) {
            String category = config.getCategory();
            byCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(config);
            byName.put(config.getName(), config);

            LOGGER.debug("Registered config '{}' in category '{}'", config.getName(), category);
        }

        configsByCategory.put(modId, byCategory);
        configsByName.put(modId, byName);

        LOGGER.info("Registered {} configuration(s) for mod '{}'", configs.size(), modId);
    }

    /**
     * Gets all configurations for a mod.
     *
     * @param modId The mod ID
     * @return List of all configurations, or empty list if not found
     */
    public List<IConfigBase> getConfigs(String modId) {
        return configsByMod.getOrDefault(modId, Collections.emptyList());
    }

    /**
     * Gets configurations organized by category.
     *
     * @param modId The mod ID
     * @return Map of category -> list of configs
     */
    public Map<String, List<IConfigBase>> getConfigsByCategory(String modId) {
        return configsByCategory.getOrDefault(modId, Collections.emptyMap());
    }

    /**
     * Gets a specific configuration by name.
     *
     * @param modId The mod ID
     * @param configName The configuration name
     * @return The configuration, or null if not found
     */
    public IConfigBase getConfig(String modId, String configName) {
        Map<String, IConfigBase> configs = configsByName.get(modId);
        return configs != null ? configs.get(configName) : null;
    }

    /**
     * Gets all category names for a mod.
     *
     * @param modId The mod ID
     * @return Set of category names
     */
    public Set<String> getCategories(String modId) {
        Map<String, List<IConfigBase>> categories = configsByCategory.get(modId);
        return categories != null ? categories.keySet() : Collections.emptySet();
    }

    /**
     * Saves configurations for a mod to file.
     *
     * @param modId The mod ID
     */
    public void saveConfig(String modId) {
        List<IConfigBase> configs = getConfigs(modId);
        if (!configs.isEmpty()) {
            ConfigSerializer.saveToFile(modId, configs);
        } else {
            LOGGER.warn("No configs found for mod '{}', nothing to save", modId);
        }
    }

    /**
     * Loads configurations for a mod from file.
     *
     * @param modId The mod ID
     */
    public void loadConfig(String modId) {
        List<IConfigBase> configs = getConfigs(modId);
        if (!configs.isEmpty()) {
            ConfigSerializer.loadFromFile(modId, configs);
        } else {
            LOGGER.warn("No configs found for mod '{}', nothing to load", modId);
        }
    }

    /**
     * Builder for configuration initialization.
     */
    public static class Builder {
        private final String modId;
        private final List<Class<?>> configClasses = new ArrayList<>();
        private final List<IConfigBase> manualConfigs = new ArrayList<>();

        private Builder(String modId) {
            this.modId = modId;
        }

        /**
         * Adds a configuration class to parse.
         * <p>
         * The class should contain {@code static final} fields of type {@link IConfigBase}
         * annotated with {@code @Config}.
         *
         * @param configClass The class containing config objects
         * @return This builder
         */
        public Builder parseClass(Class<?> configClass) {
            LOGGER.debug("Queued config class for parsing: {}", configClass.getName());
            configClasses.add(configClass);
            return this;
        }

        /**
         * Manually registers a single configuration.
         * <p>
         * Use this for configs created outside of a static class structure.
         *
         * @param config The configuration to register
         * @return This builder
         */
        public Builder register(IConfigBase config) {
            LOGGER.debug("Manually registering config: {}", config.getName());
            manualConfigs.add(config);
            return this;
        }

        /**
         * Manually registers multiple configurations.
         *
         * @param configs The configurations to register
         * @return This builder
         */
        public Builder registerAll(IConfigBase... configs) {
            for (IConfigBase config : configs) {
                register(config);
            }
            return this;
        }

        /**
         * Manually registers multiple configurations.
         *
         * @param configs The configurations to register
         * @return This builder
         */
        public Builder registerAll(Collection<IConfigBase> configs) {
            for (IConfigBase config : configs) {
                register(config);
            }
            return this;
        }

        /**
         * Loads configurations from file after building.
         * <p>
         * This should be called after all configs are registered but before
         * any config values are accessed.
         *
         * @return This builder
         */
        public Builder loadFromFile() {
            build();
            getInstance().loadConfig(modId);
            return this;
        }

        /**
         * Builds and registers all configurations.
         * <p>
         * This method:
         * <ol>
         *   <li>Parses all queued config classes</li>
         *   <li>Adds manually registered configs</li>
         *   <li>Registers everything with the ConfigManager</li>
         * </ol>
         *
         * @return The ConfigManager instance
         */
        public ConfigManager build() {
            List<IConfigBase> allConfigs = new ArrayList<>();

            // Parse all config classes
            for (Class<?> configClass : configClasses) {
                LOGGER.info("Parsing config class: {}", configClass.getName());

                // Validate class structure
                if (!ConfigParser.validateConfigClass(configClass)) {
                    LOGGER.error("Config class validation failed: {}", configClass.getName());
                    continue;
                }

                List<IConfigBase> configs = ConfigParser.parseConfigClass(configClass, modId);
                allConfigs.addAll(configs);

                LOGGER.info("Parsed {} config(s) from {}", configs.size(), configClass.getSimpleName());
            }

            // Add manually registered configs
            if (!manualConfigs.isEmpty()) {
                LOGGER.info("Adding {} manually registered config(s)", manualConfigs.size());

                for (IConfigBase config : manualConfigs) {
                    // Ensure modId is set
                    if (config.getModId() == null) {
                        config.setModId(modId);
                    }
                    allConfigs.add(config);
                }
            }

            if (allConfigs.isEmpty()) {
                LOGGER.warn("No configs registered for mod '{}'", modId);
            }

            // Register with manager
            getInstance().registerConfigs(modId, allConfigs);

            return getInstance();
        }
    }
}