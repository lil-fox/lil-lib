package net.lilfox.lillib.impl.config.options;

import net.lilfox.lillib.api.callback.IConfigValueChangeCallback;
import net.lilfox.lillib.api.config.IConfigBoolean;

/**
 * Implementation of boolean configuration options.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigBoolean extends ConfigBase implements IConfigBoolean {
    protected final boolean defaultValue;
    protected boolean value;
    protected boolean hasEffect;
    protected boolean showEffect;
    protected final boolean defaultShowEffect;
    protected IConfigValueChangeCallback<Boolean> callback;

    /**
     * Creates a new boolean configuration.
     * 
     * @param name The internal name
     * @param category The category
     * @param defaultValue The default value
     */
    public ConfigBoolean(String name, String category, boolean defaultValue) {
        super(name, category);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.hasEffect = false;
        this.showEffect = true;
        this.defaultShowEffect = true;
    }

    @Override
    public boolean getBooleanValue() {
        return value;
    }

    @Override
    public void setBooleanValue(boolean value) {
        boolean oldValue = this.value;
        this.value = value;
        
        if (oldValue != value) {
            if (callback != null) {
                callback.onValueChanged(value, oldValue);
            }
            onValueChanged();
        }
    }

    @Override
    public boolean getDefaultBooleanValue() {
        return defaultValue;
    }

    @Override
    public void toggleBooleanValue() {
        setBooleanValue(!value);
    }

    @Override
    public void setValueChangeCallback(IConfigValueChangeCallback<Boolean> callback) {
        this.callback = callback;
    }

    @Override
    public boolean hasEffect() {
        return hasEffect;
    }

    @Override
    public void bindEffect() {
        this.hasEffect = true;
    }

    @Override
    public boolean shouldShowEffect() {
        return hasEffect && showEffect && value;
    }

    @Override
    public void setShowEffect(boolean show) {
        boolean oldShow = this.showEffect;
        this.showEffect = show;
        
        if (oldShow != show) {
            onValueChanged();
        }
    }

    @Override
    public boolean getShowEffect() {
        return showEffect;
    }

    @Override
    public boolean getDefaultShowEffect() {
        return defaultShowEffect;
    }

    @Override
    public boolean isModified() {
        if (value != defaultValue) {
            return true;
        }
        if (hasEffect && showEffect != defaultShowEffect) {
            return true;
        }
        return false;
    }

    @Override
    public void resetToDefault() {
        setBooleanValue(defaultValue);
        if (hasEffect) {
            setShowEffect(defaultShowEffect);
        }
    }
}
