package net.lilfox.lillib.test;


import top.hendrixshen.magiclib.api.malilib.annotation.Config;
import top.hendrixshen.magiclib.api.malilib.config.MagicConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.MagicConfigFactory;
import top.hendrixshen.magiclib.impl.malilib.config.option.MagicConfigBoolean;
import top.hendrixshen.magiclib.impl.malilib.config.option.MagicConfigBooleanHotkeyed;
import top.hendrixshen.magiclib.impl.malilib.config.option.MagicConfigHotkey;

public class MagicConfig {

    private static final MagicConfigManager cm = ModInfo.CONFIG_MANAGER;
    private static final MagicConfigFactory cf = cm.getConfigFactory();

    @Config(category = "general")

    public static MagicConfigHotkey openConfigGui = cf.newConfigHotkey("openConfigGui", "U,C");

    @Config(category = "features")
    public static MagicConfigBoolean coolFeature = cf.newConfigBoolean("coolFeature", false);

    @Config(category = "features")
    public static MagicConfigBooleanHotkeyed coolFeatureHot =
            cf.newConfigBooleanHotkeyed("coolFeatureHot", false);


    public static void init() {
        cm.parseConfigClass(MagicConfig.class);
        MagicConfigManager.setHotkeyCallback(openConfigGui, MagicConfigsGui::openGui, true);

    }

    public static class ConfigCategory {
        public static final String GENERAL = "general";
        public static final String FEATURES = "features";
    }
}
