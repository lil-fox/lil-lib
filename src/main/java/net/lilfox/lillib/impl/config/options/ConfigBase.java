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
 * <p><b>Fixed version:</b> Category field is now mutable to allow ConfigParser
 * to set it from @Config annotation.
 *
 * @author lilfox
 * @since 1.0.0
 */
public abstract class ConfigBase implements IConfigBase {
    protected final String name;
    protected String category;  // NOT final - can be overridden by ConfigParser
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

    /**
     * Sets the category for this configuration.
     * <p>
     * This method is package-private and intended to be called only by ConfigParser
     * when applying @Config annotation metadata.
     *
     * @param category The category to set
     */
    public void setCategory(String category) {
        this.category = category;
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