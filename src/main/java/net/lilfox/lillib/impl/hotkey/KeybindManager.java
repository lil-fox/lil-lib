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
 * <p><b>Version with debug logging</b>
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
            LOGGER.warn("Attempted to register null config");
            return;
        }

        if (!allConfigs.contains(config)) {
            allConfigs.add(config);
            LOGGER.debug("Added config to allConfigs list: {}", config.getName());
        }

        String hotkeyString = config.getHotkey();
        if (hotkeyString == null || hotkeyString.isEmpty()) {
            LOGGER.debug("Config '{}' has no hotkey, skipping registration", config.getName());
            return;
        }

        HotkeySequence sequence = new HotkeySequence(hotkeyString);
        if (!sequence.isEmpty()) {
            hotkeyMap.computeIfAbsent(sequence, k -> new ArrayList<>()).add(config);
            ConflictDetector.registerHotkey(config);

            LOGGER.info("Registered hotkey '{}' for config '{}' (total hotkeys: {})",
                    hotkeyString, config.getName(), hotkeyMap.size());
        } else {
            LOGGER.warn("Failed to parse hotkey sequence: {}", hotkeyString);
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
        LOGGER.debug("Removed config from allConfigs list: {}", config.getName());

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
                LOGGER.debug("Unregistered hotkey '{}' for config '{}'", hotkeyString, config.getName());
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
        LOGGER.debug("Updating hotkey for config '{}': {} -> {}",
                config.getName(), oldHotkeyString, config.getHotkey());

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
                LOGGER.debug("Re-registered hotkey '{}' for config '{}'", newHotkeyString, config.getName());
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

        if (pressedKeys == null || pressedKeys.isEmpty()) {
            return matches;
        }

        LOGGER.trace("Checking {} hotkey(s) against sequence of {} key(s)",
                hotkeyMap.size(), pressedKeys.size());

        for (Map.Entry<HotkeySequence, List<ConfigBooleanHotkeyed>> entry : hotkeyMap.entrySet()) {
            HotkeySequence sequence = entry.getKey();

            if (sequence.isTriggeredBy(pressedKeys)) {
                matches.addAll(entry.getValue());
                LOGGER.debug("Hotkey sequence '{}' matched! Configs: {}",
                        sequence, entry.getValue().size());
            }
        }

        if (matches.isEmpty()) {
            LOGGER.trace("No matching hotkeys found");
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
     * Gets the number of registered hotkeys.
     *
     * @return The count of registered hotkeys
     */
    public int getHotkeyCount() {
        return hotkeyMap.size();
    }

    /**
     * Gets the number of registered configs.
     *
     * @return The count of registered configs
     */
    public int getConfigCount() {
        return allConfigs.size();
    }

    /**
     * Prints debug information about registered hotkeys.
     */
    public void debugPrint() {
        LOGGER.info("=== KeybindManager Debug Info ===");
        LOGGER.info("Total configs: {}", allConfigs.size());
        LOGGER.info("Total hotkeys: {}", hotkeyMap.size());

        for (Map.Entry<HotkeySequence, List<ConfigBooleanHotkeyed>> entry : hotkeyMap.entrySet()) {
            LOGGER.info("  Hotkey '{}': {} config(s)",
                    entry.getKey(), entry.getValue().size());
            for (ConfigBooleanHotkeyed config : entry.getValue()) {
                LOGGER.info("    - {}", config.getName());
            }
        }

        LOGGER.info("================================");
    }

    /**
     * Clears all registered hotkeys.
     */
    public void clear() {
        hotkeyMap.clear();
        allConfigs.clear();
        LOGGER.info("Cleared all hotkey registrations");
    }
}