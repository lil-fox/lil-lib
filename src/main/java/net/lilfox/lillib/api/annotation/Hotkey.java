package net.lilfox.lillib.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds hotkey support to a boolean configuration field.
 * <p>
 * This annotation can be used with {@code boolean} fields to make them toggleable
 * via keyboard shortcuts. The hotkey format uses comma-separated key names in the
 * order they should be pressed.
 * 
 * <p>Hotkey format examples:
 * <ul>
 *   <li>{@code "CTRL,N"} - Press Ctrl, then N</li>
 *   <li>{@code "CTRL,SHIFT,A"} - Press Ctrl, then Shift, then A</li>
 *   <li>{@code ""} - No default hotkey (can be configured in GUI)</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * @Config(category = "general")
 * @Hotkey(hotkey = "U,C")
 * public static boolean openConfigGui = false;
 * }</pre>
 * 
 * @author lilfox
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hotkey {
    /**
     * The default hotkey binding in comma-separated format.
     * <p>
     * Keys should be specified in the order they need to be pressed.
     * Use an empty string for no default hotkey.
     * 
     * @return The default hotkey string
     */
    String hotkey() default "";
}
