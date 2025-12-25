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
 * <p><b>Updated for factory pattern:</b>
 * Constructor now properly handles empty category for factory-created configs.
 * Category can be overridden by ConfigParser when processing @Config annotations.
 *
 * <p><b>Fixed version:</b>
 * - setShowEffect() only triggers onValueChanged() if value actually changed
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
     * <p>
     * When created via {@link net.lilfox.lillib.impl.config.ConfigFactory},
     * the category will be empty initially and set later by ConfigParser
     * when processing the @Config annotation.
     *
     * @param name The internal name
     * @param category The category (can be empty for factory-created configs)
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
        // CRITICAL FIX: Only update if value actually changed
        if (this.showEffect == show) {
            return;
        }

        this.showEffect = show;
        onValueChanged();
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
        System.out.println("hasEffect && showEffect != defaultShowEffect === " + (hasEffect && showEffect != defaultShowEffect));
        return hasEffect && showEffect != defaultShowEffect;
    }

    @Override
    public void resetToDefault() {
        setBooleanValue(defaultValue);
        if (hasEffect) {
            setShowEffect(defaultShowEffect);
        }
    }
}