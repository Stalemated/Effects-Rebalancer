package com.mervyn.effectsrebalancer.mixin;

import com.mervyn.effectsrebalancer.config.SyncedConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffect.class)
public class RegenerationMixin {

    @ModifyArg(method = "applyUpdateEffect(Lnet/minecraft/entity/LivingEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;heal(F)V"), index = 0)
    public float modifyRegenAmount(float amount) {
        if ((Object) this == StatusEffects.REGENERATION && !SyncedConfig.enableMaxHealthRegen) {
            return SyncedConfig.regenerationAmount;
        }
        return amount;
    }

    @Inject(method = "canApplyUpdateEffect", at = @At("HEAD"), cancellable = true)
    public void normalizeRegenFrequency(int duration, int amplifier, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == StatusEffects.REGENERATION && SyncedConfig.enableMaxHealthRegen) {
            // Replace vanilla regen logic and heal every 10 ticks regardless of level
            cir.setReturnValue(duration % SyncedConfig.healingCooldownTicks == 0);
        }
    }

    @Inject(method = "applyUpdateEffect", at = @At("HEAD"), cancellable = true)
    public void applyCustomRegenHeal(LivingEntity entity, int amplifier, CallbackInfo ci) {
        if ((Object) this == StatusEffects.REGENERATION && SyncedConfig.enableMaxHealthRegen) {
            if (entity.getHealth() < entity.getMaxHealth()) {
                float maxHealth = entity.getMaxHealth();
                int level = amplifier + 1;

                // Custom regen formula
                float healAmount = maxHealth * SyncedConfig.regenerationMaxHealthPercentage * level * SyncedConfig.healingCooldownTicks / 20;

                entity.heal(healAmount);
            }
            ci.cancel();
        }
    }
}
