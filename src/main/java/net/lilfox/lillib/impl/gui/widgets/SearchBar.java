package net.lilfox.lillib.impl.gui.widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.lilfox.lillib.impl.util.LocalizationHelper;

/**
 * Search bar widget for filtering configurations.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: <a href="https://github.com/sakura-ryoko/malilib">...</a>
 * Licensed under the GNU Lesser General Public License v3.0
 *
 * @author lilfox
 * @since 1.0.0
 */
public class SearchBar extends TextFieldWidget {
    private Runnable onChangeCallback;

    /**
     * Creates a new search bar widget.
     * 
     * @param textRenderer The text renderer
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     */
    public SearchBar(TextRenderer textRenderer, int x, int y, int width, int height) {
        super(textRenderer, x, y, width, height, Text.literal(""));
        
        this.setMaxLength(256);
        this.setDrawsBackground(true);
        this.setPlaceholder(Text.literal(LocalizationHelper.getLibTranslation("search")));
    }

    /**
     * Sets a callback to be invoked when the search text changes.
     * 
     * @param callback The callback to invoke
     */
    public void setOnChangeCallback(Runnable callback) {
        this.onChangeCallback = callback;
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        if (onChangeCallback != null) {
            onChangeCallback.run();
        }
    }

    /**
     * Gets the current search query in lowercase for case-insensitive matching.
     * 
     * @return The search query
     */
    public String getQuery() {
        return getText().toLowerCase();
    }

    /**
     * Checks if the search bar has a query.
     * 
     * @return true if query is not empty
     */
    public boolean hasQuery() {
        return !getText().isEmpty();
    }

    /**
     * Clears the search query.
     */
    public void clearQuery() {
        setText("");
    }

    /**
     * Checks if a config matches the current search query.
     * <p>
     * Searches in both config name and description.
     * 
     * @param name The config display name
     * @param description The config description
     * @return true if matches
     */
    public boolean matches(String name, String description) {
        if (!hasQuery()) {
            return true;
        }
        
        String query = getQuery();
        String nameLower = name.toLowerCase();
        String descLower = description.toLowerCase();
        
        return nameLower.contains(query) || descLower.contains(query);
    }
}
