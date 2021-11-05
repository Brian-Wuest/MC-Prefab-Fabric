package com.wuest.prefab.structures.render;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import java.util.Random;
import java.util.function.Supplier;

public class PrefabBakedModel extends ForwardingBakedModel {
    public PrefabBakedModel(BakedModel model) {
        super.wrapped = model;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        context.pushTransform((quad -> {
            Direction dir = quad.cullFace();
            if (dir == null) return true;
            return Block.shouldDrawSide(state, blockView, pos, dir, pos.offset(dir));
        }));
        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        context.popTransform();
    }
}
