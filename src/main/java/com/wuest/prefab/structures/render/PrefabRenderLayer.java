package com.wuest.prefab.structures.render;

import com.wuest.prefab.Prefab;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public class PrefabRenderLayer extends RenderLayer {
    private static final String LAYER_NAME = Prefab.MODID + "/prefab-render-layer";

    private PrefabRenderLayer() {
        super("", null, null, 0, false, true, null, null);
    }

    public static RenderLayer createRenderLayer() {
        RenderLayer.MultiPhaseParameters parameters = MultiPhaseParameters.builder()
                .shader(TRANSLUCENT_SHADER)
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
                .writeMaskState(COLOR_MASK)
                .depthTest(ALWAYS_DEPTH_TEST)
                .cull(DISABLE_CULLING)
                .layering(VIEW_OFFSET_Z_LAYERING)
                .build(false);
        return RenderLayer.of(LAYER_NAME, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS,
                TRANSLUCENT_BUFFER_SIZE, false, true, parameters);
    }
}
