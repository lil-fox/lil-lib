package net.lilfox.lillib.impl.hotkey;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a sequence of keys that must be pressed in order.
 * <p>
 * This class contains code adapted from malilib by maruohon.
 * Original source: https://github.com/sakura-ryoko/malilib
 * Licensed under the GNU Lesser General Public License v3.0
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class HotkeySequence {
    private final List<Integer> keyCodes;
    private final String stringRepresentation;

    /**
     * Creates a hotkey sequence from a comma-separated string.
     * 
     * @param hotkeyString The hotkey string (e.g., "CTRL,N,M")
     */
    public HotkeySequence(String hotkeyString) {
        this.keyCodes = new ArrayList<>();
        this.stringRepresentation = hotkeyString;
        
        if (hotkeyString != null && !hotkeyString.isEmpty()) {
            parseHotkeyString(hotkeyString);
        }
    }

    /**
     * Creates a hotkey sequence from a list of key codes.
     * 
     * @param keyCodes The list of GLFW key codes
     */
    public HotkeySequence(List<Integer> keyCodes) {
        this.keyCodes = new ArrayList<>(keyCodes);
        this.stringRepresentation = buildStringRepresentation(keyCodes);
    }

    /**
     * Parses a hotkey string into key codes.
     * 
     * @param hotkeyString The string to parse
     */
    private void parseHotkeyString(String hotkeyString) {
        String[] parts = hotkeyString.split(",");
        
        for (String part : parts) {
            String trimmed = part.trim().toUpperCase();
            int keyCode = getKeyCodeFromName(trimmed);
            
            if (keyCode != GLFW.GLFW_KEY_UNKNOWN) {
                keyCodes.add(keyCode);
            }
        }
    }

    /**
     * Builds a string representation from key codes.
     * 
     * @param keyCodes The key codes
     * @return The string representation
     */
    private String buildStringRepresentation(List<Integer> keyCodes) {
        if (keyCodes.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyCodes.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(getKeyName(keyCodes.get(i)));
        }
        
        return sb.toString();
    }

    /**
     * Converts a key name to GLFW key code.
     * 
     * @param keyName The key name
     * @return The GLFW key code, or GLFW_KEY_UNKNOWN if not found
     */
    private int getKeyCodeFromName(String keyName) {
        // Handle special keys
        switch (keyName) {
            case "CTRL": case "CONTROL": return GLFW.GLFW_KEY_LEFT_CONTROL;
            case "SHIFT": return GLFW.GLFW_KEY_LEFT_SHIFT;
            case "ALT": return GLFW.GLFW_KEY_LEFT_ALT;
            case "SUPER": return GLFW.GLFW_KEY_LEFT_SUPER;
            case "SPACE": return GLFW.GLFW_KEY_SPACE;
            case "ENTER": return GLFW.GLFW_KEY_ENTER;
            case "TAB": return GLFW.GLFW_KEY_TAB;
            case "ESCAPE": case "ESC": return GLFW.GLFW_KEY_ESCAPE;
            case "BACKSPACE": return GLFW.GLFW_KEY_BACKSPACE;
            case "DELETE": return GLFW.GLFW_KEY_DELETE;
            case "INSERT": return GLFW.GLFW_KEY_INSERT;
            case "HOME": return GLFW.GLFW_KEY_HOME;
            case "END": return GLFW.GLFW_KEY_END;
            case "PAGE_UP": return GLFW.GLFW_KEY_PAGE_UP;
            case "PAGE_DOWN": return GLFW.GLFW_KEY_PAGE_DOWN;
            case "UP": return GLFW.GLFW_KEY_UP;
            case "DOWN": return GLFW.GLFW_KEY_DOWN;
            case "LEFT": return GLFW.GLFW_KEY_LEFT;
            case "RIGHT": return GLFW.GLFW_KEY_RIGHT;
        }
        
        // Handle F-keys
        if (keyName.startsWith("F") && keyName.length() > 1) {
            try {
                int fNum = Integer.parseInt(keyName.substring(1));
                if (fNum >= 1 && fNum <= 25) {
                    return GLFW.GLFW_KEY_F1 + (fNum - 1);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        
        // Handle single character keys
        if (keyName.length() == 1) {
            char c = keyName.charAt(0);
            if (c >= 'A' && c <= 'Z') {
                return GLFW.GLFW_KEY_A + (c - 'A');
            }
            if (c >= '0' && c <= '9') {
                return GLFW.GLFW_KEY_0 + (c - '0');
            }
        }
        
        return GLFW.GLFW_KEY_UNKNOWN;
    }

    /**
     * Gets the display name for a key code.
     * 
     * @param keyCode The GLFW key code
     * @return The key name
     */
    private String getKeyName(int keyCode) {
        // Handle special keys
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            return "CTRL";
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            return "SHIFT";
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
            return "ALT";
        }
        if (keyCode == GLFW.GLFW_KEY_SPACE) return "SPACE";
        if (keyCode == GLFW.GLFW_KEY_ENTER) return "ENTER";
        if (keyCode == GLFW.GLFW_KEY_TAB) return "TAB";
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) return "ESC";
        
        // Handle F-keys
        if (keyCode >= GLFW.GLFW_KEY_F1 && keyCode <= GLFW.GLFW_KEY_F25) {
            return "F" + (keyCode - GLFW.GLFW_KEY_F1 + 1);
        }
        
        // Handle letter keys
        if (keyCode >= GLFW.GLFW_KEY_A && keyCode <= GLFW.GLFW_KEY_Z) {
            return String.valueOf((char) ('A' + (keyCode - GLFW.GLFW_KEY_A)));
        }
        
        // Handle number keys
        if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
            return String.valueOf((char) ('0' + (keyCode - GLFW.GLFW_KEY_0)));
        }
        
        return "UNKNOWN";
    }

    /**
     * Gets the list of key codes in this sequence.
     * 
     * @return The key codes
     */
    public List<Integer> getKeyCodes() {
        return new ArrayList<>(keyCodes);
    }

    /**
     * Gets the number of keys in this sequence.
     * 
     * @return The sequence length
     */
    public int size() {
        return keyCodes.size();
    }

    /**
     * Checks if this sequence is empty.
     * 
     * @return true if empty
     */
    public boolean isEmpty() {
        return keyCodes.isEmpty();
    }

    /**
     * Gets the string representation of this hotkey.
     * 
     * @return The hotkey string
     */
    public String getStringRepresentation() {
        return stringRepresentation;
    }

    /**
     * Checks if this sequence matches exactly with another.
     * 
     * @param other The other sequence
     * @return true if sequences are identical
     */
    public boolean matches(HotkeySequence other) {
        if (other == null || this.keyCodes.size() != other.keyCodes.size()) {
            return false;
        }
        
        for (int i = 0; i < keyCodes.size(); i++) {
            if (!keyCodes.get(i).equals(other.keyCodes.get(i))) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Checks if the given pressed keys match this sequence in order.
     * 
     * @param pressedKeys The currently pressed keys in order
     * @return true if the sequence matches
     */
    public boolean isTriggeredBy(List<Integer> pressedKeys) {
        if (pressedKeys.size() < keyCodes.size()) {
            return false;
        }
        
        // Check if the last N pressed keys match our sequence
        int startIndex = pressedKeys.size() - keyCodes.size();
        for (int i = 0; i < keyCodes.size(); i++) {
            if (!keyCodes.get(i).equals(pressedKeys.get(startIndex + i))) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotkeySequence that = (HotkeySequence) o;
        return Objects.equals(keyCodes, that.keyCodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyCodes);
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }
}
