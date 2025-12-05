package net.lilfox.lillib.impl.hotkey;

import net.lilfox.lillib.impl.config.options.ConfigBooleanHotkeyed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages registration and lookup of hotkey configurations.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class KeybindManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("lillib");
    private static KeybindManager INSTANCE;

    // Map of hotkey sequence -> list of configs
    private final Map<HotkeySequence, List<ConfigBooleanHotkeyed>> hotkeyMap = new ConcurrentHashMap<>();
    
    // List of all registered configs
    private final List<ConfigBooleanHotkeyed> allConfigs = new ArrayList<>();

    private KeybindManager() {
    }

    /**
     * Gets the singleton instance.
     * 
     * @return The KeybindManager instance
     */
    public static KeybindManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KeybindManager();
        }
        return INSTANCE;
    }

    /**
     * Registers a hotkeyed configuration.
     * 
     * @param config The configuration to register
     */
    public void registerHotkey(ConfigBooleanHotkeyed config) {
        if (config == null) {
            return;
        }
        
        if (!allConfigs.contains(config)) {
            allConfigs.add(config);
        }
        
        String hotkeyString = config.getHotkey();
        if (hotkeyString == null || hotkeyString.isEmpty()) {
            return;
        }
        
        HotkeySequence sequence = new HotkeySequence(hotkeyString);
        if (!sequence.isEmpty()) {
            hotkeyMap.computeIfAbsent(sequence, k -> new ArrayList<>()).add(config);
            ConflictDetector.registerHotkey(config);
            
            LOGGER.debug("Registered hotkey '{}' for config '{}'", hotkeyString, config.getName());
        }
    }

    /**
     * Unregisters a hotkeyed configuration.
     * 
     * @param config The configuration to unregister
     */
    public void unregisterHotkey(ConfigBooleanHotkeyed config) {
        if (config == null) {
            return;
        }
        
        allConfigs.remove(config);
        
        // Remove from hotkey map
        String hotkeyString = config.getHotkey();
        if (hotkeyString != null && !hotkeyString.isEmpty()) {
            HotkeySequence sequence = new HotkeySequence(hotkeyString);
            List<ConfigBooleanHotkeyed> configs = hotkeyMap.get(sequence);
            
            if (configs != null) {
                configs.remove(config);
                if (configs.isEmpty()) {
                    hotkeyMap.remove(sequence);
                }
            }
            
            ConflictDetector.unregisterHotkey(config);
        }
    }

    /**
     * Updates a hotkey registration when it changes.
     * 
     * @param config The configuration that changed
     * @param oldHotkeyString The old hotkey string
     */
    public void updateHotkey(ConfigBooleanHotkeyed config, String oldHotkeyString) {
        // Remove old registration
        if (oldHotkeyString != null && !oldHotkeyString.isEmpty()) {
            HotkeySequence oldSequence = new HotkeySequence(oldHotkeyString);
            List<ConfigBooleanHotkeyed> configs = hotkeyMap.get(oldSequence);
            
            if (configs != null) {
                configs.remove(config);
                if (configs.isEmpty()) {
                    hotkeyMap.remove(oldSequence);
                }
            }
        }
        
        // Register new hotkey
        String newHotkeyString = config.getHotkey();
        if (newHotkeyString != null && !newHotkeyString.isEmpty()) {
            HotkeySequence newSequence = new HotkeySequence(newHotkeyString);
            if (!newSequence.isEmpty()) {
                hotkeyMap.computeIfAbsent(newSequence, k -> new ArrayList<>()).add(config);
            }
        }
        
        ConflictDetector.updateHotkey(config, oldHotkeyString);
    }

    /**
     * Finds configurations that match the given key sequence.
     * 
     * @param pressedKeys The sequence of pressed keys
     * @return List of matching configurations
     */
    public List<ConfigBooleanHotkeyed> findMatchingHotkeys(List<Integer> pressedKeys) {
        List<ConfigBooleanHotkeyed> matches = new ArrayList<>();
        
        for (Map.Entry<HotkeySequence, List<ConfigBooleanHotkeyed>> entry : hotkeyMap.entrySet()) {
            HotkeySequence sequence = entry.getKey();
            
            if (sequence.isTriggeredBy(pressedKeys)) {
                matches.addAll(entry.getValue());
            }
        }
        
        return matches;
    }

    /**
     * Gets all registered hotkeyed configurations.
     * 
     * @return List of all configurations
     */
    public List<ConfigBooleanHotkeyed> getAllHotkeys() {
        return new ArrayList<>(allConfigs);
    }

    /**
     * Clears all registered hotkeys.
     */
    public void clear() {
        hotkeyMap.clear();
        allConfigs.clear();
    }
}
