package net.lilfox.lillib.impl.util;

import net.minecraft.client.resource.language.I18n;

/**
 * Helper class for localization key resolution.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class LocalizationHelper {
    
    /**
     * Gets the localized config name.
     * <p>
     * Tries key: {@code modId.config.configName}
     * Falls back to configName if not found.
     * 
     * @param modId The mod ID
     * @param configName The config name
     * @return The localized name
     */
    public static String getConfigName(String modId, String configName) {
        String key = modId + ".config." + configName;
        if (I18n.hasTranslation(key)) {
            return I18n.translate(key);
        }
        return configName;
    }

    /**
     * Gets the localized config nice name for notifications.
     * <p>
     * Tries key: {@code modId.config.configName.nice}
     * Falls back to display name if not found.
     * 
     * @param modId The mod ID
     * @param configName The config name
     * @return The localized nice name
     */
    public static String getConfigNiceName(String modId, String configName) {
        String key = modId + ".config." + configName + ".nice";
        if (I18n.hasTranslation(key)) {
            return I18n.translate(key);
        }
        return getConfigName(modId, configName);
    }

    /**
     * Gets the localized config description.
     * <p>
     * Tries key: {@code modId.config.configName.desc}
     * Returns empty string if not found.
     * 
     * @param modId The mod ID
     * @param configName The config name
     * @return The localized description
     */
    public static String getConfigDescription(String modId, String configName) {
        String key = modId + ".config." + configName + ".desc";
        if (I18n.hasTranslation(key)) {
            return I18n.translate(key);
        }
        return "";
    }

    /**
     * Gets the localized category name.
     * <p>
     * Tries key: {@code modId.config.category.categoryName}
     * Falls back to categoryName if not found.
     * 
     * @param modId The mod ID
     * @param categoryName The category name
     * @return The localized category name
     */
    public static String getCategoryName(String modId, String categoryName) {
        String key = modId + ".config.category." + categoryName;
        if (I18n.hasTranslation(key)) {
            return I18n.translate(key);
        }
        return categoryName;
    }

    /**
     * Gets a lillib internal translation.
     * <p>
     * Used for buttons and labels in the GUI.
     * 
     * @param key The translation key (without "lillib." prefix)
     * @return The localized string
     */
    public static String getLibTranslation(String key) {
        String fullKey = "lillib.gui." + key;
        if (I18n.hasTranslation(fullKey)) {
            return I18n.translate(fullKey);
        }
        return key;
    }

    /**
     * Checks if a translation key exists.
     * 
     * @param key The full translation key
     * @return true if the key has a translation
     */
    public static boolean hasTranslation(String key) {
        return I18n.hasTranslation(key);
    }
}
