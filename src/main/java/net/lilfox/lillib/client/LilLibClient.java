package net.lilfox.lillib.client;

import net.fabricmc.api.ClientModInitializer;
import net.lilfox.lillib.impl.hotkey.HotkeyHandler;
import net.lilfox.lillib.test.Configs;
import net.lilfox.lillib.utils.ParticleUtils;

public class LilLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        Configs.init();
        //ConfigInitializer.init();
        HotkeyHandler.init();
        ParticleUtils.init();
    }
}
