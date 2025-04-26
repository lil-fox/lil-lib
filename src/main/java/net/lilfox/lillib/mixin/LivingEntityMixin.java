package net.lilfox.lillib.mixin;

import net.lilfox.lillib.utils.ParticleUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "tickStatusEffects", at = @At("TAIL"))
    private void addFakeParticle(CallbackInfo ci) {
        if (((LivingEntity) (Object) this) instanceof PlayerEntity) {
            if (((PlayerEntity) (Object) this).getName().getString().equals("lil_fox")) {

                    if (!((PlayerEntity) (Object) this).isInvisible()) {

                        ((LivingEntity) (Object) this).getWorld().addParticleClient(ParticleTypes.CHERRY_LEAVES, ((LivingEntity) (Object) this).getParticleX(5), ((LivingEntity) (Object) this).getRandomBodyY() + 1, ((LivingEntity) (Object) this).getParticleZ(5), 1.0, 1.0, 1.0);
                        ((LivingEntity) (Object) this).getWorld().addParticleClient(ParticleTypes.CHERRY_LEAVES, ((LivingEntity) (Object) this).getParticleX(3), ((LivingEntity) (Object) this).getRandomBodyY(), ((LivingEntity) (Object) this).getParticleZ(3), -1.0, -1.0, -1.0);
                    }else if (!((PlayerEntity) (Object) this).isSneaking()) {
                        ParticleUtils.addParticleSet("cherry",
                                ((PlayerEntity) (Object) this), 16, 0.5, 0 , 0.5 , 0, 0 ,0);


                    }

            }
        }
    }
}