package net.lilfox.lillib.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds numeric constraints and slider support to numeric configuration fields.
 * <p>
 * This annotation is used with {@code int}, {@code double}, or {@code float} fields
 * to specify minimum and maximum values, and optionally enable a slider widget in the GUI.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @Config(category = "tweaks")
 * @Numeric(minValue = 0, maxValue = 100, useSlider = true)
 * public static int someValue = 50;
 * }</pre>
 * 
 * @author lilfox
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Numeric {
    /**
     * The minimum allowed value for this numeric configuration.
     * <p>
     * The GUI will prevent users from setting values below this limit.
     * 
     * @return The minimum value
     */
    double minValue();

    /**
     * The maximum allowed value for this numeric configuration.
     * <p>
     * The GUI will prevent users from setting values above this limit.
     * 
     * @return The maximum value
     */
    double maxValue();

    /**
     * Whether to display this configuration as a slider in the GUI.
     * <p>
     * If {@code true}, a slider widget will be shown instead of a text field,
     * making it easier to adjust values within the min/max range.
     * 
     * @return {@code true} to use a slider, {@code false} for a text field
     */
    boolean useSlider() default false;
}
