package net.lilfox.lillib.impl.hotkey;

import net.lilfox.lillib.api.config.IConfigHotkey;
import net.lilfox.lillib.impl.config.options.ConfigBooleanHotkeyed;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Detects and manages hotkey conflicts across all mods.
 * <p>
 * Two hotkeys conflict only if they have identical key sequences
 * in the same order. Conflicts are tracked globally across all
 * mods using lillib.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class ConflictDetector {
    // Map of hotkey string -> list of configs using that hotkey
    private static final Map<String, List<IConfigHotkey>> hotkeyRegistry = new ConcurrentHashMap<>();
    
    // Map of config -> hotkey string for quick lookup
    private static final Map<IConfigHotkey, String> configToHotkey = new ConcurrentHashMap<>();

    /**
     * Registers a hotkey configuration for conflict detection.
     * 
     * @param config The hotkey configuration to register
     */
    public static void registerHotkey(IConfigHotkey config) {
        if (config == null) {
            return;
        }
        
        String hotkey = config.getHotkey();
        if (hotkey == null || hotkey.isEmpty()) {
            return;
        }
        
        // Normalize hotkey string
        String normalized = normalizeHotkey(hotkey);
        
        hotkeyRegistry.computeIfAbsent(normalized, k -> new ArrayList<>()).add(config);
        configToHotkey.put(config, normalized);
    }

    /**
     * Unregisters a hotkey configuration.
     * 
     * @param config The configuration to unregister
     */
    public static void unregisterHotkey(IConfigHotkey config) {
        if (config == null) {
            return;
        }
        
        String hotkey = configToHotkey.remove(config);
        if (hotkey != null) {
            List<IConfigHotkey> configs = hotkeyRegistry.get(hotkey);
            if (configs != null) {
                configs.remove(config);
                if (configs.isEmpty()) {
                    hotkeyRegistry.remove(hotkey);
                }
            }
        }
    }

    /**
     * Updates a hotkey registration when it changes.
     * 
     * @param config The configuration that changed
     * @param oldHotkey The old hotkey string
     */
    public static void updateHotkey(IConfigHotkey config, String oldHotkey) {
        // Unregister old
        if (oldHotkey != null && !oldHotkey.isEmpty()) {
            String normalized = normalizeHotkey(oldHotkey);
            List<IConfigHotkey> configs = hotkeyRegistry.get(normalized);
            if (configs != null) {
                configs.remove(config);
                if (configs.isEmpty()) {
                    hotkeyRegistry.remove(normalized);
                }
            }
        }
        
        // Register new
        registerHotkey(config);
    }

    /**
     * Checks if a configuration has conflicting hotkeys.
     * 
     * @param config The configuration to check
     * @return true if conflicts exist
     */
    public static boolean hasConflicts(IConfigHotkey config) {
        String hotkey = configToHotkey.get(config);
        if (hotkey == null) {
            return false;
        }
        
        List<IConfigHotkey> configs = hotkeyRegistry.get(hotkey);
        return configs != null && configs.size() > 1;
    }

    /**
     * Gets all conflicting configurations for a hotkey.
     * 
     * @param config The configuration to check
     * @return Array of conflict descriptions in format "ModName > Category > ConfigName"
     */
    public static String[] getConflicts(IConfigHotkey config) {
        String hotkey = configToHotkey.get(config);
        if (hotkey == null) {
            return new String[0];
        }
        
        List<IConfigHotkey> configs = hotkeyRegistry.get(hotkey);
        if (configs == null || configs.size() <= 1) {
            return new String[0];
        }
        
        List<String> conflicts = new ArrayList<>();
        for (IConfigHotkey other : configs) {
            if (other != config) {
                conflicts.add(formatConflict(other));
            }
        }
        
        return conflicts.toArray(new String[0]);
    }

    /**
     * Formats a config into a readable conflict description.
     * 
     * @param config The configuration
     * @return Formatted string "ModName > Category > ConfigName"
     */
    private static String formatConflict(IConfigHotkey config) {
        if (config instanceof ConfigBooleanHotkeyed) {
            ConfigBooleanHotkeyed cfg = (ConfigBooleanHotkeyed) config;
            String modId = cfg.getModId();
            String category = cfg.getCategory();
            String name = cfg.getDisplayName();
            
            return String.format("%s > %s > %s", 
                modId != null ? modId : "Unknown",
                category != null ? category : "General",
                name);
        }
        
        return "Unknown Config";
    }

    /**
     * Normalizes a hotkey string for consistent comparison.
     * <p>
     * Removes extra whitespace and converts to uppercase.
     * 
     * @param hotkey The hotkey string to normalize
     * @return The normalized hotkey string
     */
    private static String normalizeHotkey(String hotkey) {
        if (hotkey == null) {
            return "";
        }
        
        // Split by comma, trim each part, rejoin
        String[] parts = hotkey.split(",");
        StringBuilder normalized = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                normalized.append(",");
            }
            normalized.append(parts[i].trim().toUpperCase());
        }
        
        return normalized.toString();
    }

    /**
     * Gets all registered hotkeys.
     * <p>
     * Used for debugging and testing.
     * 
     * @return Set of all registered hotkey strings
     */
    public static Set<String> getAllHotkeys() {
        return new HashSet<>(hotkeyRegistry.keySet());
    }

    /**
     * Clears all registered hotkeys.
     * <p>
     * Used for testing and cleanup.
     */
    public static void clear() {
        hotkeyRegistry.clear();
        configToHotkey.clear();
    }
}
