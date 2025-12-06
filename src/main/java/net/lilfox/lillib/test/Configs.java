package net.lilfox.lillib.test;

import net.lilfox.lillib.api.annotation.Config;
import net.lilfox.lillib.impl.config.ConfigFactory;
import net.lilfox.lillib.impl.config.ConfigManager;
import net.lilfox.lillib.impl.config.options.ConfigBoolean;
import net.lilfox.lillib.impl.config.options.ConfigBooleanHotkeyed;

/**
 * Test configuration class for lillib.
 * <p>
 * This class demonstrates the hybrid approach (MagicLib 0.8 style):
 * <ul>
 *   <li>Configs are created as objects via {@link ConfigFactory}</li>
 *   <li>{@code @Config} annotation provides metadata (category, dependencies)</li>
 *   <li>No {@code @Hotkey} or {@code @Numeric} annotations needed</li>
 *   <li>Direct access to config API without wrappers</li>
 * </ul>
 *
 * @author lilfox
 * @since 1.0.0
 */
public class Configs {
    // Create factory once with mod ID
    private static final ConfigFactory factory = new ConfigFactory("lillib");

    /**
     * Hotkeyed boolean config to open the configuration GUI.
     * <p>
     * Default hotkey: U,C (press U then C)
     */
    @Config(category = "general")
    public static final ConfigBooleanHotkeyed openGui =
            factory.createBooleanHotkeyed("openGui", "U,C");

    /**
     * Simple boolean config for a cool feature.
     * <p>
     * Default value: false
     */
    @Config(category = "features")
    public static final ConfigBoolean coolFeature =
            factory.createBoolean("coolFeature");

    /**
     * Another hotkeyed boolean config with no default hotkey.
     * <p>
     * Users can configure the hotkey in the GUI.
     */
    @Config(category = "features")
    public static final ConfigBooleanHotkeyed coolFeatureHot =
            factory.createBooleanHotkeyed("coolFeatureHot");

    /**
     * Initializes the configuration system.
     * <p>
     * Call this during mod initialization (client or main entrypoint).
     */
    public static void init() {
        // Parse config class and load from file
        ConfigManager.create("lillib")
                .parseClass(Configs.class)
                .loadFromFile()
                .build();

        // Register value change callback
        coolFeature.setValueChangeCallback((newValue, oldValue) -> {
            System.out.println("Cool feature changed: " + oldValue + " -> " + newValue);
            if (newValue) {
                System.out.println("ENABLED");
            } else {
                System.out.println("DISABLED");
            }
        });

        // Register hotkey activation callback
        openGui.setActivationCallback(() -> {
            System.out.println("Opening GUI via hotkey!");
            ConfigsGui.open();
        });

        // Debug: Print registered hotkeys
        net.lilfox.lillib.impl.hotkey.KeybindManager.getInstance().debugPrint();
    }
}