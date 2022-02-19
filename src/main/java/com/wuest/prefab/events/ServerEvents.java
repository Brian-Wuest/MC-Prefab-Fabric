package com.wuest.prefab.events;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Utils;
import com.wuest.prefab.config.ModConfiguration;
import com.wuest.prefab.items.ItemSickle;
import com.wuest.prefab.network.message.CustomStructureSyncMessage;
import com.wuest.prefab.structures.custom.base.CustomStructureInfo;
import com.wuest.prefab.structures.custom.base.ItemInfo;
import com.wuest.prefab.structures.events.StructureEventHandler;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Optional;

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

            // Go through all custom structures to ensure that all items actually exist with the current mods.
            // Print warning messages about invalid custom structures and remove them from the list.
            ArrayList<CustomStructureInfo> structuresToRemove = new ArrayList<>();

            for (CustomStructureInfo info : ModRegistry.CustomStructures) {
                for (ItemInfo itemInfo : info.requiredItems) {
                    Optional<Item> registeredItem = Registry.ITEM.getOptional(itemInfo.item);

                    if (registeredItem.isPresent()) {
                        itemInfo.registeredItem = registeredItem.get();
                    }
                    else {
                        Prefab.logger.warn("Unknown item registration: [{}] for file name [{}]", itemInfo.item.toString(), info.infoFileName);
                        structuresToRemove.add(info);
                    }
                }
            }

            // Remove any invalid structures.
            for (CustomStructureInfo info : structuresToRemove) {
                Prefab.logger.warn(
                        "Removing invalid structure with file name: {}. This structure is invalid due to unknown items in the required items collection",
                        info.infoFileName);
                ModRegistry.CustomStructures.remove(info);
            }
        });
    }

    private static void playerJoinedServer() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (entity instanceof ServerPlayer) {
                // Send the message to the client.
                FriendlyByteBuf messagePacket = Utils.createMessageBuffer(Prefab.serverConfiguration.writeCompoundTag());

                ServerPlayNetworking.send((ServerPlayer) entity, ModRegistry.ConfigSync, messagePacket);

                CustomStructureSyncMessage customStructureSyncMessage = new CustomStructureSyncMessage();
                customStructureSyncMessage.encodeStructures(ModRegistry.CustomStructures);
                FriendlyByteBuf structurePacket = Utils.createMessageBuffer(customStructureSyncMessage);

                ServerPlayNetworking.send((ServerPlayer)entity, ModRegistry.CustomStructureSync, structurePacket);
            }
        });
    }
}
