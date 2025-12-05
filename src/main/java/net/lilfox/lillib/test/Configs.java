package net.lilfox.lillib.test;

import net.lilfox.lillib.api.annotation.Config;
import net.lilfox.lillib.api.annotation.Hotkey;
import net.lilfox.lillib.api.config.IConfigBoolean;
import net.lilfox.lillib.api.config.IConfigBooleanHotkeyed;
import net.lilfox.lillib.impl.config.ConfigManager;

public class Configs {
    @Config(category = "general")
    @Hotkey(hotkey = "U,C")
    public static boolean openGui = false;

    @Config(category = "features")
    public static boolean coolFeature = false;

    @Config(category = "features")
    @Hotkey()
    public static boolean coolFeatureHot = false;

    public static void init() {
        ConfigManager.create("lillib")
                .parseClass(Configs.class)
                .loadFromFile()
                .build();

        IConfigBoolean config = (IConfigBoolean) ConfigManager.getInstance()
                .getConfig("lillib", "coolFeature");



        config.setValueChangeCallback((newValue, oldValue) -> {
            System.out.println("Функция изменена: " + newValue);
            if (newValue) {
                System.out.println("ENABLED");
            } else {
                System.out.println("DISABLED");
            }
        });

        ConfigManager.setHotkeyCallback((IConfigBooleanHotkeyed) ConfigManager.getInstance()
                .getConfig("lillib", "openGui"), ConfigsGui::open);
    }




}
