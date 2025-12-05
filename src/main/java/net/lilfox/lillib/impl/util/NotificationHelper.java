package net.lilfox.lillib.impl.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Helper class for displaying notifications above the hotbar.
 * <p>
 * Uses Minecraft's vanilla overlay message system to show temporary
 * notifications without cluttering the chat.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class NotificationHelper {

    /**
     * Sends a notification about a boolean config change.
     * <p>
     * Displays: "[Nice Name]: TRUE" (green) or "[Nice Name]: FALSE" (red)
     * 
     * @param niceName The nice name of the config
     * @param value The new value
     */
    public static void notifyBooleanChange(String niceName, boolean value) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        Text message = Text.literal("[" + niceName + "]: ")
            .append(Text.literal(value ? "TRUE" : "FALSE")
                .formatted(value ? Formatting.GREEN : Formatting.RED));

        // Send as overlay message (shows above hotbar)
        client.player.sendMessage(message, true);
    }

    /**
     * Sends a notification about an integer config change.
     * <p>
     * Displays: "[Nice Name]: 42" (white)
     * 
     * @param niceName The nice name of the config
     * @param value The new value
     */
    public static void notifyIntegerChange(String niceName, int value) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        Text message = Text.literal("[" + niceName + "]: ")
            .append(Text.literal(String.valueOf(value)).formatted(Formatting.WHITE));

        client.player.sendMessage(message, true);
    }

    /**
     * Sends a notification about a double config change.
     * <p>
     * Displays: "[Nice Name]: 3.14" (white)
     * 
     * @param niceName The nice name of the config
     * @param value The new value
     */
    public static void notifyDoubleChange(String niceName, double value) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        Text message = Text.literal("[" + niceName + "]: ")
            .append(Text.literal(String.format("%.2f", value)).formatted(Formatting.WHITE));

        client.player.sendMessage(message, true);
    }

    /**
     * Sends a notification about a string config change.
     * <p>
     * Displays: "[Nice Name]: newValue" (white)
     * 
     * @param niceName The nice name of the config
     * @param value The new value
     */
    public static void notifyStringChange(String niceName, String value) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        Text message = Text.literal("[" + niceName + "]: ")
            .append(Text.literal(value).formatted(Formatting.WHITE));

        client.player.sendMessage(message, true);
    }

    /**
     * Sends a custom notification message.
     * <p>
     * Displays above the hotbar with specified formatting.
     * 
     * @param message The message to display
     * @param formatting The text formatting to apply
     */
    public static void notify(String message, Formatting formatting) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        client.player.sendMessage(Text.literal(message).formatted(formatting), true);
    }

    /**
     * Sends a custom notification message with default white formatting.
     * 
     * @param message The message to display
     */
    public static void notify(String message) {
        notify(message, Formatting.WHITE);
    }
}
