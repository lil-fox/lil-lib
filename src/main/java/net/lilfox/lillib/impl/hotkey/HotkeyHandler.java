package net.lilfox.lillib.impl.hotkey;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
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
 * Original source: https://github.com/sakura-ryoko/malilib
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
        
        LOGGER.info("Hotkey handler initialized");
    }

    /**
     * Processes hotkey state each tick.
     * 
     * @param client The Minecraft client
     */
    private void tick(MinecraftClient client) {
        //LOGGER.warn("LISTEN_KEYS");
        if (client == null || client.player == null) {
            return;
        }

        LOGGER.warn(""+HotkeyContext.canProcessHotkeys());
        // Check if we can process hotkeys in current context
        if (!HotkeyContext.canProcessHotkeys()) {
            // Clear state when we can't process
            if (!pressedSequence.isEmpty() || !heldKeys.isEmpty()) {
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
     * Updates the set of currently held keys.
     * 
     * @param client The Minecraft client
     */
    private void updateHeldKeys(MinecraftClient client) {
        heldKeys.clear();
        long windowHandle = client.getWindow().getHandle();
        
        // Check all possible keys
        for (int keyCode = GLFW.GLFW_KEY_SPACE; keyCode <= GLFW.GLFW_KEY_LAST; keyCode++) {
            if (GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS) {
                heldKeys.add(keyCode);
            }
        }
        
        // Also check mouse buttons
        for (int button = GLFW.GLFW_MOUSE_BUTTON_1; button <= GLFW.GLFW_MOUSE_BUTTON_8; button++) {
            if (GLFW.glfwGetMouseButton(windowHandle, button) == GLFW.GLFW_PRESS) {
                // Offset mouse buttons to avoid key code conflicts
                heldKeys.add(button + 1000);
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
        if (heldKeys.isEmpty()) {
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
            LOGGER.debug("Triggering hotkey: {}", config.getName());
            config.onHotkeyActivated();
        } catch (Exception e) {
            LOGGER.error("Error triggering hotkey for config: {}", config.getName(), e);
        }
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
