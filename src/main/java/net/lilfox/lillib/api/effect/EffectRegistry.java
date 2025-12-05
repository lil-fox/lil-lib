package net.lilfox.lillib.api.effect;

import net.lilfox.lillib.api.config.IConfigBoolean;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

/**
 * Public API for registering fake effects that serve as visual indicators for configurations.
 * <p>
 * Fake effects are client-side status effects that don't affect gameplay but provide
 * visual feedback in the HUD when certain configurations are enabled. This registry
 * acts as a wrapper around Minecraft's status effect registry.
 * 
 * <p>Example usage:
 * <pre>{@code
 * public class MyModEffects {
 *     public static RegistryEntry<StatusEffect> ULTRA_HASTE;
 *     
 *     public static void register() {
 *         ULTRA_HASTE = EffectRegistry.register(
 *             "mymod",
 *             "ultra_haste",
 *             new FakeEffect(),
 *             MyModConfigs.ultraHaste
 *         );
 *     }
 * }
 * }</pre>
 * 
 * @author lilfox
 * @since 1.0.0
 */
public final class EffectRegistry {
    private EffectRegistry() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Registers a fake effect and binds it to a boolean configuration.
     * <p>
     * This method:
     * <ul>
     *   <li>Registers the effect in Minecraft's status effect registry</li>
     *   <li>Adds it to the internal list of fake effects</li>
     *   <li>Binds it to the specified configuration</li>
     *   <li>Marks the configuration as having an effect</li>
     * </ul>
     * 
     * <p>The effect will automatically appear/disappear in the HUD based on
     * the configuration's state and the user's effect display preference.
     * 
     * @param modId The mod ID for namespacing the effect identifier
     * @param name The effect name (will be used as "modId:name")
     * @param effect The StatusEffect instance (typically a FakeEffect)
     * @param config The boolean configuration to bind this effect to
     * @return The registry entry for the registered effect
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if an effect with the same ID is already registered
     */
    public static RegistryEntry<StatusEffect> register(String modId, String name, 
                                                        StatusEffect effect, 
                                                        IConfigBoolean config) {
        return net.lilfox.lillib.effect.impl.EffectRegistryImpl.register(modId, name, effect, config);
    }

    /**
     * Gets all registered fake effects.
     * <p>
     * This list only includes effects registered through this API, not all status effects.
     * 
     * @return An unmodifiable list of all fake effects
     */
    public static List<RegistryEntry<StatusEffect>> getAllFakeEffects() {
        return net.lilfox.lillib.effect.impl.EffectRegistryImpl.getAllFakeEffects();
    }

    /**
     * Gets the configuration bound to a specific fake effect.
     * 
     * @param effect The effect registry entry
     * @return The bound configuration, or {@code null} if not found
     */
    public static IConfigBoolean getConfig(RegistryEntry<StatusEffect> effect) {
        return net.lilfox.lillib.effect.impl.EffectRegistryImpl.getConfig(effect);
    }

    /**
     * Checks if an effect is registered as a fake effect.
     * 
     * @param effect The effect registry entry to check
     * @return {@code true} if registered as a fake effect, {@code false} otherwise
     */
    public static boolean isFakeEffect(RegistryEntry<StatusEffect> effect) {
        return net.lilfox.lillib.effect.impl.EffectRegistryImpl.isFakeEffect(effect);
    }
}
