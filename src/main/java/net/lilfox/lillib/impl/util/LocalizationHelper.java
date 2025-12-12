package net.lilfox.lillib.impl.util;

import net.minecraft.client.resource.language.I18n;

/**
 * Helper class for localization key resolution.
 * <p>
 * If translation is not found, returns the key itself for debugging.
 *
 * @author lilfox
 * @since 1.0.0
 */
public class LocalizationHelper {

    /**
     * Gets the localized config name.
     * <p>
     * Tries key: {@code modId.config.configName}
     * Falls back to key itself if not found (for debugging).
     *
     * @param modId The mod ID
     * @param configName The config name
     * @return The localized name or the key
     */
    public static String getConfigName(String modId, String configName) {
        String key = modId + ".config." + configName;
        String translation = I18n.translate(key);

        // If translation equals key, it wasn't found - return key for debugging
        if (translation.equals(key)) {
            return configName;
        }

        return translation;
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
        String translation = I18n.translate(key);

        // If translation not found, fall back to display name
        if (translation.equals(key)) {
            return key;
        }

        return translation;
    }

    /**
     * Gets the localized config description.
     * <p>
     * Tries key: {@code modId.config.configName.desc}
     * Returns the key itself if not found (for debugging).
     *
     * @param modId The mod ID
     * @param configName The config name
     * @return The localized description or the key
     */
    public static String getConfigDescription(String modId, String configName) {
        String key = modId + ".config." + configName + ".desc";
        String translation = I18n.translate(key);

        // Return key itself for debugging if translation not found
        if (translation.equals(key)) {
            return key;
        }

        return translation;
    }

    /**
     * Gets the localized category name.
     * <p>
     * Tries key: {@code modId.config.category.categoryName}
     * Falls back to key itself if not found.
     *
     * @param modId The mod ID
     * @param categoryName The category name
     * @return The localized category name or the key
     */
    public static String getCategoryName(String modId, String categoryName) {
        String key = modId + ".config.category." + categoryName;
        String translation = I18n.translate(key);

        // Return key itself for debugging if translation not found
        if (translation.equals(key)) {
            return key;
        }

        return translation;
    }

    /**
     * Gets a lillib internal translation.
     * <p>
     * Used for buttons and labels in the GUI.
     *
     * @param key The translation key (without "lillib." prefix)
     * @return The localized string or the full key
     */
    public static String getLibTranslation(String key) {
        String fullKey = "lillib.gui." + key;
        String translation = I18n.translate(fullKey);

        // Return full key for debugging if translation not found
        if (translation.equals(fullKey)) {
            return fullKey;
        }

        return translation;
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