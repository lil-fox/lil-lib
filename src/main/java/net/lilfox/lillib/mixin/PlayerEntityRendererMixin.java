package net.lilfox.lillib.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)


public class PlayerEntityRendererMixin<AvatarlikeEntity extends PlayerLikeEntity & ClientPlayerLikeEntity> {

    @Unique
    private int colotTimer = 500;

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
    at=@At("TAIL"))
    private void customNameColor(AvatarlikeEntity playerLikeEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci){
        //System.out.println("LABEL");
        if(playerLikeEntity instanceof AbstractClientPlayerEntity player
        && "lil_fox".equals(player.getGameProfile().name())){
            //System.out.println("PLAYER");
            Text original = playerEntityRenderState.displayName;
            if(original != null){

                int custom = colotTimer < 256 ? 16552697 : Formatting.GOLD.getColorValue().intValue();
                colotTimer = colotTimer == 0 ? 500 : colotTimer - 1;

                playerEntityRenderState.displayName = (Text.literal("\uD83D\uDC95").styled(style -> style.withColor(custom))
                        .append(original
                                .copy().
                                styled(style -> style
                                        .withBold(true).withColor(16552697)))
                                        //.withObfuscated(true)))
                        .append("\uD83D\uDC95").styled(style -> style.withColor(custom)));
                //playerEntityRenderState.playerName = Text.literal("lil_fox").styled(style -> style.withColor(16552697).withObfuscated(true));
            }
        }

        if(playerLikeEntity instanceof AbstractClientPlayerEntity player
                && "fantom".equals(player.getGameProfile().name().toLowerCase())){
            Text original = playerEntityRenderState.displayName;
            if(original != null){
                playerEntityRenderState.displayName = original.copy().styled(style -> style.withObfuscated(true));
            }
        }
    }
}
