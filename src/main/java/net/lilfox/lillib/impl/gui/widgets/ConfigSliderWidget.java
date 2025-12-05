package net.lilfox.lillib.impl.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.lilfox.lillib.api.config.IConfigDouble;
import net.lilfox.lillib.api.config.IConfigInteger;

/**
 * Slider widget for numeric configurations.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigSliderWidget extends SliderWidget {
    private final Object config;
    private final boolean isInteger;
    private final double minValue;
    private final double maxValue;

    /**
     * Creates a slider for an integer configuration.
     * 
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param config The integer configuration
     */
    public ConfigSliderWidget(int x, int y, int width, int height, IConfigInteger config) {
        super(x, y, width, height, Text.empty(), 0.0);
        this.config = config;
        this.isInteger = true;
        this.minValue = config.getMinValue();
        this.maxValue = config.getMaxValue();
        
        // Set initial value
        int currentValue = config.getIntegerValue();
        this.value = (currentValue - minValue) / (maxValue - minValue);
        updateMessage();
    }

    /**
     * Creates a slider for a double configuration.
     * 
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param config The double configuration
     */
    public ConfigSliderWidget(int x, int y, int width, int height, IConfigDouble config) {
        super(x, y, width, height, Text.empty(), 0.0);
        this.config = config;
        this.isInteger = false;
        this.minValue = config.getMinValue();
        this.maxValue = config.getMaxValue();
        
        // Set initial value
        double currentValue = config.getDoubleValue();
        this.value = (currentValue - minValue) / (maxValue - minValue);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        if (isInteger) {
            int intValue = (int) Math.round(minValue + value * (maxValue - minValue));
            this.setMessage(Text.literal(String.valueOf(intValue)));
        } else {
            double doubleValue = minValue + value * (maxValue - minValue);
            this.setMessage(Text.literal(String.format("%.2f", doubleValue)));
        }
    }

    @Override
    protected void applyValue() {
        if (isInteger) {
            IConfigInteger intConfig = (IConfigInteger) config;
            int intValue = (int) Math.round(minValue + value * (maxValue - minValue));
            intConfig.setIntegerValue(intValue);
        } else {
            IConfigDouble doubleConfig = (IConfigDouble) config;
            double doubleValue = minValue + value * (maxValue - minValue);
            doubleConfig.setDoubleValue(doubleValue);
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Sync slider with config value
        if (isInteger) {
            IConfigInteger intConfig = (IConfigInteger) config;
            int currentValue = intConfig.getIntegerValue();
            this.value = (currentValue - minValue) / (maxValue - minValue);
        } else {
            IConfigDouble doubleConfig = (IConfigDouble) config;
            double currentValue = doubleConfig.getDoubleValue();
            this.value = (currentValue - minValue) / (maxValue - minValue);
        }
        
        updateMessage();
        super.renderWidget(context, mouseX, mouseY, delta);
    }
}
