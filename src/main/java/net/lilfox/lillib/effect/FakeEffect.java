package net.lilfox.lillib.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Colors;

/**
 * Base class for fake status effects that serve as visual indicators.
 * <p>
 * Fake effects are client-side only effects that don't affect gameplay mechanics.
 * They are used to display visual indicators in the HUD when certain configurations
 * are enabled, without requiring custom HUD rendering code.
 * 
 * <p>These effects are automatically managed by the effect handler system and will
 * appear/disappear based on the bound configuration's state.
 * 
 * <p>Example usage:
 * <pre>{@code
 * public static final RegistryEntry<StatusEffect> ULTRA_HASTE = 
 *     EffectRegistry.register("mymod", "ultra_haste", new FakeEffect(), config);
 * }</pre>
 * 
 * @author lilfox
 * @since 1.0.0
 */
public class FakeEffect extends StatusEffect {
    /**
     * Creates a new fake effect with default visual properties.
     * <p>
     * The effect is marked as beneficial (positive) with a yellow color.
     * These visual properties can be overridden by custom textures.
     */
    public FakeEffect() {
        super(StatusEffectCategory.BENEFICIAL, Colors.YELLOW);
    }

    /**
     * Creates a new fake effect with custom color.
     * 
     * @param color The color to use for this effect (RGB format)
     */
    public FakeEffect(int color) {
        super(StatusEffectCategory.BENEFICIAL, color);
    }

    /**
     * Creates a new fake effect with custom category and color.
     * 
     * @param category The effect category (BENEFICIAL, HARMFUL, or NEUTRAL)
     * @param color The color to use for this effect (RGB format)
     */
    public FakeEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
}
