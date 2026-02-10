# Effects Rebalancer 🛡️

A lightweight, configuration-driven Fabric mod that allows you to fine-tune Minecraft's core status effects. Perfect for modpack makers who want to rebalance the strength of Resistance, Regeneration, and Absorption.

## ✨ Features

-   **Custom Absorption scaling**: Change how many extra hearts (Absorption points) each level provides.
-   **Configurable Resistance**: Fine-tune damage reduction percentages per level.
-   **Adjustable Regeneration**: Control the exact amount of health points restored per tick.
-   **Lightweight & Efficient**: Uses optimized Mixins to modify vanilla behavior without overhead.

## ⚙️ Configuration

Upon first run, the mod generates a configuration file at `.minecraft/config/effects-rebalancer.properties`. You can modify the following values:

| Property | Default | Description |
| :--- | :--- | :--- |
| `resistance_modifier` | `0.20` | Damage reduction per level (0.20 = 20%). |
| `regeneration_amount` | `1.0` | Health points healed per activation (1.0 = 0.5 hearts). |
| `absorption_amount` | `4` | Absorption points added per level (4 = 2 hearts). |

## 🛠️ Installation

1. Make sure you have the [Fabric Loader](https://fabricmc.net/) installed for Minecraft 1.20.1.
2. Drop the `effects-rebalancer-1.0.jar` into your `mods` folder.
3. (Optional) Install [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) for maximum compatibility.

## 📜 License
This mod is available under the MIT License. Feel free to include it in any modpacks!
