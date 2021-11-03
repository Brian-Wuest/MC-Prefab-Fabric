package com.wuest.prefab.mixins;

import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class ClientWorldMixin {
	@Inject(method = "renderWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"))
	public void renderWorldLast(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
		MinecraftClient mc = MinecraftClient.getInstance();

		if (mc.player != null && (!mc.player.isSneaking())) {
			StructureRenderHandler.render(matrix);
		}
	}
}
