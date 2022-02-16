package com.wuest.prefab;

import com.wuest.prefab.config.ModConfiguration;
import com.wuest.prefab.events.ServerEvents;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class Prefab implements ModInitializer {
    /**
     * This is the ModID
     */
    public static final String MODID = "prefab";

    /**
     * Simulates an air block that blocks movement and cannot be moved.
     */
    public static final Material SeeThroughImmovable = new Material(
            MaterialColor.NONE,
            false,
            true,
            true,
            false,
            false,
            false,
            PushReaction.IGNORE);

    public static Logger logger;

    /**
     * This is used to determine if the mod is currently being debugged.
     */
    public static boolean isDebug = false;

    /**
     * Determines if structure items will scan their defined space or show the build gui. Default is false.
     * Note: this should only be set to true during debug mode.
     */
    public static boolean useScanningMode = false;
    public static ModConfiguration configuration;
    public static ModConfiguration serverConfiguration;
    public static Path configFolder;
    public static Path customStructuresFolder;

    static {
        Prefab.logger = LogManager.getLogger("Prefab");
        Prefab.isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");
    }

    @Override
    public void onInitialize() {
        Prefab.logger.info("Registering Mod Components");
        ModRegistry.registerModComponents();

        AutoConfig.register(ModConfiguration.class, GsonConfigSerializer::new);

        Prefab.serverConfiguration = new ModConfiguration();
        Prefab.configuration = AutoConfig.getConfigHolder(ModConfiguration.class).getConfig();

        ServerEvents.registerServerEvents();
    }
}
