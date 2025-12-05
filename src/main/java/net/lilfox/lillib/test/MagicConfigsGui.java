package net.lilfox.lillib.test;

import fi.dy.masa.malilib.gui.GuiBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.impl.malilib.config.gui.MagicConfigGui;
import top.hendrixshen.magiclib.util.collect.ValueContainer;

public class MagicConfigsGui extends MagicConfigGui {

    @Nullable
    private static MagicConfigsGui INSTANCE = null;

    public MagicConfigsGui() {
        super(ModInfo.MOD_ID, ModInfo.CONFIG_MANAGER, "lillib.title");

    }

    @Override
    public void init() {
        super.init();
        INSTANCE = this;
    }

    @Override
    public void removed() {
        super.removed();
        INSTANCE = null;
    }


    public static void openGui() {
        GuiBase.openGui(new MagicConfigsGui());
    }

    @Override
    public boolean hideUnAvailableConfigs() {
        return true;
    }

    public static @NotNull ValueContainer<MagicConfigsGui> getCurrentInstance() {
        return ValueContainer.ofNullable(INSTANCE);
    }
}
