package net.lilfox.lillib.mixin.magiclibfix;

import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
        value = {WidgetConfigOption.class},
        priority = 999,
        remap = false
)
public interface WidgetConfigOptionAccessor {
    @Accessor("host")
    IKeybindConfigGui getHost();
}