package com.wuest.prefab.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class ClientWorldMixin {
	@Inject(method = "renderLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z"))
	public void renderWorldLast(float tickDelta, long limitTime, PoseStack matrix, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();

		if (mc.player != null && (!mc.player.isCrouching())) {
			StructureRenderHandler.renderPlayerLook(mc.player, mc.hitResult, matrix, ci);
		}
	}
}
