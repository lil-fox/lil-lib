package net.lilfox.lillib.client;

import net.fabricmc.api.ClientModInitializer;
import net.lilfox.lillib.api.effect.EffectRegistry;
import net.lilfox.lillib.effect.FakeEffect;
import net.lilfox.lillib.impl.hotkey.HotkeyHandler;
import net.lilfox.lillib.impl.hotkey.KeybindManager;
import net.lilfox.lillib.test.Configs;
import net.lilfox.lillib.utils.ParticleUtils;

public class LilLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        Configs.init();
        KeybindManager.getInstance().debugPrint();
        FakeEffect testEffect = new FakeEffect();
        EffectRegistry.register("lillib" ,"fake_effect", testEffect, Configs.coolFeatureHot);
        //ConfigInitializer.init();
        HotkeyHandler.init();
        ParticleUtils.init();
    }
}
