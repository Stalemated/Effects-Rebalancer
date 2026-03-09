package com.mervyn.effectsrebalancer.mixin;

import com.mervyn.effectsrebalancer.config.SyncedConfig;
import net.minecraft.entity.effect.AbsorptionStatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbsorptionStatusEffect.class)
public class AbsorptionMixin {

    @ModifyConstant(method = "onApplied", constant = @Constant(intValue = 4))
    private int modifyAbsorptionApplied(int constant) {
        return SyncedConfig.absorptionAmount;
    }

    @ModifyConstant(method = "removed", constant = @Constant(intValue = 4))
    private int modifyAbsorptionRemoved(int constant) {
        return SyncedConfig.absorptionAmount;
    }
}
