package net.lilfox.lillib.test;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;


public class ConfigInitializer {

    public static void init() {
        InitializationHandler.getInstance().registerInitializationHandler(() ->
                ConfigManager.getInstance().registerConfigHandler(ModInfo.MOD_ID,
                        ModInfo.CONFIG_HANDLER));
        MagicConfig.init();
        InputEventHandler.getKeybindManager().registerKeybindProvider(
                (IKeybindProvider) ModInfo.CONFIG_MANAGER);
    }
}
