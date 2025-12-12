package net.lilfox.lillib.impl.config;

import net.lilfox.lillib.api.annotation.Config;
import net.lilfox.lillib.api.config.IConfigBase;
import net.lilfox.lillib.impl.config.options.ConfigBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses configuration classes and extracts config objects with their annotations.
 * <p>
 * This parser operates on pre-created config objects (created via {@link ConfigFactory})
 * rather than creating them from annotated primitive fields. It extracts metadata from
 * the {@link Config} annotation and applies it to the config objects.
 *
 * <p><b>Simplified version:</b> Uses ConfigBase.setCategory() method
 *
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigParser {
    private static final Logger LOGGER = LoggerFactory.getLogger("lillib");

    /**
     * Parses a configuration class and extracts all annotated config objects.
     * <p>
     * This method scans for {@code static final} fields that:
     * <ul>
     *   <li>Are of type {@link IConfigBase}</li>
     *   <li>Have the {@link Config} annotation</li>
     * </ul>
     *
     * @param configClass The class containing @Config annotated config fields
     * @param modId The mod ID to assign to configurations
     * @return List of parsed configuration instances
     */
    public static List<IConfigBase> parseConfigClass(Class<?> configClass, String modId) {
        List<IConfigBase> configs = new ArrayList<>();

        LOGGER.info("Parsing config class: {} for mod: {}", configClass.getName(), modId);

        for (Field field : configClass.getDeclaredFields()) {
            // Only process static final fields
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
                continue;
            }

            // Check for @Config annotation
            Config configAnnotation = field.getAnnotation(Config.class);
            if (configAnnotation == null) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object fieldValue = field.get(null);

                // Field must be a config object
                if (!(fieldValue instanceof IConfigBase config)) {
                    LOGGER.warn("Field '{}' has @Config annotation but is not an IConfigBase instance: {}",
                            field.getName(), fieldValue.getClass().getName());
                    continue;
                }

                // Apply metadata from annotation
                String category = configAnnotation.category();
                applyMetadata(config, category, modId);

                configs.add(config);

                LOGGER.info("Parsed config: name='{}', type={}, category='{}'",
                        config.getName(), config.getClass().getSimpleName(), config.getCategory());

            } catch (Exception e) {
                LOGGER.error("Failed to parse config field: {}", field.getName(), e);
            }
        }

        LOGGER.info("Successfully parsed {} config(s) from class: {}", configs.size(), configClass.getSimpleName());
        return configs;
    }

    /**
     * Applies metadata from the @Config annotation to a config object.
     * <p>
     * This method sets the category and ensures the modId is set.
     *
     * @param config The configuration object
     * @param category The category from @Config annotation
     * @param modId The mod ID to assign
     */
    private static void applyMetadata(IConfigBase config, String category, String modId) {
        // Set modId if not already set
        if (config.getModId() == null) {
            config.setModId(modId);
            LOGGER.debug("Set modId '{}' for config '{}'", modId, config.getName());
        }

        // Set category using the package-private setCategory method
        if (config instanceof ConfigBase configBase) {
            configBase.setCategory(category);
            LOGGER.debug("Set category '{}' for config '{}'", category, config.getName());
        } else {
            LOGGER.warn("Config '{}' is not a ConfigBase instance, cannot set category", config.getName());
        }
    }

    /**
     * Validates that a config class follows the required structure.
     *
     * @param configClass The class to validate
     * @return true if valid, false otherwise
     */
    public static boolean validateConfigClass(Class<?> configClass) {
        boolean hasConfigs = false;
        boolean allValid = true;

        for (Field field : configClass.getDeclaredFields()) {
            Config configAnnotation = field.getAnnotation(Config.class);
            if (configAnnotation == null) {
                continue;
            }

            hasConfigs = true;

            // Check modifiers
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                LOGGER.error("Config field '{}' must be static", field.getName());
                allValid = false;
            }
            if (!Modifier.isFinal(modifiers)) {
                LOGGER.error("Config field '{}' must be final", field.getName());
                allValid = false;
            }

            // Check type
            if (!IConfigBase.class.isAssignableFrom(field.getType())) {
                LOGGER.error("Config field '{}' must be an IConfigBase type, found: {}",
                        field.getName(), field.getType().getName());
                allValid = false;
            }

            // Check if it extends ConfigBase
            if (!ConfigBase.class.isAssignableFrom(field.getType())) {
                LOGGER.warn("Config field '{}' does not extend ConfigBase, category might not be set correctly",
                        field.getName());
            }
        }

        if (!hasConfigs) {
            LOGGER.warn("Config class '{}' has no @Config annotated fields", configClass.getName());
            return false;
        }

        return allValid;
    }
}