package net.lilfox.lillib.impl.config.options;

import net.lilfox.lillib.api.callback.IConfigValueChangeCallback;
import net.lilfox.lillib.api.config.IConfigInteger;

/**
 * Implementation of integer configuration options.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigInteger extends ConfigBase implements IConfigInteger {
    protected final int defaultValue;
    protected final int minValue;
    protected final int maxValue;
    protected final boolean useSlider;
    protected int value;
    protected IConfigValueChangeCallback<Integer> callback;

    /**
     * Creates a new integer configuration.
     * 
     * @param name The internal name
     * @param category The category
     * @param defaultValue The default value
     * @param minValue The minimum allowed value
     * @param maxValue The maximum allowed value
     * @param useSlider Whether to display as a slider
     */
    public ConfigInteger(String name, String category, int defaultValue, int minValue, int maxValue, boolean useSlider) {
        super(name, category);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.useSlider = useSlider;
        this.value = defaultValue;
    }

    /**
     * Creates a new integer configuration without slider.
     * 
     * @param name The internal name
     * @param category The category
     * @param defaultValue The default value
     * @param minValue The minimum allowed value
     * @param maxValue The maximum allowed value
     */
    public ConfigInteger(String name, String category, int defaultValue, int minValue, int maxValue) {
        this(name, category, defaultValue, minValue, maxValue, false);
    }

    @Override
    public int getIntegerValue() {
        return value;
    }

    @Override
    public void setIntegerValue(int value) {
        int oldValue = this.value;
        this.value = clampValue(value);
        
        if (oldValue != this.value) {
            if (callback != null) {
                callback.onValueChanged(this.value, oldValue);
            }
            onValueChanged();
        }
    }

    @Override
    public int getDefaultIntegerValue() {
        return defaultValue;
    }

    @Override
    public int getMinValue() {
        return minValue;
    }

    @Override
    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public boolean useSlider() {
        return useSlider;
    }

    @Override
    public void setValueChangeCallback(IConfigValueChangeCallback<Integer> callback) {
        this.callback = callback;
    }

    @Override
    public boolean isModified() {
        return value != defaultValue;
    }

    @Override
    public void resetToDefault() {
        setIntegerValue(defaultValue);
    }

    /**
     * Clamps a value to the allowed range.
     * 
     * @param value The value to clamp
     * @return The clamped value
     */
    protected int clampValue(int value) {
        return Math.max(minValue, Math.min(maxValue, value));
    }
}
