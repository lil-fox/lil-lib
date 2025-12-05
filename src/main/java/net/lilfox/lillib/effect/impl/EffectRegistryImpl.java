package net.lilfox.lillib.effect.impl;

import net.lilfox.lillib.api.config.IConfigBoolean;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal implementation of the effect registry.
 * <p>
 * This registry acts as a wrapper around Minecraft's vanilla status effect registry,
 * providing additional functionality for tracking fake effects and their bound configurations.
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class EffectRegistryImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger("lillib");

    // List of all registered fake effects
    private static final List<RegistryEntry<StatusEffect>> FAKE_EFFECTS = new ArrayList<>();
    
    // Map of effect -> bound configuration
    private static final Map<RegistryEntry<StatusEffect>, IConfigBoolean> EFFECT_CONFIGS = new ConcurrentHashMap<>();
    
    // Map of identifier -> effect (for lookup)
    private static final Map<Identifier, RegistryEntry<StatusEffect>> EFFECTS_BY_ID = new ConcurrentHashMap<>();

    /**
     * Registers a fake effect in the vanilla registry and binds it to a configuration.
     * <p>
     * This method:
     * <ul>
     *   <li>Registers the effect in {@link Registries#STATUS_EFFECT}</li>
     *   <li>Adds it to the internal list of fake effects</li>
     *   <li>Binds it to the provided configuration</li>
     *   <li>Marks the configuration as having an effect</li>
     * </ul>
     * 
     * @param modId The mod ID for namespacing
     * @param name The effect name
     * @param effect The StatusEffect instance
     * @param config The configuration to bind to
     * @return The registry entry for the effect
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if an effect with the same ID already exists
     */
    public static RegistryEntry<StatusEffect> register(String modId, String name, 
                                                        StatusEffect effect, 
                                                        IConfigBoolean config) {
        Objects.requireNonNull(modId, "modId cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(effect, "effect cannot be null");
        Objects.requireNonNull(config, "config cannot be null");

        Identifier id = Identifier.of(modId, name);
        
        // Check if already registered
        if (EFFECTS_BY_ID.containsKey(id)) {
            throw new IllegalArgumentException("Effect with ID '" + id + "' is already registered");
        }

        // Register in vanilla registry
        RegistryEntry<StatusEffect> entry = Registry.registerReference(Registries.STATUS_EFFECT, id, effect);
        
        // Add to our tracking
        FAKE_EFFECTS.add(entry);
        EFFECT_CONFIGS.put(entry, config);
        EFFECTS_BY_ID.put(id, entry);
        
        // Mark config as having an effect
        config.bindEffect();
        
        LOGGER.info("Registered fake effect '{}' bound to config '{}'", id, config.getName());
        
        return entry;
    }

    /**
     * Gets all registered fake effects.
     * 
     * @return Unmodifiable list of all fake effects
     */
    public static List<RegistryEntry<StatusEffect>> getAllFakeEffects() {
        return Collections.unmodifiableList(FAKE_EFFECTS);
    }

    /**
     * Gets the configuration bound to a specific effect.
     * 
     * @param effect The effect registry entry
     * @return The bound configuration, or null if not found
     */
    public static IConfigBoolean getConfig(RegistryEntry<StatusEffect> effect) {
        return EFFECT_CONFIGS.get(effect);
    }

    /**
     * Checks if an effect is registered as a fake effect.
     * 
     * @param effect The effect registry entry
     * @return true if registered as a fake effect
     */
    public static boolean isFakeEffect(RegistryEntry<StatusEffect> effect) {
        return FAKE_EFFECTS.contains(effect);
    }

    /**
     * Gets an effect by its identifier.
     * 
     * @param id The effect identifier
     * @return The effect registry entry, or null if not found
     */
    public static RegistryEntry<StatusEffect> getEffectById(Identifier id) {
        return EFFECTS_BY_ID.get(id);
    }

    /**
     * Gets all registered effect identifiers.
     * 
     * @return Set of all effect identifiers
     */
    public static Set<Identifier> getAllEffectIds() {
        return new HashSet<>(EFFECTS_BY_ID.keySet());
    }

    /**
     * Clears all registered effects.
     * <p>
     * Used for testing and cleanup. Does not unregister from vanilla registry.
     */
    public static void clear() {
        FAKE_EFFECTS.clear();
        EFFECT_CONFIGS.clear();
        EFFECTS_BY_ID.clear();
    }
}
