package com.wuest.prefab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.wuest.prefab.config.ModConfiguration;
import com.wuest.prefab.config.RecipeMapGuiProvider;
import com.wuest.prefab.config.StructureOptionGuiProvider;
import com.wuest.prefab.events.ClientEvents;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;

/**
 * This class represents the client-side initialization.
 */
public class PrefabClient implements ClientModInitializer {

	public static final RenderType PREVIEW_LAYER = new PreviewLayer();

	@Override
	public void onInitializeClient() {
		Prefab.logger.info("Registering client-side components");
		ClientModRegistry.registerModComponents();

		GuiRegistry registry = AutoConfig.getGuiRegistry(ModConfiguration.class);
		RecipeMapGuiProvider providerMap = new RecipeMapGuiProvider();
		StructureOptionGuiProvider structureOptionGuiProvider = new StructureOptionGuiProvider();

		registry.registerPredicateProvider(providerMap, (field) -> field.getDeclaringClass() == ModConfiguration.class && field.getName().equals("recipes"));
		registry.registerPredicateProvider(structureOptionGuiProvider, (field) -> field.getDeclaringClass() == ModConfiguration.class && field.getName().equals("structureOptions"));

		ClientEvents.registerClientEvents();
	}

	private static class PreviewLayer extends RenderType {
		public PreviewLayer() {
			super(Prefab.MODID + ".preview", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
					() -> {
						Sheets.translucentCullBlockSheet().setupRenderState();
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.4F);
					}, () -> {
						Sheets.translucentCullBlockSheet().clearRenderState();
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					});
		}
	}
}
