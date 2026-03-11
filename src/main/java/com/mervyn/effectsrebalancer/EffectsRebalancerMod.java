package com.mervyn.effectsrebalancer;

import com.mervyn.effectsrebalancer.config.EffectsConfig;
import com.mervyn.effectsrebalancer.config.SyncedConfig;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.UUID;

public class EffectsRebalancerMod implements ModInitializer {
        public static final Logger LOGGER = LoggerFactory.getLogger("effects-rebalancer");

        public static final Identifier SYNC_CONFIG_PACKET_ID = new Identifier("effects-rebalancer", "sync_config");
        public static final Identifier UPDATE_CONFIG_PACKET_ID = new Identifier("effects-rebalancer", "update_config");

        // Track players pending a sync to ensure sending it on the very next tick after
        // JOIN
        private static final Set<UUID> pendingSyncPlayers = ConcurrentHashMap.newKeySet();

        @Override
        public void onInitialize() {
                LOGGER.info("Initializing Effects Rebalancer...");
                MidnightConfig.init("effects-rebalancer", EffectsConfig.class);
                SyncedConfig.reset();

                LOGGER.info("Loaded config: Resistance={}, Regen={}, Absorption={}",
                                EffectsConfig.resistanceModifier,
                                EffectsConfig.regenerationAmount,
                                EffectsConfig.absorptionAmount);

                // Queue players for sync upon joining
                ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> pendingSyncPlayers.add(handler.player.getUuid()));

                // Execute sync on the very next Server Tick after JOIN
                ServerTickEvents.END_SERVER_TICK.register(server -> {
                        if (!pendingSyncPlayers.isEmpty()) {
                                PacketByteBuf buf = createSyncPacket();
                                for (UUID uuid : pendingSyncPlayers) {
                                        ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                                        if (player != null) {
                                                ServerPlayNetworking.send(player, SYNC_CONFIG_PACKET_ID, buf);
                                        }
                                }
                                pendingSyncPlayers.clear();
                        }
                });

                // Register Global C2S Receiver for OP Config Updates
                ServerPlayNetworking.registerGlobalReceiver(UPDATE_CONFIG_PACKET_ID,
                                (server, player, handler, buf, responseSender) -> {
                                        // Read client intent first
                                        double proposedResistance = buf.readDouble();
                                        float proposedRegeneration = buf.readFloat();
                                        boolean proposedMaxHealthRegen = buf.readBoolean();
                                        float proposedRegenMaxHealthPercentage = buf.readFloat();
                                        int proposedCooldown = buf.readInt();
                                        int proposedAbsorption = buf.readInt();

                                        server.execute(() -> {
                                                // Must be OP level 2+
                                                if (player.hasPermissionLevel(2)) {
                                                        // Bounds validation & Silent Clamping
                                                        EffectsConfig.resistanceModifier = MathHelper
                                                                        .clamp(proposedResistance, 0.0, 1.0);
                                                        EffectsConfig.regenerationAmount = MathHelper
                                                                        .clamp(proposedRegeneration, 0.0f, 20.0f);
                                                        EffectsConfig.enableMaxHealthRegen = proposedMaxHealthRegen;
                                                        EffectsConfig.regenerationMaxHealthPercentage = MathHelper
                                                                        .clamp(proposedRegenMaxHealthPercentage, 0.0f, 1.0f);
                                                        EffectsConfig.healingCooldownTicks = MathHelper
                                                                        .clamp(proposedCooldown, 1, 1200);
                                                        EffectsConfig.absorptionAmount = MathHelper
                                                                        .clamp(proposedAbsorption, 0, 100);

                                                        // Write to local server disk first via MidnightConfig save
                                                        // mechanism
                                                        MidnightConfig.write("effects-rebalancer");

                                                        // Update server's active proxy
                                                        SyncedConfig.reset();

                                                        LOGGER.info("OP {} updated and saved server config. Broadcasting synced values.",
                                                                        player.getName().getString());

                                                        // Broadcast clamped values to everyone so their proxies match
                                                        // what was actually saved
                                                        PacketByteBuf broadcastBuf = createSyncPacket();
                                                        for (ServerPlayerEntity p : server.getPlayerManager()
                                                                        .getPlayerList()) {
                                                                ServerPlayNetworking.send(p, SYNC_CONFIG_PACKET_ID,
                                                                                broadcastBuf);
                                                        }
                                                } else {
                                                        LOGGER.warn("Player {} attempted to update config without sufficient permissions.",
                                                                        player.getName().getString());
                                                }
                                        });
                                });
        }

        private PacketByteBuf createSyncPacket() {
                PacketByteBuf buf = PacketByteBufs.create();
                // Write fields in explicit order: double, float, int
                buf.writeDouble(EffectsConfig.resistanceModifier);
                buf.writeFloat(EffectsConfig.regenerationAmount);
                buf.writeBoolean(EffectsConfig.enableMaxHealthRegen);
                buf.writeFloat(EffectsConfig.regenerationMaxHealthPercentage);
                buf.writeInt(EffectsConfig.healingCooldownTicks);
                buf.writeInt(EffectsConfig.absorptionAmount);
                return buf;
        }
}
