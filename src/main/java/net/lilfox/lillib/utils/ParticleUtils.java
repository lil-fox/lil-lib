package net.lilfox.lillib.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

import java.util.HashMap;
import java.util.Map;

public class ParticleUtils {

    private static final Map<String, ParticleEffect> PARTICLE_KEYS = HashMap.newHashMap(1);

    public static void init() {
        PARTICLE_KEYS.put("enchant", ParticleTypes.ENCHANT);
        PARTICLE_KEYS.put("cherry", ParticleTypes.CHERRY_LEAVES);
    }

    public static void addParticleSet(String particleKey,
                                      LivingEntity target,
                                      int count, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        for (int i = 0; i < count; i++) {
            target.getWorld().addParticle(PARTICLE_KEYS.get(particleKey),
                    target.getParticleX(x), target.getRandomBodyY() + y, target.getParticleZ(z), velocityX, velocityY, velocityZ);
        }
    }
}
