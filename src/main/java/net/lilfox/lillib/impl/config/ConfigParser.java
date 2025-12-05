package net.lilfox.lillib.impl.config;

import net.lilfox.lillib.api.annotation.Config;
import net.lilfox.lillib.api.annotation.Hotkey;
import net.lilfox.lillib.api.annotation.Numeric;
import net.lilfox.lillib.api.config.IConfigBase;
import net.lilfox.lillib.impl.config.options.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses configuration classes with annotations and creates config instances.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigParser {
    private static final Logger LOGGER = LoggerFactory.getLogger("lillib");

    /**
     * Parses a configuration class and extracts all annotated fields.
     * 
     * @param configClass The class containing @Config annotated fields
     * @param modId The mod ID to assign to configurations
     * @return List of parsed configuration instances
     */
    public static List<IConfigBase> parseConfigClass(Class<?> configClass, String modId) {
        List<IConfigBase> configs = new ArrayList<>();

        for (Field field : configClass.getDeclaredFields()) {
            // Skip non-static fields
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // Check for @Config annotation
            Config configAnnotation = field.getAnnotation(Config.class);
            if (configAnnotation == null) {
                continue;
            }

            try {
                field.setAccessible(true);
                IConfigBase config = parseField(field, configAnnotation, modId);
                
                if (config != null) {
                    configs.add(config);
                    
                    // Replace field value with config instance for hotkeyed configs
                    if (config instanceof ConfigBooleanHotkeyed) {
                        // For hotkeyed configs, we keep the field as-is but register separately
                        // This allows both field.getBooleanValue() and direct boolean access
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to parse config field: " + field.getName(), e);
            }
        }

        return configs;
    }

    /**
     * Parses a single field and creates the appropriate config instance.
     * 
     * @param field The field to parse
     * @param configAnnotation The @Config annotation
     * @param modId The mod ID
     * @return The created config instance, or null if parsing failed
     * @throws IllegalAccessException if field access fails
     */
    private static IConfigBase parseField(Field field, Config configAnnotation, String modId) throws IllegalAccessException {
        String name = field.getName();
        String category = configAnnotation.category();
        Class<?> fieldType = field.getType();

        // Check for @Hotkey annotation
        Hotkey hotkeyAnnotation = field.getAnnotation(Hotkey.class);
        
        // Check for @Numeric annotation
        Numeric numericAnnotation = field.getAnnotation(Numeric.class);

        IConfigBase config = null;

        // Parse boolean fields
        if (fieldType == boolean.class || fieldType == Boolean.class) {
            boolean defaultValue = field.getBoolean(null);
            
            if (hotkeyAnnotation != null) {
                String defaultHotkey = hotkeyAnnotation.hotkey();
                config = new ConfigBooleanHotkeyed(name, category, defaultValue, defaultHotkey);
            } else {
                config = new ConfigBoolean(name, category, defaultValue);
            }
        }
        // Parse integer fields
        else if (fieldType == int.class || fieldType == Integer.class) {
            int defaultValue = field.getInt(null);
            
            if (numericAnnotation != null) {
                int minValue = (int) numericAnnotation.minValue();
                int maxValue = (int) numericAnnotation.maxValue();
                boolean useSlider = numericAnnotation.useSlider();
                config = new ConfigInteger(name, category, defaultValue, minValue, maxValue, useSlider);
            } else {
                config = new ConfigInteger(name, category, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
            }
        }
        // Parse double fields
        else if (fieldType == double.class || fieldType == Double.class) {
            double defaultValue = field.getDouble(null);
            
            if (numericAnnotation != null) {
                double minValue = numericAnnotation.minValue();
                double maxValue = numericAnnotation.maxValue();
                boolean useSlider = numericAnnotation.useSlider();
                config = new ConfigDouble(name, category, defaultValue, minValue, maxValue, useSlider);
            } else {
                config = new ConfigDouble(name, category, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
            }
        }
        // Parse float fields (treated as double)
        else if (fieldType == float.class || fieldType == Float.class) {
            double defaultValue = field.getFloat(null);
            
            if (numericAnnotation != null) {
                double minValue = numericAnnotation.minValue();
                double maxValue = numericAnnotation.maxValue();
                boolean useSlider = numericAnnotation.useSlider();
                config = new ConfigDouble(name, category, defaultValue, minValue, maxValue, useSlider);
            } else {
                config = new ConfigDouble(name, category, defaultValue, -Float.MAX_VALUE, Float.MAX_VALUE);
            }
        }
        // Parse String fields
        else if (fieldType == String.class) {
            String defaultValue = (String) field.get(null);
            config = new ConfigString(name, category, defaultValue);
        }
        else {
            LOGGER.warn("Unsupported config field type: {} for field {}", fieldType.getName(), name);
            return null;
        }

        if (config != null) {
            config.setModId(modId);
        }

        return config;
    }
}
