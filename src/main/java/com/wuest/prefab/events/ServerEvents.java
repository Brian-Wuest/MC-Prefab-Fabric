package com.wuest.prefab.events;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Utils;
import com.wuest.prefab.config.ModConfiguration;
import com.wuest.prefab.items.ItemSickle;
import com.wuest.prefab.registries.ModRegistries;
import com.wuest.prefab.structures.events.StructureEventHandler;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
        ServerEvents.serverStarting();

        ServerEvents.serverStarted();

        ServerEvents.playerJoinedServer();

        StructureEventHandler.registerStructureServerSideEvents();
    }

    private static void serverStarting() {
        ServerLifecycleEvents.SERVER_STARTING.register((server -> {
            // Only do this for server-side.
            ModRegistry.serverModRegistries = new ModRegistries();
        }));
    }

    private static void serverStarted() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            // Get the server configuration.
            // This will be pushed to the player when they join the world.
            ModConfiguration config = AutoConfig.getConfigHolder(ModConfiguration.class).getConfig();

            // Make sure the static mod configuration object is separate from the object loaded from the file system.
            // This way we don't have issues when players swap between servers and local worlds.
            CompoundTag tag = config.writeCompoundTag();
            Prefab.serverConfiguration = new ModConfiguration();
            Prefab.serverConfiguration.readFromTag(tag);

            // Do this when the server starts so that all appropriate tags are used.
            ItemSickle.setEffectiveBlocks();
        });
    }

    private static void playerJoinedServer() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (entity instanceof ServerPlayer) {
                // Send the message to the client.
                FriendlyByteBuf messagePacket = Utils.createMessageBuffer(Prefab.serverConfiguration.writeCompoundTag());
                ServerPlayNetworking.send((ServerPlayer) entity, ModRegistry.ConfigSync, messagePacket);
            }
        });
    }
}
