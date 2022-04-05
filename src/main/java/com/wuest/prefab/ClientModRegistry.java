package com.wuest.prefab;

import com.mojang.blaze3d.platform.InputConstants;
import com.wuest.prefab.base.BaseConfig;
import com.wuest.prefab.blocks.BlockCustomWall;
import com.wuest.prefab.blocks.BlockGrassSlab;
import com.wuest.prefab.blocks.BlockGrassStairs;
import com.wuest.prefab.config.EntityPlayerConfiguration;
import com.wuest.prefab.config.block_entities.DraftingTableConfiguration;
import com.wuest.prefab.config.block_entities.StructureScannerConfig;
import com.wuest.prefab.gui.GuiBase;
import com.wuest.prefab.gui.screens.GuiDraftingTable;
import com.wuest.prefab.gui.screens.GuiStructureScanner;
import com.wuest.prefab.network.message.ConfigSyncMessage;
import com.wuest.prefab.network.message.CustomStructureSyncMessage;
import com.wuest.prefab.network.message.PlayerEntityTagMessage;
import com.wuest.prefab.structures.custom.base.CustomStructureInfo;
import com.wuest.prefab.structures.gui.GuiStructure;
import com.wuest.prefab.structures.items.StructureItem;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ClientModRegistry {

    public static EntityPlayerConfiguration playerConfig = new EntityPlayerConfiguration();
    public static KeyMapping keyBinding;
    public static ArrayList<StructureScannerConfig> structureScanners;
    public static ArrayList<CustomStructureInfo> ServerRegisteredStructures = new ArrayList<>();

    /**
     * The hashmap of mod guis.
     */
    public static HashMap<StructureItem, GuiStructure> ModGuis = new HashMap<>();

    static {
        ClientModRegistry.structureScanners = new ArrayList<>();
    }

    public static void registerModComponents() {
        ClientModRegistry.registerKeyBindings();

        ClientModRegistry.registerBlockLayers();

        ClientModRegistry.registerServerToClientMessageHandlers();

        ClientModRegistry.RegisterGuis();
    }

    public static void openGuiForItem(UseOnContext itemUseContext) {
        for (Map.Entry<StructureItem, GuiStructure> entry : ClientModRegistry.ModGuis.entrySet()) {
            if (entry.getKey() == itemUseContext.getItemInHand().getItem()) {
                GuiStructure screen = entry.getValue();
                screen.pos = itemUseContext.getClickedPos();

                Minecraft.getInstance().setScreen(screen);
            }
        }
    }

    public static void openGuiForBlock(BlockPos blockPos, Level world, BaseConfig config) {
        Screen screen = null;

        if (config instanceof StructureScannerConfig) {
            screen = new GuiStructureScanner(blockPos, world, (StructureScannerConfig) config);
        }

        if (config instanceof DraftingTableConfiguration) {
            screen = new GuiDraftingTable(blockPos, world, (DraftingTableConfiguration) config);
            Minecraft.getInstance().player.containerMenu = ((MenuAccess)screen).getMenu();
        }

        if (screen != null) {
            Minecraft.getInstance().setScreen(screen);
        }
    }

    private static void registerServerToClientMessageHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(ModRegistry.ConfigSync,
                (client, handler, buf, responseSender) -> {
                    // Can only access the "attachedData" on the "network thread" which is here.
                    ConfigSyncMessage syncMessage = ConfigSyncMessage.decode(buf);

                    client.execute(() -> {
                        // This is now on the "main" client thread and things can be done in the world!
                        Prefab.serverConfiguration.readFromTag(syncMessage.getMessageTag());
                    });
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(ModRegistry.PlayerConfigSync, (client, handler, buf, responseSender) -> {
            // Can only access the "buf" on the "network thread" which is here.
            PlayerEntityTagMessage syncMessage = PlayerEntityTagMessage.decode(buf);

            client.execute(() -> {
                // This is now on the "main" client thread and things can be done in the world!
                UUID playerUUID = Minecraft.getInstance().player.getUUID();

                ClientModRegistry.playerConfig = EntityPlayerConfiguration.loadFromTag(playerUUID, syncMessage.getMessageTag());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModRegistry.CustomStructureSync, (client, handler, buf, responseSender) -> {
            // Can only access the "buf" on the "network thread" which is here.
            CustomStructureSyncMessage syncMessage = CustomStructureSyncMessage.decode(buf);

            client.execute(() -> {
                ArrayList<CustomStructureInfo> tempServerRegisteredStructures = syncMessage.getDecodedStructures();

                // Always clear out the list of server registered structures to avoid adding duplicates or from other worlds.
                ClientModRegistry.ServerRegisteredStructures = new ArrayList<>();

                // Nested loop for server and client custom structures to ensure that everything is in-sync.
                if (tempServerRegisteredStructures != null && ModRegistry.CustomStructures != null) {
                    for (CustomStructureInfo serverStructure : tempServerRegisteredStructures) {
                        boolean foundClientStructure = false;

                        for (CustomStructureInfo clientStructure : ModRegistry.CustomStructures) {
                            if (serverStructure.infoFileName.equalsIgnoreCase(clientStructure.infoFileName)) {
                                // Update the server structure to use the client structure file path.
                                // This is for the preview function.
                                // Since this path is important for the client is the only piece of information which should come solely from the client.
                                serverStructure.structureFilePath = clientStructure.structureFilePath;
                                foundClientStructure = true;
                                ClientModRegistry.ServerRegisteredStructures.add(serverStructure);
                                break;
                            }
                        }

                        if(foundClientStructure) {
                            break;
                        }
                    }
                }
            });
        });
    }

    private static void registerBlockLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.GlassStairs, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.GlassSlab, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.PaperLantern, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.Boundary, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.Phasic, RenderType.translucent());

        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.GrassStairs, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.DirtStairs, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.GrassSlab, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.DirtSlab, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.GrassWall, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.DirtWall, RenderType.cutoutMipped());
    }

    /**
     * This is called within a Mixin as the BlockColors and ItemColors classes are otherwise null when this class is called.
     */
    public static void RegisterBlockRenderer() {
        // Register the block renderer.
        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> worldIn != null && pos != null
                ? BiomeColors.getAverageGrassColor(worldIn, pos)
                : GrassColor.get(0.5D, 1.0D), ModRegistry.GrassWall, ModRegistry.GrassSlab, ModRegistry.GrassStairs);

        // Register the item renderer.
        Minecraft.getInstance().itemColors.register((stack, tintIndex) -> {
            // Get the item for this stack.
            Item item = stack.getItem();

            if (item instanceof BlockItem) {
                // Get the block for this item and determine if it's a grass stairs.
                BlockItem itemBlock = (BlockItem) item;
                boolean paintBlock = false;

                if (itemBlock.getBlock() instanceof BlockCustomWall) {
                    BlockCustomWall customWall = (BlockCustomWall) itemBlock.getBlock();

                    if (customWall.BlockVariant == BlockCustomWall.EnumType.GRASS) {
                        paintBlock = true;
                    }
                } else if (itemBlock.getBlock() instanceof BlockGrassSlab) {
                    paintBlock = true;
                } else if (itemBlock.getBlock() instanceof BlockGrassStairs) {
                    paintBlock = true;
                }

                if (paintBlock) {
                    BlockPos pos = Minecraft.getInstance().player.blockPosition();
                    ClientLevel world = Minecraft.getInstance().level;
                    return BiomeColors.getAverageGrassColor(world, pos);
                }
            }

            return -1;
        }, new Block[]{ModRegistry.GrassWall, ModRegistry.GrassSlab, ModRegistry.GrassStairs});
    }

    /**
     * Adds all of the Mod Guis to the HasMap.
     */
    private static void RegisterGuis() {
        for (Consumer<Object> consumer : ModRegistry.guiRegistrations) {
            consumer.accept(null);
        }
    }

    private static void registerKeyBindings() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "Build Current Structure", // The translation key of the keybinding's name
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "Prefab - Structure Preview" // The translation key of the keybinding's category.
        ));
    }
}
