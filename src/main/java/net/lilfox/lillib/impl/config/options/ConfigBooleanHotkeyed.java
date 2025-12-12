package net.lilfox.lillib.impl.config.options;

import net.lilfox.lillib.api.config.IConfigBooleanHotkeyed;
import net.lilfox.lillib.impl.hotkey.ConflictDetector;
import net.lilfox.lillib.impl.hotkey.KeybindManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of boolean configuration options with hotkey support.
 * <p>
 * This class automatically registers itself with the hotkey system upon creation,
 * enabling hotkey detection and conflict resolution without manual registration.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: <a href="https://github.com/sakura-ryoko/malilib">...</a>
 * Licensed under the GNU Lesser General Public License v3.0
 *
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigBooleanHotkeyed extends ConfigBoolean implements IConfigBooleanHotkeyed {
    private static final Logger LOGGER = LoggerFactory.getLogger("lillib");

    protected final String defaultHotkey;
    protected String hotkey;
    protected Runnable activationCallback;

    /**
     * Creates a new boolean configuration with hotkey support.
     * <p>
     * This constructor automatically registers the config with:
     * <ul>
     *   <li>{@link KeybindManager} - for hotkey detection</li>
     *   <li>{@link ConflictDetector} - for conflict tracking</li>
     * </ul>
     *
     * @param name The internal name
     * @param category The category
     * @param defaultValue The default boolean value
     * @param defaultHotkey The default hotkey binding (can be empty string)
     */
    public ConfigBooleanHotkeyed(String name, String category, boolean defaultValue, String defaultHotkey) {
        super(name, category, defaultValue);
        this.defaultHotkey = defaultHotkey != null ? defaultHotkey : "";
        this.hotkey = this.defaultHotkey;

        // Auto-register with hotkey systems
        registerHotkey();
    }

    /**
     * Registers this config with the hotkey management systems.
     * <p>
     * Called automatically during construction. Also called when
     * hotkey changes to update registrations.
     */
    private void registerHotkey() {
        if (!this.hotkey.isEmpty()) {
            try {
                KeybindManager.getInstance().registerHotkey(this);
                ConflictDetector.registerHotkey(this);
                LOGGER.debug("Registered hotkey '{}' for config '{}'", this.hotkey, this.getName());
            } catch (Exception e) {
                LOGGER.error("Failed to register hotkey for config '{}'", this.getName(), e);
            }
        }
    }

    @Override
    public String getHotkey() {
        return hotkey;
    }

    @Override
    public void setHotkey(String hotkey) {
        String oldHotkey = this.hotkey;
        this.hotkey = hotkey != null ? hotkey : "";

        if (!oldHotkey.equals(this.hotkey)) {
            // Unregister old hotkey
            if (!oldHotkey.isEmpty()) {
                ConflictDetector.unregisterHotkey(this);
                KeybindManager.getInstance().updateHotkey(this, oldHotkey);
            }

            // Register new hotkey
            if (!this.hotkey.isEmpty()) {
                registerHotkey();
            }

            onValueChanged();
        }
    }

    @Override
    public String getDefaultHotkey() {
        return defaultHotkey;
    }

    @Override
    public boolean hasConflicts() {
        if (hotkey.isEmpty()) {
            return false;
        }
        return ConflictDetector.hasConflicts(this);
    }

    @Override
    public String[] getConflicts() {
        if (hotkey.isEmpty()) {
            return new String[0];
        }
        return ConflictDetector.getConflicts(this);
    }

    @Override
    public void setActivationCallback(Runnable callback) {
        this.activationCallback = callback;
    }

    @Override
    public Runnable getActivationCallback() {
        return activationCallback;
    }

    @Override
    public boolean hasActivationCallback() {
        return activationCallback != null;
    }

    /**
     * Called when the hotkey is activated.
     * <p>
     * This method is invoked by the hotkey handler system.
     * It toggles the boolean value, shows a notification, and
     * invokes any registered activation callback.
     */
    public void onHotkeyActivated() {
        LOGGER.debug("Hotkey activated for config: {}", this.getName());

        toggleBooleanValue();

        // Show notification about the change
        net.lilfox.lillib.impl.util.NotificationHelper.notifyBooleanChange(
                getNiceName(), getBooleanValue()
        );

        if (activationCallback != null) {
            try {
                activationCallback.run();
                LOGGER.debug("Activation callback executed for: {}", this.getName());
            } catch (Exception e) {
                LOGGER.error("Error in activation callback for '{}'", this.getName(), e);
            }
        }
    }

    @Override
    public boolean isModified() {
        if (super.isModified()) {
            return true;
        }
        return !hotkey.equals(defaultHotkey);
    }

    @Override
    public void resetToDefault() {
        super.resetToDefault();
        setHotkey(defaultHotkey);
    }
}