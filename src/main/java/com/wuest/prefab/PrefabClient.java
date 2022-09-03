package com.wuest.prefab;

import com.wuest.prefab.config.ModConfiguration;
import com.wuest.prefab.config.RecipeMapGuiProvider;
import com.wuest.prefab.config.StructureOptionGuiProvider;
import com.wuest.prefab.events.ClientEvents;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import net.fabricmc.api.ClientModInitializer;

/**
 * This class represents the client-side initialization.
 */
public class PrefabClient implements ClientModInitializer {

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
}
