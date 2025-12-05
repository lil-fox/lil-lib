package net.lilfox.lillib.impl.config.options;

import net.lilfox.lillib.api.callback.IConfigValueChangeCallback;
import net.lilfox.lillib.api.config.IConfigDouble;

/**
 * Implementation of double configuration options.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigDouble extends ConfigBase implements IConfigDouble {
    protected final double defaultValue;
    protected final double minValue;
    protected final double maxValue;
    protected final boolean useSlider;
    protected double value;
    protected IConfigValueChangeCallback<Double> callback;

    /**
     * Creates a new double configuration.
     * 
     * @param name The internal name
     * @param category The category
     * @param defaultValue The default value
     * @param minValue The minimum allowed value
     * @param maxValue The maximum allowed value
     * @param useSlider Whether to display as a slider
     */
    public ConfigDouble(String name, String category, double defaultValue, double minValue, double maxValue, boolean useSlider) {
        super(name, category);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.useSlider = useSlider;
        this.value = defaultValue;
    }

    /**
     * Creates a new double configuration without slider.
     * 
     * @param name The internal name
     * @param category The category
     * @param defaultValue The default value
     * @param minValue The minimum allowed value
     * @param maxValue The maximum allowed value
     */
    public ConfigDouble(String name, String category, double defaultValue, double minValue, double maxValue) {
        this(name, category, defaultValue, minValue, maxValue, false);
    }

    @Override
    public double getDoubleValue() {
        return value;
    }

    @Override
    public void setDoubleValue(double value) {
        double oldValue = this.value;
        this.value = clampValue(value);
        
        if (Math.abs(oldValue - this.value) > 0.000001) {
            if (callback != null) {
                callback.onValueChanged(this.value, oldValue);
            }
            onValueChanged();
        }
    }

    @Override
    public double getDefaultDoubleValue() {
        return defaultValue;
    }

    @Override
    public double getMinValue() {
        return minValue;
    }

    @Override
    public double getMaxValue() {
        return maxValue;
    }

    @Override
    public boolean useSlider() {
        return useSlider;
    }

    @Override
    public void setValueChangeCallback(IConfigValueChangeCallback<Double> callback) {
        this.callback = callback;
    }

    @Override
    public boolean isModified() {
        return Math.abs(value - defaultValue) > 0.000001;
    }

    @Override
    public void resetToDefault() {
        setDoubleValue(defaultValue);
    }

    /**
     * Clamps a value to the allowed range.
     * 
     * @param value The value to clamp
     * @return The clamped value
     */
    protected double clampValue(double value) {
        return Math.max(minValue, Math.min(maxValue, value));
    }
}
