package net.lilfox.lillib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CapeFeatureRenderer.class)
public class TCape {

    @Redirect(method = "render*", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/SkinTextures;cape()Lnet/minecraft/util/AssetInfo$TextureAsset;"
    ))
    private AssetInfo.TextureAsset redirectCapeTexture(SkinTextures skinTextures, @Local(argsOnly = true) PlayerEntityRenderState state) {
        if(state.displayName != null && state.displayName.getString().contains("\uD83D\uDC95lil_fox\uD83D\uDC95")) {
            if (skinTextures.cape() == null) {
                return new AssetInfo.TextureAsset() {
                    @Override
                    public Identifier texturePath() {
                        return Identifier.of("lillib", "textures/entity/cape/tfox.png");
                    }

                    @Override
                    public Identifier id() {
                        return Identifier.of("lillib", "textures/entity/cape/tfox.png");
                    }
                };
            }
        }
        return skinTextures.cape();
    }
}
