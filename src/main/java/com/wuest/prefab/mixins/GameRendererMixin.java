package com.wuest.prefab.mixins;

import com.wuest.prefab.structures.render.ShaderManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "loadShaders", at = @At("RETURN"))
    private void loadShaders(ResourceManager manager, CallbackInfo ci) {
        ShaderManager.loadShaders(manager);
    }
}
