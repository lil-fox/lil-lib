package net.lilfox.lillib.effect.impl;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.lilfox.lillib.api.config.IConfigBoolean;
import net.lilfox.lillib.effect.impl.EffectRegistryImpl;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles automatic application and removal of fake effects based on configuration state.
 * <p>
 * This handler runs every client tick and synchronizes the player's active fake effects
 * with their bound configuration states.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class EffectHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("lillib");
    private static boolean initialized = false;

    /**
     * Registers the effect handler with the client tick event.
     * <p>
     * Should be called during mod initialization.
     */
    public static void register() {
        if (initialized) {
            LOGGER.warn("EffectHandler already initialized");
            return;
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                updateEffects(client.player);
            }
        });

        initialized = true;
        LOGGER.info("EffectHandler registered");
    }

    /**
     * Updates all fake effects for a player based on their configuration states.
     * <p>
     * This method is called every tick and performs the following logic for each fake effect:
     * <ul>
     *   <li>If config is enabled AND showEffect is true AND effect not rendered → Add effect</li>
     *   <li>If config is disabled OR showEffect is false AND effect is rendered → Remove effect</li>
     *   <li>Otherwise → No change</li>
     * </ul>
     * 
     * @param player The client player entity
     */
    private static void updateEffects(ClientPlayerEntity player) {
        for (RegistryEntry<StatusEffect> effectEntry : EffectRegistryImpl.getAllFakeEffects()) {
            IConfigBoolean config = EffectRegistryImpl.getConfig(effectEntry);
            
            if (config == null) {
                continue;
            }

            boolean shouldShow = config.shouldShowEffect();
            boolean isCurrentlyActive = player.hasStatusEffect(effectEntry);

            if (shouldShow && !isCurrentlyActive) {
                // Add the effect
                addEffect(player, effectEntry);
            } else if (!shouldShow && isCurrentlyActive) {
                // Remove the effect
                removeEffect(player, effectEntry);
            }
        }
    }

    /**
     * Adds a fake effect to the player.
     * <p>
     * The effect is added with infinite duration (-1), no amplifier (0),
     * and all display flags enabled.
     * 
     * @param player The player
     * @param effectEntry The effect to add
     */
    private static void addEffect(ClientPlayerEntity player, RegistryEntry<StatusEffect> effectEntry) {
        try {
            StatusEffectInstance instance = new StatusEffectInstance(
                effectEntry,
                -1,        // Infinite duration
                0,         // No amplifier
                true,      // Ambient
                true,      // Show particles
                true       // Show icon
            );
            
            player.addStatusEffect(instance);
        } catch (Exception e) {
            LOGGER.error("Failed to add fake effect", e);
        }
    }

    /**
     * Removes a fake effect from the player.
     * <p>
     * Uses the internal removal method to avoid triggering side effects.
     * 
     * @param player The player
     * @param effectEntry The effect to remove
     */
    private static void removeEffect(ClientPlayerEntity player, RegistryEntry<StatusEffect> effectEntry) {
        try {
            player.removeStatusEffectInternal(effectEntry);
        } catch (Exception e) {
            LOGGER.error("Failed to remove fake effect", e);
        }
    }

    /**
     * Forces an update of all effects immediately.
     * <p>
     * Used when configs are loaded or changed outside of normal tick cycle.
     * 
     * @param player The player to update effects for
     */
    public static void forceUpdate(ClientPlayerEntity player) {
        if (player != null) {
            updateEffects(player);
        }
    }

    /**
     * Checks if the handler has been initialized.
     * 
     * @return true if initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
}
