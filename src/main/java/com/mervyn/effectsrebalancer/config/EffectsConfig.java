package com.mervyn.effectsrebalancer.config;

import com.mervyn.effectsrebalancer.EffectsRebalancerMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class EffectsConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir()
            .resolve("effects-rebalancer.properties");

    // Default values
    public static double resistanceModifier = 0.20; // 20% per level
    public static float regenerationAmount = 1.0f; // 1 HP (0.5 hearts)
    public static int absorptionAmount = 4; // 4 Absorption points (2 hearts) per level

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            createConfig();
        } else {
            readConfig();
        }
    }

    private static void createConfig() {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(CONFIG_PATH))) {
            writer.print("""
                    # Effects Rebalancer Configuration
                    # --------------------------------

                    # Resistance Modifier: Damage reduction per level of Resistance.
                    # Vanilla default: 0.20 (20% reduction per level)
                    # Example: 0.10 would be 10% per level.
                    resistance_modifier=%s

                    # Regeneration Amount: Health points healed per activation.
                    # Vanilla default: 1.0 (0.5 hearts)
                    regeneration_amount=%s

                    # Absorption Amount: Absorption points added per level.
                    # Vanilla default: 4 (2 hearts per level)
                    absorption_amount=%d
                    """.formatted(resistanceModifier, regenerationAmount, absorptionAmount));

        } catch (IOException e) {
            EffectsRebalancerMod.LOGGER.error("Failed to write config", e);
        }
    }

    private static void readConfig() {
        Properties props = new Properties();
        try (InputStream stream = Files.newInputStream(CONFIG_PATH)) {
            props.load(stream);

            String resistanceStr = props.getProperty("resistance_modifier");
            if (resistanceStr != null) {
                try {
                    resistanceModifier = Double.parseDouble(resistanceStr);
                } catch (NumberFormatException e) {
                    EffectsRebalancerMod.LOGGER.warn("Invalid value for resistance_modifier: '{}', using default {}",
                            resistanceStr, resistanceModifier);
                }
            }

            String regStr = props.getProperty("regeneration_amount");
            if (regStr != null) {
                try {
                    regenerationAmount = Float.parseFloat(regStr);
                } catch (NumberFormatException e) {
                    EffectsRebalancerMod.LOGGER.warn("Invalid value for regeneration_amount: '{}', using default {}",
                            regStr, regenerationAmount);
                }
            }

            String absStr = props.getProperty("absorption_amount");
            if (absStr != null) {
                try {
                    absorptionAmount = Integer.parseInt(absStr);
                } catch (NumberFormatException e) {
                    EffectsRebalancerMod.LOGGER.warn("Invalid value for absorption_amount: '{}', using default {}",
                            absStr, absorptionAmount);
                }
            }

        } catch (IOException e) {
            EffectsRebalancerMod.LOGGER.error("Failed to read config", e);
        }
    }
}
