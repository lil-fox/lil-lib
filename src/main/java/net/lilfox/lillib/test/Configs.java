package net.lilfox.lillib.test;

import net.lilfox.lillib.api.annotation.Config;
import net.lilfox.lillib.impl.config.ConfigFactory;
import net.lilfox.lillib.impl.config.ConfigManager;
import net.lilfox.lillib.impl.config.options.ConfigBoolean;
import net.lilfox.lillib.impl.config.options.ConfigBooleanHotkeyed;
import net.lilfox.lillib.impl.config.options.ConfigInteger;

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
    @Config(category = "test")
    public static final ConfigBooleanHotkeyed openGui =
            factory.createBooleanHotkeyed("openGui", "U,C");

    /**
     * Simple boolean config for a cool feature.
     * <p>
     * Default value: false
     */
    @Config(category = "test")
    public static final ConfigBoolean coolFeature =
            factory.createBoolean("coolFeature");
    @Config(category = "test")
    public static final ConfigBoolean coolFeature1 =
            factory.createBoolean("coolFeature1");
    @Config(category = "test")
    public static final ConfigBoolean coolFeature2 =
            factory.createBoolean("coolFeature2");
    @Config(category = "test")
    public static final ConfigBoolean coolFeature3 =
            factory.createBoolean("coolFeature3");
    @Config(category = "test")
    public static final ConfigBoolean coolFeature4 =
            factory.createBoolean("coolFeature4");
    @Config(category = "test")
    public static final ConfigBoolean coolFeature5 =
            factory.createBoolean("coolFeature5");
    @Config(category = "test")
    public static final ConfigBoolean coolFeature6 =
            factory.createBoolean("coolFeature6");
    @Config(category = "test")
    public static final ConfigBoolean coolFeature7 =
            factory.createBoolean("coolFeature7");
    @Config(category = "test")
    public static final ConfigBoolean coolFeature8 =
            factory.createBoolean("coolFeature8");
    @Config(category = "test")
    public static final ConfigBoolean coolFeature9 =
            factory.createBoolean("coolFeature9");
    @Config(category = "test")
    public static final ConfigBoolean coolFeature10 =
            factory.createBoolean("coolFeature10");

    /**
     * Another hotkeyed boolean config with no default hotkey.
     * <p>
     * Users can configure the hotkey in the GUI.
     */
    @Config(category = "features")
    public static final ConfigBooleanHotkeyed coolFeatureHot =
            factory.createBooleanHotkeyed("coolFeatureHot");

    @Config(category = "features")
    public static final ConfigInteger testInt =
            factory.createInteger("coolFeatureHot",1,1,10,true);

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