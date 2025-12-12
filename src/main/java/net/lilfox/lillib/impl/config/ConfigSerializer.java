package net.lilfox.lillib.impl.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.lilfox.lillib.api.config.*;
import net.lilfox.lillib.impl.config.options.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles JSON serialization and deserialization of configurations.
 * <p>
 * Configurations are organized by category in a nested JSON structure.
 * Only modified configurations are saved to reduce file size.
 *
 * <p><b>Fixed version:</b>
 * - Sets loading flag before deserialization to prevent auto-save
 * - Clears loading flag after deserialization
 *
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigSerializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("lillib");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Saves configurations to a JSON file.
     * <p>
     * The file is saved as "config/modId.json" with nested structure by category.
     * Only modified configurations are included.
     *
     * @param modId The mod ID (used as filename)
     * @param configs The list of configurations to save
     */
    public static void saveToFile(String modId, List<IConfigBase> configs) {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configFile = configDir.resolve(modId + ".json");

        LOGGER.info("=== SAVING CONFIG FILE: {} ===", configFile);
        LOGGER.debug("Total configs to process: {}", configs.size());

        try {
            // Build nested structure: category -> configName -> properties
            JsonObject root = new JsonObject();

            // Group configs by category
            Map<String, Map<String, IConfigBase>> byCategory = new HashMap<>();
            int modifiedCount = 0;

            for (IConfigBase config : configs) {
                if (config.isModified()) {
                    byCategory.computeIfAbsent(config.getCategory(), k -> new HashMap<>())
                            .put(config.getName(), config);
                    modifiedCount++;
                    LOGGER.debug("Config '{}' is modified, will be saved", config.getName());
                } else {
                    LOGGER.debug("Config '{}' is not modified, skipping", config.getName());
                }
            }

            LOGGER.info("Modified configs to save: {}", modifiedCount);

            // Serialize each category
            for (Map.Entry<String, Map<String, IConfigBase>> categoryEntry : byCategory.entrySet()) {
                String category = categoryEntry.getKey();
                JsonObject categoryObject = new JsonObject();

                LOGGER.debug("Serializing category: {}", category);

                for (Map.Entry<String, IConfigBase> configEntry : categoryEntry.getValue().entrySet()) {
                    String configName = configEntry.getKey();
                    IConfigBase config = configEntry.getValue();

                    JsonObject configObject = serializeConfig(config);
                    categoryObject.add(configName, configObject);

                    LOGGER.debug("  Serialized config '{}': {}", configName, configObject);
                }

                root.add(category, categoryObject);
            }

            // Write to file
            String json = GSON.toJson(root);
            Files.writeString(configFile, json);

            LOGGER.info("Successfully saved {} modified config(s) to: {}", modifiedCount, configFile);
            LOGGER.debug("Saved JSON: {}", json);

        } catch (IOException e) {
            LOGGER.error("Failed to save config file for mod '{}'", modId, e);
        }
    }

    /**
     * Loads configurations from a JSON file.
     * <p>
     * Reads from "config/modId.json" and applies values to the provided configurations.
     * Obsolete configurations in the file are ignored with a warning.
     *
     * @param modId The mod ID (used as filename)
     * @param configs The list of configurations to load values into
     */
    public static void loadFromFile(String modId, List<IConfigBase> configs) {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configFile = configDir.resolve(modId + ".json");

        LOGGER.info("=== LOADING CONFIG FILE: {} ===", configFile);

        if (!Files.exists(configFile)) {
            LOGGER.info("No config file found for mod '{}', using defaults", modId);
            return;
        }

        try {
            String json = Files.readString(configFile);
            LOGGER.debug("Read JSON from file: {}", json);

            JsonObject root = GSON.fromJson(json, JsonObject.class);

            if (root == null) {
                LOGGER.error("Failed to parse JSON from config file: {}", configFile);
                return;
            }

            // Create lookup map for configs
            Map<String, Map<String, IConfigBase>> configMap = new HashMap<>();
            for (IConfigBase config : configs) {
                configMap.computeIfAbsent(config.getCategory(), k -> new HashMap<>())
                        .put(config.getName(), config);
            }

            LOGGER.debug("Created config lookup map with {} categor(ies)", configMap.size());

            int loadedCount = 0;
            int obsoleteCount = 0;

            // Load each category
            for (Map.Entry<String, JsonElement> categoryEntry : root.entrySet()) {
                String category = categoryEntry.getKey();

                LOGGER.debug("Processing category: {}", category);

                if (!categoryEntry.getValue().isJsonObject()) {
                    LOGGER.warn("Category '{}' is not a JSON object, skipping", category);
                    continue;
                }

                JsonObject categoryObject = categoryEntry.getValue().getAsJsonObject();
                Map<String, IConfigBase> categoryConfigs = configMap.get(category);

                if (categoryConfigs == null) {
                    LOGGER.warn("Ignoring obsolete category '{}' in config file", category);
                    obsoleteCount += categoryObject.size();
                    continue;
                }

                // Load each config in category
                for (Map.Entry<String, JsonElement> configEntry : categoryObject.entrySet()) {
                    String configName = configEntry.getKey();
                    IConfigBase config = categoryConfigs.get(configName);

                    if (config == null) {
                        LOGGER.warn("Ignoring obsolete config '{}.{}' in config file", category, configName);
                        obsoleteCount++;
                        continue;
                    }

                    if (!configEntry.getValue().isJsonObject()) {
                        LOGGER.warn("Config '{}.{}' value is not a JSON object", category, configName);
                        continue;
                    }

                    JsonObject configObject = configEntry.getValue().getAsJsonObject();
                    LOGGER.debug("Loading config '{}.{}' from: {}", category, configName, configObject);

                    // CRITICAL: Set loading flag before deserialization
                    if (config instanceof ConfigBase) {
                        ((ConfigBase) config).setLoading(true);
                    }

                    try {
                        deserializeConfig(config, configObject);
                        loadedCount++;
                    } finally {
                        // CRITICAL: Clear loading flag after deserialization
                        if (config instanceof ConfigBase) {
                            ((ConfigBase) config).setLoading(false);
                        }
                    }
                }
            }

            LOGGER.info("Successfully loaded {} config(s) from file: {}", loadedCount, configFile);
            if (obsoleteCount > 0) {
                LOGGER.info("Ignored {} obsolete config(s) from file", obsoleteCount);
            }

        } catch (Exception e) {
            LOGGER.error("Failed to load config file for mod '{}'", modId, e);
        }
    }

    /**
     * Serializes a single configuration to JSON.
     *
     * @param config The configuration to serialize
     * @return JSON object containing the config properties
     */
    private static JsonObject serializeConfig(IConfigBase config) {
        JsonObject obj = new JsonObject();

        if (config instanceof IConfigBoolean) {
            IConfigBoolean boolConfig = (IConfigBoolean) config;
            obj.addProperty("value", boolConfig.getBooleanValue());

            if (boolConfig.hasEffect()) {
                obj.addProperty("showEffect", boolConfig.getShowEffect());
            }
        }
        else if (config instanceof IConfigInteger) {
            IConfigInteger intConfig = (IConfigInteger) config;
            obj.addProperty("value", intConfig.getIntegerValue());
        }
        else if (config instanceof IConfigDouble) {
            IConfigDouble doubleConfig = (IConfigDouble) config;
            obj.addProperty("value", doubleConfig.getDoubleValue());
        }
        else if (config instanceof IConfigString) {
            IConfigString stringConfig = (IConfigString) config;
            obj.addProperty("value", stringConfig.getStringValue());
        }

        // Add hotkey if present
        if (config instanceof IConfigBooleanHotkeyed) {
            IConfigBooleanHotkeyed hotkeyConfig = (IConfigBooleanHotkeyed) config;
            obj.addProperty("hotkey", hotkeyConfig.getHotkey());
        }

        return obj;
    }

    /**
     * Deserializes a JSON object into a configuration.
     *
     * @param config The configuration to load values into
     * @param obj The JSON object containing the values
     */
    private static void deserializeConfig(IConfigBase config, JsonObject obj) {
        try {
            LOGGER.debug("Deserializing config '{}' of type {}", config.getName(), config.getClass().getSimpleName());

            if (config instanceof ConfigBoolean) {
                ConfigBoolean boolConfig = (ConfigBoolean) config;

                if (obj.has("value")) {
                    boolean value = obj.get("value").getAsBoolean();
                    LOGGER.debug("  Setting boolean value: {} -> {}", boolConfig.getBooleanValue(), value);
                    boolConfig.setBooleanValue(value);
                } else {
                    LOGGER.warn("  Missing 'value' field for boolean config");
                }

                if (obj.has("showEffect") && boolConfig.hasEffect()) {
                    boolean showEffect = obj.get("showEffect").getAsBoolean();
                    LOGGER.debug("  Setting showEffect: {} -> {}", boolConfig.getShowEffect(), showEffect);
                    boolConfig.setShowEffect(showEffect);
                }
            }
            else if (config instanceof ConfigInteger) {
                ConfigInteger intConfig = (ConfigInteger) config;

                if (obj.has("value")) {
                    int value = obj.get("value").getAsInt();
                    LOGGER.debug("  Setting integer value: {} -> {}", intConfig.getIntegerValue(), value);
                    intConfig.setIntegerValue(value);
                } else {
                    LOGGER.warn("  Missing 'value' field for integer config");
                }
            }
            else if (config instanceof ConfigDouble) {
                ConfigDouble doubleConfig = (ConfigDouble) config;

                if (obj.has("value")) {
                    double value = obj.get("value").getAsDouble();
                    LOGGER.debug("  Setting double value: {} -> {}", doubleConfig.getDoubleValue(), value);
                    doubleConfig.setDoubleValue(value);
                } else {
                    LOGGER.warn("  Missing 'value' field for double config");
                }
            }
            else if (config instanceof ConfigString) {
                ConfigString stringConfig = (ConfigString) config;

                if (obj.has("value")) {
                    String value = obj.get("value").getAsString();
                    LOGGER.debug("  Setting string value: {} -> {}", stringConfig.getStringValue(), value);
                    stringConfig.setStringValue(value);
                } else {
                    LOGGER.warn("  Missing 'value' field for string config");
                }
            }

            // Load hotkey if present
            if (config instanceof ConfigBooleanHotkeyed) {
                ConfigBooleanHotkeyed hotkeyConfig = (ConfigBooleanHotkeyed) config;

                if (obj.has("hotkey")) {
                    String hotkey = obj.get("hotkey").getAsString();
                    LOGGER.debug("  Setting hotkey: {} -> {}", hotkeyConfig.getHotkey(), hotkey);
                    hotkeyConfig.setHotkey(hotkey);
                }
            }

            LOGGER.debug("Successfully deserialized config '{}'", config.getName());

        } catch (Exception e) {
            LOGGER.error("Failed to deserialize config '{}': {}", config.getName(), e.getMessage(), e);
        }
    }
}