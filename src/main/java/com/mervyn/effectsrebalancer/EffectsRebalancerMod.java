package com.mervyn.effectsrebalancer;

import com.mervyn.effectsrebalancer.config.EffectsConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EffectsRebalancerMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("effects-rebalancer");

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Effects Rebalancer...");
        EffectsConfig.load();
        LOGGER.info("Loaded config: Resistance={}, Regen={}, Absorption={}",
                EffectsConfig.resistanceModifier,
                EffectsConfig.regenerationAmount,
                EffectsConfig.absorptionAmount);
    }
}
