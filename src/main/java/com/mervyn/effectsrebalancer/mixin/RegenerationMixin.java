package com.mervyn.effectsrebalancer.mixin;

import com.mervyn.effectsrebalancer.config.EffectsConfig;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Use targets because the class might be package-private or hard to import
@Mixin(targets = "net.minecraft.entity.effect.RegenerationStatusEffect")
public class RegenerationMixin {

    // Target the applyUpdateEffect method
    // In Yarn 1.20.1: applyUpdateEffect(LivingEntity entity, int amplifier)
    @Redirect(method = "applyUpdateEffect(Lnet/minecraft/entity/LivingEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;heal(F)V"))
    public void modifyRegenAmount(LivingEntity instance, float amount) {
        // Use our configured amount instead of the hardcoded 1.0F
        instance.heal(EffectsConfig.regenerationAmount);
    }
}
