package com.mervyn.effectsrebalancer.mixin;

import com.mervyn.effectsrebalancer.config.EffectsConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class ResistanceMixin {

    // Disable Vanilla Resistance Logic by pretending we don't have the effect
    @Redirect(method = "modifyAppliedDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
    private boolean disableVanillaResistance(LivingEntity instance, StatusEffect effect) {
        if (effect == StatusEffects.RESISTANCE) {
            return false;
        }
        return instance.hasStatusEffect(effect);
    }

    // Apply Custom Resistance Logic
    @ModifyVariable(method = "modifyAppliedDamage", at = @At("HEAD"), argsOnly = true)
    private float customResistance(float amount, DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Check standard bypass logic (bypasses magic usually bypasses resistance)
        if (!source.isIn(DamageTypeTags.BYPASSES_RESISTANCE) && entity.hasStatusEffect(StatusEffects.RESISTANCE)) {
            var effect = entity.getStatusEffect(StatusEffects.RESISTANCE);
            if (effect == null)
                return amount;
            int amplifier = (effect.getAmplifier() + 1);
            float reduction = Math.min((float) (amplifier * EffectsConfig.resistanceModifier), 1.0F);

            float newAmount = amount * (1.0F - reduction);
            return Math.max(newAmount, 0.0F);
        }
        return amount;
    }
}
