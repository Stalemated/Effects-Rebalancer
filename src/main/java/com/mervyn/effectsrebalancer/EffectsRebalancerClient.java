package com.mervyn.effectsrebalancer;

import com.mervyn.effectsrebalancer.config.EffectsConfig;
import com.mervyn.effectsrebalancer.config.SyncedConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public class EffectsRebalancerClient implements ClientModInitializer {

    // Store the last sent values to avoid spamming the server if the player didn't
    // change anything
    private double lastSentResistance = EffectsConfig.resistanceModifier;
    private float lastSentRegeneration = EffectsConfig.regenerationAmount;
    private int lastSentAbsorption = EffectsConfig.absorptionAmount;

    @Override
    public void onInitializeClient() {
        // Fallback: Restore local config values when disconnecting from a server
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            SyncedConfig.reset();
            EffectsRebalancerMod.LOGGER.info("Disconnected from server. Restored local config values.");
        });

        // Register Server-to-Client packet receiver for config syncing
        ClientPlayNetworking.registerGlobalReceiver(EffectsRebalancerMod.SYNC_CONFIG_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    // Read fields in explicit order: double, float, int
                    double syncedResistance = buf.readDouble();
                    float syncedRegeneration = buf.readFloat();
                    int syncedAbsorption = buf.readInt();

                    client.execute(() -> {
                        // Apply ONLY to the in-memory SyncedConfig proxy, leaving the local disk
                        // untouched
                        SyncedConfig.resistanceModifier = syncedResistance;
                        SyncedConfig.regenerationAmount = syncedRegeneration;
                        SyncedConfig.absorptionAmount = syncedAbsorption;
                        EffectsRebalancerMod.LOGGER.info(
                                "Config synced from server: Resistance={}, Regen={}, Absorption={}",
                                syncedResistance, syncedRegeneration, syncedAbsorption);
                    });
                });

        // Detect config changes while the screen is open, gating polling to avoid
        // performance hits
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen != null && client.currentScreen.getClass().getName().contains("MidnightConfig")) {

                // If any value changed compared to what we last sent, and we're currently on a
                // server (networkHandler != null)
                boolean changed = (lastSentResistance != EffectsConfig.resistanceModifier) ||
                        (lastSentRegeneration != EffectsConfig.regenerationAmount) ||
                        (lastSentAbsorption != EffectsConfig.absorptionAmount);

                // We also only want to send this if we are actively connected to a server.
                if (changed && ClientPlayNetworking.canSend(EffectsRebalancerMod.UPDATE_CONFIG_PACKET_ID)) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeDouble(EffectsConfig.resistanceModifier);
                    buf.writeFloat(EffectsConfig.regenerationAmount);
                    buf.writeInt(EffectsConfig.absorptionAmount);

                    ClientPlayNetworking.send(EffectsRebalancerMod.UPDATE_CONFIG_PACKET_ID, buf);
                    EffectsRebalancerMod.LOGGER.info("Detected local config change. Sent C2S update packet to server.");

                    // Update tracked variables to avoid resending the same changed values multiple
                    // times
                    lastSentResistance = EffectsConfig.resistanceModifier;
                    lastSentRegeneration = EffectsConfig.regenerationAmount;
                    lastSentAbsorption = EffectsConfig.absorptionAmount;
                }
            }
        });
    }
}
