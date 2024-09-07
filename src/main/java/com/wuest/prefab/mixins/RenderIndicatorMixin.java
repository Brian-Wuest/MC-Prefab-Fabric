package com.wuest.prefab.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class RenderIndicatorMixin {
    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderWorldLast(PoseStack matrices, MultiBufferSource.BufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        Minecraft prefabMinecraft = Minecraft.getInstance();

        if (prefabMinecraft.player != null && (!prefabMinecraft.player.isCrouching())) {
            StructureRenderHandler.RenderTest(prefabMinecraft.level, matrices, vertexConsumers, (float)cameraX, (float)cameraY, (float)cameraZ);
        }

        // It there are structure scanners; run the rendering for them now.
        if (ClientModRegistry.structureScanners != null && !ClientModRegistry.structureScanners.isEmpty()) {
            StructureRenderHandler.renderScanningBoxes(matrices, vertexConsumers, (float)cameraX, (float)cameraY, (float)cameraZ);
        }
    }
}
