package net.lilfox.lillib.impl.hotkey;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.lilfox.lillib.api.gui.LillibConfigScreen;

/**
 * Determines the context in which hotkeys can be processed.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class HotkeyContext {
    
    /**
     * Checks if hotkeys can be processed in the current context.
     * <p>
     * Hotkeys are blocked when:
     * <ul>
     *   <li>Editing text (chat, command, sign, etc.)</li>
     *   <li>Editing a hotkey in the config GUI</li>
     * </ul>
     * 
     * Hotkeys work in all other contexts including:
     * <ul>
     *   <li>Normal gameplay</li>
     *   <li>Player inventory</li>
     *   <li>Containers (chests, furnaces, etc.)</li>
     *   <li>Any other GUI screens</li>
     * </ul>
     * 
     * @return true if hotkeys can be processed
     */
    public static boolean canProcessHotkeys() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // No client means no hotkeys
        if (client == null) {
            return false;
        }
        
        Screen screen = client.currentScreen;
        
        // If editing a hotkey in config GUI, block all other hotkeys
        if (screen instanceof LillibConfigScreen) {
            LillibConfigScreen configScreen = (LillibConfigScreen) screen;
            if (configScreen.isEditingHotkey()) {
                return false;
            }
        }
        
        // Block if focused element is a text field (typing)
        if (screen != null && screen.getFocused() instanceof TextFieldWidget) {
            return false;
        }
        
        // Block if chat is open
        if (client.currentScreen != null && 
            client.currentScreen.getClass().getName().contains("ChatScreen")) {
            return false;
        }
        
        // Allow in all other contexts
        return true;
    }

    /**
     * Checks if hotkeys should work in inventory screens.
     * <p>
     * In our implementation, hotkeys always work in inventory screens
     * unless blocked by other conditions (text editing, etc.)
     * 
     * @return true
     */
    public static boolean allowInInventory() {
        return true;
    }

    /**
     * Checks if hotkeys should work in GUI screens.
     * <p>
     * In our implementation, hotkeys always work in GUI screens
     * unless blocked by other conditions (text editing, etc.)
     * 
     * @return true
     */
    public static boolean allowInGui() {
        return true;
    }

    /**
     * Checks if currently in a GUI context.
     * 
     * @return true if a screen is open
     */
    public static boolean isInGui() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null && client.currentScreen != null;
    }

    /**
     * Checks if currently in gameplay (no GUI open).
     * 
     * @return true if no screen is open
     */
    public static boolean isInGame() {
        return !isInGui();
    }
}
