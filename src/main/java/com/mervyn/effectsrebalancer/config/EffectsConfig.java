package com.mervyn.effectsrebalancer.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class EffectsConfig extends MidnightConfig {
    @Entry
    public static double resistanceModifier = 0.20; // 20% reduction per level

    @Entry
    public static float regenerationAmount = 1.0f; // 1 HP (0.5 hearts)

    @Entry(min = 0, max = 100)
    public static int absorptionAmount = 4; // 4 points (2 hearts) per level
}
