package com.wuest.prefab.structures.render;

import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Supplier;

public class ShaderManager {
    public static @Nullable RenderPhase.Shader PREFAB_PREVIEW;
    public static @Nullable Shader PREFAB_PREVIEW_SHADER;

    public static void loadShaders(ResourceManager manager) {
        try {
            PREFAB_PREVIEW_SHADER = new Shader(manager, "prefab_preview", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Supplier<Shader> shaderSupplier = () -> PREFAB_PREVIEW_SHADER;
            PREFAB_PREVIEW = new RenderPhase.Shader(shaderSupplier);
        }
    }
}
