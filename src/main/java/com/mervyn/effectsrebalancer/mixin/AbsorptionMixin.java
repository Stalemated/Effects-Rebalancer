package com.mervyn.effectsrebalancer.mixin;

import com.mervyn.effectsrebalancer.config.EffectsConfig;
import net.minecraft.entity.effect.AbsorptionStatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbsorptionStatusEffect.class)
public class AbsorptionMixin {

    @ModifyConstant(method = "onApplied", constant = @Constant(intValue = 4))
    private int modifyAbsorptionApplied(int constant) {
        return EffectsConfig.absorptionAmount;
    }

    @ModifyConstant(method = "onRemoved", constant = @Constant(intValue = 4))
    private int modifyAbsorptionRemoved(int constant) {
        return EffectsConfig.absorptionAmount;
    }
}
