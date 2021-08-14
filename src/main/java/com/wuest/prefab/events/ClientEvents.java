package com.wuest.prefab.events;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Utils;
import com.wuest.prefab.structures.config.BasicStructureConfiguration;
import com.wuest.prefab.structures.config.StructureConfiguration;
import com.wuest.prefab.structures.events.StructureClientEventHandler;
import com.wuest.prefab.structures.gui.GuiBasicStructure;
import com.wuest.prefab.structures.gui.GuiStructure;
import com.wuest.prefab.structures.items.ItemBasicStructure;
import com.wuest.prefab.structures.items.StructureItem;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

public class ClientEvents {
    /**
     * Determines how long a shader has been running.
     */
    public static int ticksInGame;

    public static void registerClientEvents() {
        StructureClientEventHandler.registerStructureClientSideEvents();

        ClientEvents.registerClientEndTick();
    }

    public static void registerClientEndTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ClientModRegistry.keyBinding.isPressed()) {
                if (StructureRenderHandler.currentStructure != null) {
                    ItemStack mainHandStack = MinecraftClient.getInstance().player.getMainHandStack();
                    ItemStack offHandStack = MinecraftClient.getInstance().player.getOffHandStack();
                    boolean foundCorrectStructureItem = false;

                    if (mainHandStack != ItemStack.EMPTY || offHandStack != ItemStack.EMPTY) {
                        StructureTagMessage.EnumStructureConfiguration structureConfigurationEnum = StructureTagMessage.EnumStructureConfiguration.getByConfigurationInstance(StructureRenderHandler.currentConfiguration);

                        if (mainHandStack != ItemStack.EMPTY && mainHandStack.getItem() instanceof StructureItem) {
                            // Check main hand.
                            foundCorrectStructureItem = ClientEvents.checkIfStackIsCorrectGui(structureConfigurationEnum, mainHandStack);
                        }

                        if (!foundCorrectStructureItem && offHandStack != ItemStack.EMPTY && offHandStack.getItem() instanceof StructureItem) {
                            // Main hand is not correct item; check off-hand
                            foundCorrectStructureItem = ClientEvents.checkIfStackIsCorrectGui(structureConfigurationEnum, offHandStack);
                        }
                    }

                    if (foundCorrectStructureItem) {
                        PacketByteBuf messagePacket = Utils.createStructureMessageBuffer(
                                StructureRenderHandler.currentConfiguration.WriteToCompoundNBT(),
                                StructureTagMessage.EnumStructureConfiguration.getByConfigurationInstance(StructureRenderHandler.currentConfiguration));

                        ClientPlayNetworking.send(ModRegistry.StructureBuild, messagePacket);
                    }

                    StructureRenderHandler.currentStructure = null;
                }
            }
        });
    }

    public static boolean checkIfStackIsCorrectGui(StructureTagMessage.EnumStructureConfiguration currentConfiguration, ItemStack stack) {
        GuiStructure mainHandGui = ClientModRegistry.ModGuis.get(stack.getItem());
        mainHandGui.init();

        if (currentConfiguration == mainHandGui.structureConfiguration) {
            if (currentConfiguration == StructureTagMessage.EnumStructureConfiguration.Basic) {
                ItemBasicStructure item = (ItemBasicStructure) stack.getItem();
                BasicStructureConfiguration.EnumBasicStructureName basicStructureName = ((BasicStructureConfiguration)StructureRenderHandler.currentConfiguration).basicStructureName;

                return item.structureType == basicStructureName;
            } else {
                return true;
            }
        }

        return false;
    }
}
