package com.mervyn.effectsrebalancer.mixin;

import com.mervyn.effectsrebalancer.config.SyncedConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
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
        if (effect.getTranslationKey().equals("effect.minecraft.resistance")) {
            return false;
        }
        return instance.hasStatusEffect(effect);
    }

    // Apply Custom Resistance Logic
    @ModifyVariable(method = "modifyAppliedDamage", at = @At("HEAD"), argsOnly = true)
    private float customResistance(float amount, DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Check standard bypass logic (bypasses magic usually bypasses resistance)
        if (!source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) {
            StatusEffectInstance resistanceEffect = null;
            for (StatusEffectInstance instanceEffect : entity.getStatusEffects()) {
                if (instanceEffect.getEffectType().getTranslationKey().equals("effect.minecraft.resistance")) {
                    resistanceEffect = instanceEffect;
                    break;
                }
            }
            
            if (resistanceEffect != null) {
                int amplifier = (resistanceEffect.getAmplifier() + 1);
                float reduction = Math.min((float) (amplifier * SyncedConfig.resistanceModifier), 1.0F);

                float newAmount = amount * (1.0F - reduction);
                return Math.max(newAmount, 0.0F);
            }
        }
        return amount;
    }
}

