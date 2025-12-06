package net.lilfox.lillib.impl.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.lilfox.lillib.api.config.IConfigBase;
import net.lilfox.lillib.api.gui.LillibConfigScreen;
import net.lilfox.lillib.impl.config.ConfigManager;
import net.lilfox.lillib.impl.gui.widgets.ConfigEntryWidget;
import net.lilfox.lillib.impl.gui.widgets.SearchBar;
import net.lilfox.lillib.impl.util.LocalizationHelper;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * Implementation of the configuration screen.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 *
 * <p><b>Fixed version:</b> Tab buttons now properly display text for all tabs
 *
 * @author lilfox
 * @since 1.0.0
 */
public class LillibConfigScreenImpl extends LillibConfigScreen {
    private final Map<String, List<IConfigBase>> configsByCategory;
    private final List<String> categoryNames;
    private String currentCategory;
    private final List<ConfigEntryWidget> configWidgets;
    private final List<ButtonWidget> tabButtons;
    private SearchBar searchBar;
    private int scrollOffset;
    private final int entryHeight = 24;
    private final int listTop = 60;
    private int listHeight;

    /**
     * Creates a new configuration screen implementation.
     *
     * @param modId The mod ID
     * @param titleKey The title translation key
     */
    public LillibConfigScreenImpl(String modId, String titleKey) {
        super(modId, titleKey);
        this.configsByCategory = ConfigManager.getInstance().getConfigsByCategory(modId);
        this.categoryNames = new ArrayList<>(configsByCategory.keySet());
        Collections.sort(this.categoryNames);
        this.currentCategory = categoryNames.isEmpty() ? null : categoryNames.get(0);
        this.configWidgets = new ArrayList<>();
        this.tabButtons = new ArrayList<>();
        this.scrollOffset = 0;
    }

    @Override
    protected void init() {
        super.init();

        if (this.client == null) {
            return;
        }

        this.listHeight = this.height - listTop - 30;
        this.clearChildren();
        this.configWidgets.clear();
        this.tabButtons.clear();

        // Create search bar
        int searchWidth = 200;
        int searchX = (this.width - searchWidth) / 2;
        this.searchBar = new SearchBar(this.textRenderer, searchX, 10, searchWidth, 20);
        this.searchBar.setOnChangeCallback(this::rebuildConfigList);
        this.addDrawableChild(this.searchBar);

        // Create category tabs
        createCategoryTabs();

        // Create config list
        rebuildConfigList();

        // Create close button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(LocalizationHelper.getLibTranslation("button.done")),
                button -> this.close()
        ).dimensions(this.width / 2 - 50, this.height - 25, 100, 20).build());
    }

    /**
     * Creates category tab buttons.
     * <p>
     * <b>Fixed:</b> Buttons now properly update their active state and display text correctly.
     */
    private void createCategoryTabs() {
        if (categoryNames.isEmpty()) {
            return;
        }

        int tabWidth = 100;
        int tabHeight = 20;
        int tabSpacing = 5;
        int totalWidth = (tabWidth + tabSpacing) * categoryNames.size() - tabSpacing;
        int startX = (this.width - totalWidth) / 2;

        for (int i = 0; i < categoryNames.size(); i++) {
            String category = categoryNames.get(i);
            int tabX = startX + i * (tabWidth + tabSpacing);

            // Create button with proper text
            Text buttonText = Text.literal(LocalizationHelper.getCategoryName(modId, category));

            ButtonWidget tabButton = ButtonWidget.builder(
                    buttonText,
                    button -> selectCategory(category)
            ).dimensions(tabX, 35, tabWidth, tabHeight).build();

            // Mark current category button as active
            if (category.equals(currentCategory)) {
                tabButton.active = false;
            }

            this.tabButtons.add(tabButton);
            this.addDrawableChild(tabButton);
        }
    }

    /**
     * Selects a category and rebuilds the config list.
     *
     * @param category The category to select
     */
    private void selectCategory(String category) {
        if (this.currentCategory != null && this.currentCategory.equals(category)) {
            return; // Already selected
        }

        this.currentCategory = category;
        this.scrollOffset = 0;

        // Update button states
        updateTabButtonStates();

        rebuildConfigList();
    }

    /**
     * Updates the active state of tab buttons based on current category.
     */
    private void updateTabButtonStates() {
        for (int i = 0; i < tabButtons.size(); i++) {
            ButtonWidget button = tabButtons.get(i);
            String category = categoryNames.get(i);

            // Active button = not clickable (currently selected)
            button.active = !category.equals(currentCategory);
        }
    }

    /**
     * Rebuilds the config list based on current category and search.
     */
    private void rebuildConfigList() {
        configWidgets.clear();

        if (currentCategory == null) {
            return;
        }

        List<IConfigBase> configs = configsByCategory.get(currentCategory);
        if (configs == null) {
            return;
        }

        int yOffset = listTop;
        for (IConfigBase config : configs) {
            // Apply search filter
            if (searchBar != null && !searchBar.matches(config.getDisplayName(), config.getDescription())) {
                continue;
            }

            ConfigEntryWidget widget = new ConfigEntryWidget(
                    config, 10, yOffset, this.width - 20, entryHeight, this.textRenderer
            );
            configWidgets.add(widget);
            yOffset += entryHeight;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFF);

        // Render current category name above config list
        if (currentCategory != null) {
            String categoryDisplay = LocalizationHelper.getCategoryName(modId, currentCategory);
            context.drawText(this.textRenderer, categoryDisplay, 15, listTop - 15, 0xFFFFFF, false);
        }

        // Enable scissor for scrollable list
        int listBottom = listTop + listHeight;
        context.enableScissor(0, listTop, this.width, listBottom);

        // Render config widgets with scroll offset
        for (int i = 0; i < configWidgets.size(); i++) {
            ConfigEntryWidget widget = configWidgets.get(i);
            int widgetY = i * entryHeight + listTop - scrollOffset;

            if (widgetY + entryHeight > listTop && widgetY < listBottom) {
                widget.render(context, mouseX, mouseY, delta);
            }
        }

        context.disableScissor();

        // Render other widgets (search, buttons, tabs)
        // This will render tab buttons with their proper text
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        // Check config widgets with scroll adjustment
        double adjustedMouseY = click.y() + scrollOffset;
        for (ConfigEntryWidget widget : configWidgets) {
            if (widget.mouseClicked(new Click(click.x(), adjustedMouseY, click.buttonInfo()), doubled)) {
                return true;
            }
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // Handle scrolling in config list
        if (mouseY >= listTop && mouseY < listTop + listHeight) {
            int totalHeight = configWidgets.size() * entryHeight;
            int maxScroll = Math.max(0, totalHeight - listHeight);

            scrollOffset -= (int) (verticalAmount * 10);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        // Check if editing hotkey
        for (ConfigEntryWidget widget : configWidgets) {
            if (widget.isEditingHotkey()) {
                if (widget.keyPressed(input)) {
                    return true;
                }
            }
        }

        // ESC closes screen (unless editing hotkey)
        if (input.isEscape() && !isEditingHotkey()) {
            this.close();
            return true;
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean isEditingHotkey() {
        for (ConfigEntryWidget widget : configWidgets) {
            if (widget.isEditingHotkey()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() {
        // Save all configs before closing
        ConfigManager.getInstance().saveConfig(modId);
        super.close();
    }
}