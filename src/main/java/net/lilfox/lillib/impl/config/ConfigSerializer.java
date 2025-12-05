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

        try {
            // Build nested structure: category -> configName -> properties
            JsonObject root = new JsonObject();
            
            // Group configs by category
            Map<String, Map<String, IConfigBase>> byCategory = new HashMap<>();
            for (IConfigBase config : configs) {
                if (config.isModified()) {
                    byCategory.computeIfAbsent(config.getCategory(), k -> new HashMap<>())
                              .put(config.getName(), config);
                }
            }

            // Serialize each category
            for (Map.Entry<String, Map<String, IConfigBase>> categoryEntry : byCategory.entrySet()) {
                String category = categoryEntry.getKey();
                JsonObject categoryObject = new JsonObject();

                for (Map.Entry<String, IConfigBase> configEntry : categoryEntry.getValue().entrySet()) {
                    String configName = configEntry.getKey();
                    IConfigBase config = configEntry.getValue();
                    
                    JsonObject configObject = serializeConfig(config);
                    categoryObject.add(configName, configObject);
                }

                root.add(category, categoryObject);
            }

            // Write to file
            String json = GSON.toJson(root);
            Files.writeString(configFile, json);
            
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

        if (!Files.exists(configFile)) {
            LOGGER.info("No config file found for mod '{}', using defaults", modId);
            return;
        }

        try {
            String json = Files.readString(configFile);
            JsonObject root = GSON.fromJson(json, JsonObject.class);

            // Create lookup map for configs
            Map<String, Map<String, IConfigBase>> configMap = new HashMap<>();
            for (IConfigBase config : configs) {
                configMap.computeIfAbsent(config.getCategory(), k -> new HashMap<>())
                         .put(config.getName(), config);
            }

            // Load each category
            for (Map.Entry<String, JsonElement> categoryEntry : root.entrySet()) {
                String category = categoryEntry.getKey();
                
                if (!categoryEntry.getValue().isJsonObject()) {
                    continue;
                }
                
                JsonObject categoryObject = categoryEntry.getValue().getAsJsonObject();
                Map<String, IConfigBase> categoryConfigs = configMap.get(category);

                if (categoryConfigs == null) {
                    LOGGER.warn("Ignoring obsolete category '{}' in config file", category);
                    continue;
                }

                // Load each config in category
                for (Map.Entry<String, JsonElement> configEntry : categoryObject.entrySet()) {
                    String configName = configEntry.getKey();
                    IConfigBase config = categoryConfigs.get(configName);

                    if (config == null) {
                        LOGGER.warn("Ignoring obsolete config '{}.{}' in config file", category, configName);
                        continue;
                    }

                    if (!configEntry.getValue().isJsonObject()) {
                        continue;
                    }

                    JsonObject configObject = configEntry.getValue().getAsJsonObject();
                    deserializeConfig(config, configObject);
                }
            }

            LOGGER.info("Loaded config file for mod '{}'", modId);
            
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
            if (config instanceof ConfigBoolean) {
                ConfigBoolean boolConfig = (ConfigBoolean) config;
                
                if (obj.has("value")) {
                    boolConfig.setBooleanValue(obj.get("value").getAsBoolean());
                }
                
                if (obj.has("showEffect") && boolConfig.hasEffect()) {
                    boolConfig.setShowEffect(obj.get("showEffect").getAsBoolean());
                }
            }
            else if (config instanceof ConfigInteger) {
                ConfigInteger intConfig = (ConfigInteger) config;
                
                if (obj.has("value")) {
                    intConfig.setIntegerValue(obj.get("value").getAsInt());
                }
            }
            else if (config instanceof ConfigDouble) {
                ConfigDouble doubleConfig = (ConfigDouble) config;
                
                if (obj.has("value")) {
                    doubleConfig.setDoubleValue(obj.get("value").getAsDouble());
                }
            }
            else if (config instanceof ConfigString) {
                ConfigString stringConfig = (ConfigString) config;
                
                if (obj.has("value")) {
                    stringConfig.setStringValue(obj.get("value").getAsString());
                }
            }

            // Load hotkey if present
            if (config instanceof ConfigBooleanHotkeyed) {
                ConfigBooleanHotkeyed hotkeyConfig = (ConfigBooleanHotkeyed) config;
                
                if (obj.has("hotkey")) {
                    hotkeyConfig.setHotkey(obj.get("hotkey").getAsString());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to deserialize config '{}'", config.getName(), e);
        }
    }
}
