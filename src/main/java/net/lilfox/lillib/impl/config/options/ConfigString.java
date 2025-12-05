package net.lilfox.lillib.impl.config.options;

import net.lilfox.lillib.api.callback.IConfigValueChangeCallback;
import net.lilfox.lillib.api.config.IConfigString;

/**
 * Implementation of string configuration options.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigString extends ConfigBase implements IConfigString {
    protected final String defaultValue;
    protected String value;
    protected IConfigValueChangeCallback<String> callback;

    /**
     * Creates a new string configuration.
     * 
     * @param name The internal name
     * @param category The category
     * @param defaultValue The default value
     */
    public ConfigString(String name, String category, String defaultValue) {
        super(name, category);
        this.defaultValue = defaultValue != null ? defaultValue : "";
        this.value = this.defaultValue;
    }

    @Override
    public String getStringValue() {
        return value;
    }

    @Override
    public void setStringValue(String value) {
        String oldValue = this.value;
        this.value = value != null ? value : "";
        
        if (!oldValue.equals(this.value)) {
            if (callback != null) {
                callback.onValueChanged(this.value, oldValue);
            }
            onValueChanged();
        }
    }

    @Override
    public String getDefaultStringValue() {
        return defaultValue;
    }

    @Override
    public void setValueChangeCallback(IConfigValueChangeCallback<String> callback) {
        this.callback = callback;
    }

    @Override
    public boolean isModified() {
        return !value.equals(defaultValue);
    }

    @Override
    public void resetToDefault() {
        setStringValue(defaultValue);
    }
}
