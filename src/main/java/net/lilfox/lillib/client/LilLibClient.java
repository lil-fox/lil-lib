package net.lilfox.lillib.client;

import net.fabricmc.api.ClientModInitializer;
import net.lilfox.lillib.utils.ParticleUtils;

public class LilLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParticleUtils.init();
    }
}
