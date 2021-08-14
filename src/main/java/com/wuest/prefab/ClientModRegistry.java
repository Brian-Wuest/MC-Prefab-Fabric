package com.wuest.prefab;

import com.wuest.prefab.config.EntityPlayerConfiguration;
import com.wuest.prefab.network.message.ConfigSyncMessage;
import com.wuest.prefab.network.message.PlayerEntityTagMessage;
import com.wuest.prefab.structures.gui.*;
import com.wuest.prefab.structures.items.StructureItem;
import com.wuest.prefab.structures.render.ShaderHelper;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemUsageContext;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ClientModRegistry {

	public static EntityPlayerConfiguration playerConfig = new EntityPlayerConfiguration();
	public static KeyBinding keyBinding;

	/**
	 * The hashmap of mod guis.
	 */
	public static HashMap<StructureItem, GuiStructure> ModGuis = new HashMap<>();

	public static void registerModComponents() {
		ClientModRegistry.registerKeyBindings();

		ClientModRegistry.registerBlockLayers();

		ClientModRegistry.registerServerToClientMessageHandlers();

		ClientModRegistry.registerRenderers();

		ClientModRegistry.RegisterGuis();
	}

	public static void openGuiForItem(ItemUsageContext itemUseContext) {
		for (Map.Entry<StructureItem, GuiStructure> entry : ClientModRegistry.ModGuis.entrySet()) {
			if (entry.getKey() == itemUseContext.getStack().getItem()) {
				GuiStructure screen = entry.getValue();
				screen.pos = itemUseContext.getBlockPos();

				MinecraftClient.getInstance().openScreen(screen);
			}
		}
	}

	private static void registerServerToClientMessageHandlers() {
		ClientSidePacketRegistry.INSTANCE.register(ModRegistry.ConfigSync,
				(packetContext, attachedData) -> {
					// Can only access the "attachedData" on the "network thread" which is here.
					ConfigSyncMessage syncMessage = ConfigSyncMessage.decode(attachedData);

					packetContext.getTaskQueue().execute(() -> {
						// This is now on the "main" client thread and things can be done in the world!
						Prefab.serverConfiguration.readFromTag(syncMessage.getMessageTag());
					});
				}
		);

		ClientSidePacketRegistry.INSTANCE.register(ModRegistry.PlayerConfigSync, (packetContext, attachedData) -> {
			// Can only access the "attachedData" on the "network thread" which is here.
			PlayerEntityTagMessage syncMessage = PlayerEntityTagMessage.decode(attachedData);

			packetContext.getTaskQueue().execute(() -> {
				// This is now on the "main" client thread and things can be done in the world!
				UUID playerUUID = MinecraftClient.getInstance().player.getUuid();

				ClientModRegistry.playerConfig = EntityPlayerConfiguration.loadFromTag(playerUUID, syncMessage.getMessageTag());
			});
		});
	}

	private static void registerBlockLayers() {
		BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.GlassStairs, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.GlassSlab, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.PaperLantern, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.Boundary, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(ModRegistry.Phasic, RenderLayer.getTranslucent());
	}

	private static void registerRenderers() {
		ShaderHelper.Initialize();
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
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Build Current Structure", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_B,
				"Prefab - Structure Preview" // The translation key of the keybinding's category.
		));
	}
}
