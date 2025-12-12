package net.lilfox.lillib.impl.hotkey;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.lilfox.lillib.impl.config.options.ConfigBooleanHotkeyed;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main handler for processing hotkey events.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: <a href="https://github.com/sakura-ryoko/malilib">...</a>
 * Licensed under the GNU Lesser General Public License v3.0
 *
 * <p>Hotkey processing rules:
 * <ul>
 *   <li>Keys must be pressed in the exact sequence specified</li>
 *   <li>If CTRL,N is pressed and held, then M is pressed -> CTRL,N,M triggers</li>
 *   <li>If CTRL is held, N pressed and released, then M pressed -> CTRL,M triggers</li>
 *   <li>Works in all contexts except text editing and hotkey configuration</li>
 * </ul>
 *
 * <p><b>Optimized version:</b> Only checks ~50 keys instead of 350+
 *
 * @author lilfox
 * @since 1.0.0
 */
public class HotkeyHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("lillib");
    private static HotkeyHandler INSTANCE;

    // Sequence of keys pressed in order
    private final List<Integer> pressedSequence = new ArrayList<>();

    // Set of currently held keys (for release detection)
    private final Set<Integer> heldKeys = new HashSet<>();

    // Last tick's held keys (for detecting releases)
    private final Set<Integer> lastHeldKeys = new HashSet<>();

    // Keys to check (optimized list)
    private static final int[] KEYS_TO_CHECK = buildKeysToCheck();

    private HotkeyHandler() {
    }

    /**
     * Gets the singleton instance.
     *
     * @return The HotkeyHandler instance
     */
    public static HotkeyHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HotkeyHandler();
        }
        return INSTANCE;
    }

    /**
     * Initializes the hotkey handler and registers event listeners.
     */
    public static void init() {
        HotkeyHandler handler = getInstance();

        // Register tick event for processing hotkeys
        ClientTickEvents.END_CLIENT_TICK.register(handler::tick);

        LOGGER.info("Hotkey handler initialized (checking {} keys)", KEYS_TO_CHECK.length);
    }

    /**
     * Builds the optimized list of keys to check each tick.
     * <p>
     * Includes:
     * <ul>
     *   <li>Modifiers: CTRL, SHIFT, ALT, SUPER (both left and right)</li>
     *   <li>Letters: A-Z</li>
     *   <li>Numbers: 0-9</li>
     *   <li>Function keys: F1-F25</li>
     *   <li>Special keys: Space, Enter, Tab, Escape</li>
     * </ul>
     *
     * @return Array of GLFW key codes to check
     */
    private static int[] buildKeysToCheck() {
        List<Integer> keys = new ArrayList<>();

        // Modifiers (8 keys)
        keys.add(GLFW.GLFW_KEY_LEFT_CONTROL);
        keys.add(GLFW.GLFW_KEY_RIGHT_CONTROL);
        keys.add(GLFW.GLFW_KEY_LEFT_SHIFT);
        keys.add(GLFW.GLFW_KEY_RIGHT_SHIFT);
        keys.add(GLFW.GLFW_KEY_LEFT_ALT);
        keys.add(GLFW.GLFW_KEY_RIGHT_ALT);
        keys.add(GLFW.GLFW_KEY_LEFT_SUPER);
        keys.add(GLFW.GLFW_KEY_RIGHT_SUPER);

        // Letters A-Z (26 keys)
        for (int key = GLFW.GLFW_KEY_A; key <= GLFW.GLFW_KEY_Z; key++) {
            keys.add(key);
        }

        // Numbers 0-9 (10 keys)
        for (int key = GLFW.GLFW_KEY_0; key <= GLFW.GLFW_KEY_9; key++) {
            keys.add(key);
        }

        // Function keys F1-F25 (25 keys)
        for (int key = GLFW.GLFW_KEY_F1; key <= GLFW.GLFW_KEY_F25; key++) {
            keys.add(key);
        }

        // Special keys (4 keys)
        keys.add(GLFW.GLFW_KEY_SPACE);
        keys.add(GLFW.GLFW_KEY_ENTER);
        keys.add(GLFW.GLFW_KEY_TAB);
        keys.add(GLFW.GLFW_KEY_ESCAPE);

        // Convert to array
        int[] result = new int[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            result[i] = keys.get(i);
        }

        return result;
    }

    /**
     * Processes hotkey state each tick.
     *
     * @param client The Minecraft client
     */
    private void tick(MinecraftClient client) {
        if (client == null || client.player == null) {
            return;
        }

        // Check if we can process hotkeys in current context
        if (!HotkeyContext.canProcessHotkeys()) {
            // Clear state when we can't process
            if (!pressedSequence.isEmpty() || !heldKeys.isEmpty()) {
                LOGGER.debug("Clearing hotkey state (context blocked)");
                pressedSequence.clear();
                heldKeys.clear();
                lastHeldKeys.clear();
            }
            return;
        }

        // Update currently held keys
        updateHeldKeys(client);

        // Detect newly pressed keys
        detectNewPresses();

        // Detect released keys and update sequence
        detectReleases();

        // Check for matching hotkeys
        if (!pressedSequence.isEmpty()) {
            checkHotkeyMatches();
        }

        // Update last held keys for next tick
        lastHeldKeys.clear();
        lastHeldKeys.addAll(heldKeys);
    }

    /**
     * Updates the set of currently held keys (optimized version).
     * <p>
     * Only checks keys in the optimized list instead of all possible keys.
     *
     * @param client The Minecraft client
     */
    private void updateHeldKeys(MinecraftClient client) {
        heldKeys.clear();
        long windowHandle = client.getWindow().getHandle();

        // Check only optimized key list
        for (int keyCode : KEYS_TO_CHECK) {
            if (GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS) {
                heldKeys.add(keyCode);
            }
        }
    }

    /**
     * Detects newly pressed keys and adds them to the sequence.
     */
    private void detectNewPresses() {
        for (Integer keyCode : heldKeys) {
            if (!lastHeldKeys.contains(keyCode)) {
                // New key press detected
                pressedSequence.add(keyCode);
                LOGGER.debug("Key pressed: {} (sequence: {})", getKeyName(keyCode), pressedSequence.size());
            }
        }
    }

    /**
     * Detects released keys and updates the sequence accordingly.
     * <p>
     * When a key is released:
     * - If it's the last key in sequence, remove it
     * - Otherwise, keep the sequence as-is (partial release)
     */
    private void detectReleases() {
        List<Integer> releasedKeys = new ArrayList<>();

        for (Integer keyCode : lastHeldKeys) {
            if (!heldKeys.contains(keyCode)) {
                releasedKeys.add(keyCode);
            }
        }

        if (!releasedKeys.isEmpty()) {
            LOGGER.debug("Keys released: {}", releasedKeys.size());
        }

        // Remove released keys from the end of sequence
        for (Integer released : releasedKeys) {
            // Find and remove from sequence
            int lastIndex = pressedSequence.lastIndexOf(released);
            if (lastIndex != -1) {
                // Only remove if it's at the end
                if (lastIndex == pressedSequence.size() - 1) {
                    pressedSequence.remove(lastIndex);
                } else {
                    // If released key is in middle, it might start a new sequence
                    // Remove all keys after and including the released key
                    pressedSequence.subList(lastIndex, pressedSequence.size()).clear();
                }
            }
        }

        // If no keys are held, clear the sequence
        if (heldKeys.isEmpty() && !pressedSequence.isEmpty()) {
            LOGGER.debug("All keys released, clearing sequence");
            pressedSequence.clear();
        }
    }

    /**
     * Checks if the current pressed sequence matches any registered hotkeys.
     */
    private void checkHotkeyMatches() {
        List<ConfigBooleanHotkeyed> matches = KeybindManager.getInstance()
                .findMatchingHotkeys(pressedSequence);

        if (!matches.isEmpty()) {
            LOGGER.info("Found {} matching hotkey(s) for sequence: {}", matches.size(), getSequenceString());

            // Trigger all matching hotkeys
            for (ConfigBooleanHotkeyed config : matches) {
                triggerHotkey(config);
            }

            // Clear sequence after triggering to prevent re-triggering
            // But keep currently held keys for potential chain hotkeys
            pressedSequence.clear();

            // Re-add currently held keys to sequence for chaining
            pressedSequence.addAll(heldKeys);
        }
    }

    /**
     * Triggers a hotkey configuration.
     *
     * @param config The configuration to trigger
     */
    private void triggerHotkey(ConfigBooleanHotkeyed config) {
        try {
            LOGGER.info("Triggering hotkey for config: {}", config.getName());
            config.onHotkeyActivated();
        } catch (Exception e) {
            LOGGER.error("Error triggering hotkey for config: {}", config.getName(), e);
        }
    }

    /**
     * Gets the current pressed key sequence as a readable string.
     *
     * @return String representation of the sequence
     */
    private String getSequenceString() {
        if (pressedSequence.isEmpty()) {
            return "<empty>";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pressedSequence.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(getKeyName(pressedSequence.get(i)));
        }
        return sb.toString();
    }

    /**
     * Gets a readable name for a key code.
     *
     * @param keyCode The GLFW key code
     * @return The key name
     */
    private String getKeyName(int keyCode) {
        // Modifiers
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) return "CTRL";
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) return "SHIFT";
        if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) return "ALT";
        if (keyCode == GLFW.GLFW_KEY_LEFT_SUPER || keyCode == GLFW.GLFW_KEY_RIGHT_SUPER) return "SUPER";

        // Special keys
        if (keyCode == GLFW.GLFW_KEY_SPACE) return "SPACE";
        if (keyCode == GLFW.GLFW_KEY_ENTER) return "ENTER";
        if (keyCode == GLFW.GLFW_KEY_TAB) return "TAB";
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) return "ESC";

        // Function keys
        if (keyCode >= GLFW.GLFW_KEY_F1 && keyCode <= GLFW.GLFW_KEY_F25) {
            return "F" + (keyCode - GLFW.GLFW_KEY_F1 + 1);
        }

        // Letters
        if (keyCode >= GLFW.GLFW_KEY_A && keyCode <= GLFW.GLFW_KEY_Z) {
            return String.valueOf((char)('A' + (keyCode - GLFW.GLFW_KEY_A)));
        }

        // Numbers
        if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
            return String.valueOf((char)('0' + (keyCode - GLFW.GLFW_KEY_0)));
        }

        return "KEY_" + keyCode;
    }

    /**
     * Gets the current pressed key sequence.
     * <p>
     * For debugging purposes.
     *
     * @return The current sequence
     */
    public List<Integer> getCurrentSequence() {
        return new ArrayList<>(pressedSequence);
    }

    /**
     * Clears the current sequence.
     * <p>
     * Used when context changes or for testing.
     */
    public void clearSequence() {
        pressedSequence.clear();
        heldKeys.clear();
        lastHeldKeys.clear();
    }
}