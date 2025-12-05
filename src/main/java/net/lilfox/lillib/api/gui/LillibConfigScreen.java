package net.lilfox.lillib.api.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Base class for creating configuration GUI screens.
 * <p>
 * Mods should extend this class to create their configuration screens.
 * The screen automatically generates tabs for each category and provides
 * search functionality within the current tab.
 * 
 * <p>Example usage:
 * <pre>{@code
 * public class MyModConfigGui extends LillibConfigScreen {
 *     public MyModConfigGui() {
 *         super("mymod", "mymod.config.title");
 *     }
 *     
 *     @Override
 *     public boolean hideUnavailableConfigs() {
 *         return true;
 *     }
 *     
 *     public static void open() {
 *         MinecraftClient.getInstance().setScreen(new MyModConfigGui());
 *     }
 * }
 * }</pre>
 * 
 * @author lilfox
 * @since 1.0.0
 */
public abstract class LillibConfigScreen extends Screen {
    protected final String modId;

    /**
     * Creates a new configuration screen.
     * 
     * @param modId The mod ID whose configurations to display
     * @param titleKey The translation key for the screen title
     */
    protected LillibConfigScreen(String modId, String titleKey) {
        super(Text.translatable(titleKey));
        this.modId = modId;
    }

    /**
     * Gets the mod ID this screen is displaying configurations for.
     * 
     * @return The mod ID
     */
    public String getModId() {
        return modId;
    }

    /**
     * Determines whether unavailable configurations should be hidden.
     * <p>
     * Unavailable configurations are those that have dependency predicates
     * that evaluate to false. Override this method to control visibility.
     * 
     * @return {@code true} to hide unavailable configs, {@code false} to show them grayed out
     */
    public boolean hideUnavailableConfigs() {
        return false;
    }

    /**
     * Called when a configuration value is changed.
     * <p>
     * Override this method to perform additional actions when configs change.
     * 
     * @param configName The name of the configuration that changed
     */
    protected void onConfigChanged(String configName) {
        // Override in subclasses if needed
    }

    /**
     * Checks if a hotkey is currently being edited.
     * <p>
     * Used internally to prevent hotkey conflicts during editing.
     * 
     * @return {@code true} if editing a hotkey, {@code false} otherwise
     */
    public boolean isEditingHotkey() {
        // Implemented in the internal implementation class
        return false;
    }
}
