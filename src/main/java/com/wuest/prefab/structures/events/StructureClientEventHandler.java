package com.wuest.prefab.structures.events;

import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;

/**
 * @author WuestMan
 */
public final class StructureClientEventHandler {

    public static void registerStructureClientSideEvents() {

        StructureClientEventHandler.registerPlayerUseItemEvent();
    }

    public static void registerPlayerUseItemEvent() {
        StructureClientEventHandler.onPlayerUseBlock();
    }

    /**
     * The player right-click block event. This is used to stop the structure rendering for the preview.
     */
    public static void onPlayerUseBlock() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (StructureRenderHandler.currentStructure != null && player == Minecraft.getInstance().player) {
                StructureRenderHandler.setStructure(null, Direction.NORTH, null);

                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });
    }
}
