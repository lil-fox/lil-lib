package net.lilfox.lillib.impl.config.options;

import net.lilfox.lillib.api.config.IConfigBooleanHotkeyed;
import net.lilfox.lillib.impl.hotkey.ConflictDetector;

/**
 * Implementation of boolean configuration options with hotkey support.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 *
 * @author lilfox
 * @since 1.0.0
 */
public class ConfigBooleanHotkeyed extends ConfigBoolean implements IConfigBooleanHotkeyed {
    protected final String defaultHotkey;
    protected String hotkey;
    protected Runnable activationCallback;

    /**
     * Creates a new boolean configuration with hotkey support.
     *
     * @param name The internal name
     * @param category The category
     * @param defaultValue The default boolean value
     * @param defaultHotkey The default hotkey binding
     */
    public ConfigBooleanHotkeyed(String name, String category, boolean defaultValue, String defaultHotkey) {
        super(name, category, defaultValue);
        this.defaultHotkey = defaultHotkey != null ? defaultHotkey : "";
        this.hotkey = this.defaultHotkey;
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
            }

            // Register new hotkey
            if (!this.hotkey.isEmpty()) {
                ConflictDetector.registerHotkey(this);
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
     */
    public void onHotkeyActivated() {
        toggleBooleanValue();

        // Show notification about the change
        net.lilfox.lillib.impl.util.NotificationHelper.notifyBooleanChange(
                getNiceName(), getBooleanValue()
        );

        if (activationCallback != null) {
            activationCallback.run();
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
