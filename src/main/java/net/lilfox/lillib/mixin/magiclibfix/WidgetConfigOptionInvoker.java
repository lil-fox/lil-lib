package net.lilfox.lillib.mixin.magiclibfix;

import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(
        value = {WidgetConfigOption.class},
        priority = 999,
        remap = false
)
public interface WidgetConfigOptionInvoker {
    @Invoker("addKeybindResetButton")
    void invokeAddKeybindResetButton(int var1, int var2, IKeybind var3, ConfigButtonKeybind var4);
}
