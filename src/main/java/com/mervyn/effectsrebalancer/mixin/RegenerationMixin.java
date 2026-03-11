package com.mervyn.effectsrebalancer.mixin;

import com.mervyn.effectsrebalancer.config.SyncedConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StatusEffect.class)
public class RegenerationMixin {

    @Redirect(method = "applyUpdateEffect(Lnet/minecraft/entity/LivingEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;heal(F)V"))
    public void modifyRegenAmount(LivingEntity instance, float amount) {
        if (((StatusEffect) (Object) this).getTranslationKey().equals("effect.minecraft.regeneration")) {
            instance.heal(SyncedConfig.regenerationAmount);
        } else {
            instance.heal(amount);
        }
    }
}
