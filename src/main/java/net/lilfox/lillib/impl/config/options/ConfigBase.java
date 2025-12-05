package net.lilfox.lillib.impl.config.options;

import net.lilfox.lillib.api.config.IConfigBase;
import net.lilfox.lillib.impl.util.LocalizationHelper;

/**
 * Abstract base implementation for all configuration types.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 * 
 * @author lilfox
 * @since 1.0.0
 */
public abstract class ConfigBase implements IConfigBase {
    protected final String name;
    protected final String category;
    protected String modId;

    /**
     * Creates a new configuration base.
     * 
     * @param name The internal name of this configuration
     * @param category The category this configuration belongs to
     */
    protected ConfigBase(String name, String category) {
        this.name = name;
        this.category = category;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getModId() {
        return modId;
    }

    @Override
    public void setModId(String modId) {
        this.modId = modId;
    }

    @Override
    public String getDisplayName() {
        if (modId == null) {
            return name;
        }
        return LocalizationHelper.getConfigName(modId, name);
    }

    @Override
    public String getNiceName() {
        if (modId == null) {
            return name;
        }
        return LocalizationHelper.getConfigNiceName(modId, name);
    }

    @Override
    public String getDescription() {
        if (modId == null) {
            return "";
        }
        return LocalizationHelper.getConfigDescription(modId, name);
    }

    /**
     * Notifies the config manager that this configuration has changed.
     * <p>
     * This triggers automatic saving to file.
     */
    protected void onValueChanged() {
        if (modId != null) {
            net.lilfox.lillib.impl.config.ConfigManager.getInstance().saveConfig(modId);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name='" + name + "', category='" + category + "'}";
    }
}
