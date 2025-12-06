package net.lilfox.lillib.impl.config;

import net.lilfox.lillib.api.annotation.Config;
import net.lilfox.lillib.api.config.IConfigBase;
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
 * <p>Key differences from the old approach:
 * <ul>
 *   <li>Parses {@code static final} fields of type {@link IConfigBase}</li>
 *   <li>Configs are created by developer via ConfigFactory before parsing</li>
 *   <li>Parser only extracts and applies metadata from @Config annotation</li>
 *   <li>No reflection-based object creation</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * public class Configs {
 *     private static final ConfigFactory factory = new ConfigFactory("mymod");
 *
 *     @Config(category = "general")
 *     public static final ConfigBoolean feature = factory.createBoolean("feature");
 * }
 *
 * // Parser extracts the ConfigBoolean object and sets its category to "general"
 * List<IConfigBase> configs = ConfigParser.parseConfigClass(Configs.class, "mymod");
 * }</pre>
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
     * <p>For each found field:
     * <ul>
     *   <li>Extracts the config object</li>
     *   <li>Applies category from @Config annotation</li>
     *   <li>Sets modId if not already set</li>
     *   <li>Adds to the returned list</li>
     * </ul>
     *
     * @param configClass The class containing @Config annotated config fields
     * @param modId The mod ID to assign to configurations
     * @return List of parsed configuration instances
     */
    public static List<IConfigBase> parseConfigClass(Class<?> configClass, String modId) {
        List<IConfigBase> configs = new ArrayList<>();

        LOGGER.debug("Parsing config class: {} for mod: {}", configClass.getName(), modId);

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
                if (!(fieldValue instanceof IConfigBase)) {
                    LOGGER.warn("Field '{}' has @Config annotation but is not an IConfigBase instance: {}",
                            field.getName(), fieldValue.getClass().getName());
                    continue;
                }

                IConfigBase config = (IConfigBase) fieldValue;

                // Apply metadata from annotation
                String category = configAnnotation.category();
                applyMetadata(config, category, modId);

                configs.add(config);

                LOGGER.debug("Parsed config: name={}, type={}, category={}",
                        config.getName(), config.getClass().getSimpleName(), category);

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
     * The category from the config object's internal field is overwritten
     * with the value from the annotation.
     *
     * @param config The configuration object
     * @param category The category from @Config annotation
     * @param modId The mod ID to assign
     */
    private static void applyMetadata(IConfigBase config, String category, String modId) {
        // Set modId if not already set
        if (config.getModId() == null) {
            config.setModId(modId);
        }

        // Override category with annotation value
        // We need to access the internal category field
        try {
            Field categoryField = config.getClass().getSuperclass().getDeclaredField("category");
            categoryField.setAccessible(true);
            categoryField.set(config, category);
        } catch (Exception e) {
            LOGGER.warn("Failed to set category for config '{}': {}", config.getName(), e.getMessage());
        }
    }

    /**
     * Validates that a config class follows the required structure.
     * <p>
     * Checks:
     * <ul>
     *   <li>Has at least one @Config annotated field</li>
     *   <li>All @Config fields are static final IConfigBase</li>
     * </ul>
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
        }

        if (!hasConfigs) {
            LOGGER.warn("Config class '{}' has no @Config annotated fields", configClass.getName());
            return false;
        }

        return allValid;
    }
}