package com.wuest.prefab.events;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Utils;
import com.wuest.prefab.config.ModConfiguration;
import com.wuest.prefab.items.ItemSickle;
import com.wuest.prefab.structures.events.StructureEventHandler;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public class ServerEvents {
    /**
     * Determines the affected blocks by redstone power.
     */
    public static ArrayList<BlockPos> RedstoneAffectedBlockPositions = new ArrayList<>();

    static {
        ServerEvents.RedstoneAffectedBlockPositions = new ArrayList<>();
    }

    public static void registerServerEvents() {
        ServerEvents.serverStarted();

        ServerEvents.playerJoinedServer();

        StructureEventHandler.registerStructureServerSideEvents();
    }

    private static void serverStarted() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            // Get the server configuration.
            // This will be pushed to the player when they join the world.
            Prefab.serverConfiguration = AutoConfig.getConfigHolder(ModConfiguration.class).getConfig();

            // Do this when the server starts so that all appropriate tags are used.
            ItemSickle.setEffectiveBlocks();
        });
    }

    private static void playerJoinedServer() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (entity instanceof ServerPlayer) {
                // Send the message to the client.
                FriendlyByteBuf messagePacket = Utils.createMessageBuffer(Prefab.serverConfiguration.writeCompoundTag());

                ServerSidePacketRegistry.INSTANCE.sendToPlayer((ServerPlayer) entity, ModRegistry.ConfigSync, messagePacket);
            }
        });
    }
}
