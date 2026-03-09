package com.mervyn.effectsrebalancer.config;

public class SyncedConfig {
    // Volatile to ensure thread safety when mixins read off main thread while
    // server updates it
    public static volatile double resistanceModifier = EffectsConfig.resistanceModifier;
    public static volatile float regenerationAmount = EffectsConfig.regenerationAmount;
    public static volatile int absorptionAmount = EffectsConfig.absorptionAmount;

    // Copies the standard local disk values into the active synced configuration
    public static void reset() {
        resistanceModifier = EffectsConfig.resistanceModifier;
        regenerationAmount = EffectsConfig.regenerationAmount;
        absorptionAmount = EffectsConfig.absorptionAmount;
    }
}
