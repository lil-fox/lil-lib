package net.lilfox.lillib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.lilfox.lillib.effect.FakeEffect;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Mixin to apply custom background frame to fake effects in the HUD.
 * <p>
 * This mixin intercepts the rendering of status effect icons and replaces
 * the background texture for fake effects with a custom one, allowing players
 * to visually distinguish fake effects from real gameplay effects.
 * 
 * @author lilfox
 * @since 1.0.0
 */
@Mixin(InGameHud.class)
public class FakeEffectFrameMixin {

    /**
     * Modifies the sprite identifier for status effect backgrounds.
     * <p>
     * When a fake effect is being rendered, this replaces the default background
     * sprite with a custom one from lillib.
     * 
     * @param sprite The original sprite identifier
     * @param statusEffectInstance The status effect instance being rendered
     * @return The modified sprite identifier for fake effects, or original for real effects
     */
    @ModifyArg(
        method = "renderStatusEffectOverlay",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"
        ),
        index = 1
    )
    private Identifier modifyEffectBackground(Identifier sprite, @Local StatusEffectInstance statusEffectInstance) {
        // Get the status effect from the instance
        StatusEffect effect = Registries.STATUS_EFFECT.get(
            statusEffectInstance.getEffectType().getKey().orElseThrow()
        );

        // Check if it's a fake effect
        if (effect instanceof FakeEffect) {
            // Return custom background sprite for fake effects
            return Identifier.of("lillib", "hud/effect_background_fake");
        }

        // Return original sprite for real effects
        return sprite;
    }
}
