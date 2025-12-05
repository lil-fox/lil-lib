package net.lilfox.lillib.test;

import net.lilfox.lillib.api.gui.LillibConfigScreen;
import net.lilfox.lillib.impl.gui.LillibConfigScreenImpl;
import net.minecraft.client.MinecraftClient;

public class ConfigsGui extends LillibConfigScreenImpl {
    public ConfigsGui() {
        super("lillib", "lillib.gui.title");
    }
    public static void open() {
        MinecraftClient.getInstance().setScreen(new ConfigsGui());
    }
}
