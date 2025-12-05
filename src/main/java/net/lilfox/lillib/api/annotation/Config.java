package net.lilfox.lillib.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a configuration option.
 * <p>
 * This annotation is used to automatically parse and register configuration fields.
 * The field name will be used as the configuration key, and localization keys will be
 * automatically generated in the format:
 * <ul>
 *   <li>{@code fieldName} - Display name in GUI</li>
 *   <li>{@code fieldName.nice} - Nice name for notifications</li>
 *   <li>{@code fieldName.desc} - Description tooltip</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * @Config(category = "general")
 * public static boolean myConfig = false;
 * }</pre>
 * 
 * @author lilfox
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    /**
     * The category this configuration belongs to.
     * <p>
     * Categories are used to organize configurations into tabs in the GUI.
     * The category name will be localized using the key format:
     * {@code modId.config.category.categoryName}
     * 
     * @return The category name
     */
    String category();
}
