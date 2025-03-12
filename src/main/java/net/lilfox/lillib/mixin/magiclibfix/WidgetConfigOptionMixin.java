package net.lilfox.lillib.mixin.magiclibfix;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetKeybindSettings;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        value = {WidgetConfigOption.class},
        priority = 999,
        remap = false
)

public abstract class WidgetConfigOptionMixin extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper> {
    public WidgetConfigOptionMixin(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex) {
        super(x, y, width, height, parent, entry, listIndex);
    }

    @Inject(
            method = {"addHotkeyConfigElements"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void deleteIdioticTriggerButtonFromMagicLibNew(int x, int y, int configWidth, String configName, IHotkey hotkey, CallbackInfo ci) {
        configWidth -= 22;
        IKeybind keybind = hotkey.getKeybind();
        ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(x, y, configWidth, 20, keybind, ((WidgetConfigOptionAccessor)this).getHost());
        x += configWidth + 2;
        this.addWidget(new WidgetKeybindSettings(x, y, 20, 20, keybind, configName, this.parent, ((WidgetConfigOptionAccessor)this).getHost().getDialogHandler()));
        x += 22;
        this.addButton(keybindButton, ((WidgetConfigOptionAccessor)this).getHost().getButtonPressListener());
        ((WidgetConfigOptionInvoker)this).invokeAddKeybindResetButton(x, y, keybind, keybindButton);
        ci.cancel();
    }
}
