package net.lilfox.lillib.mixin;

import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElytraFeatureRenderer.class)
public class TElytra {
//
//    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
//    private static void replaceTexture(BipedEntityRenderState state, CallbackInfoReturnable<Identifier> cir) {
//        if (state instanceof PlayerEntityRenderState playerState) {
//
//            if ("lil_fox".equals(playerState.displayName.getString())) {
//                cir.setReturnValue(Identifier.of("lillib", "textures/entity/cape/tfox.png"));
//            }
//        }
//    }
}
