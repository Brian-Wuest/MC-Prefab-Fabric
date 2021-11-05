package com.wuest.prefab.structures.render;

import com.wuest.prefab.structures.base.BuildBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.ColorResolver;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class PrefabRenderView implements BlockRenderView {
    private final HashMap<BlockPos, BlockState> blocks = new HashMap<>();

    public PrefabRenderView(ArrayList<BuildBlock> blockList, Direction assumedNorth, Direction facing) {
        for (BuildBlock block : blockList) {
            BlockPos pos = block.getStartingPosition().getRelativePosition(
                    new BlockPos(0, 0, 0), assumedNorth, facing);
            blocks.put(pos, block.getBlockState());
        }
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        World world = MinecraftClient.getInstance().world;
        if (world != null) {
            return world.getBrightness(direction, shaded);
        } else {
            return 0.0f;
        }
    }

    @Override
    public LightingProvider getLightingProvider() {
        World world = MinecraftClient.getInstance().world;
        if (world != null) {
            return world.getLightingProvider();
        } else {
            return null;
        }
    }

    @Override
    public int getColor(BlockPos pos, ColorResolver colorResolver) {
        World world = MinecraftClient.getInstance().world;
        if (world != null) {
            return colorResolver.getColor(world.getBiome(pos), pos.getX(), pos.getY());
        } else {
            return 0;
        }
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        BlockState block = blocks.get(pos);
        if (block != null) {
            return block;
        } else {
            return Blocks.AIR.getDefaultState();
        }
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return blocks.get(pos).getFluidState();
    }

    @Override
    public int getHeight() {
        World world = MinecraftClient.getInstance().world;
        if (world != null) {
            return world.getHeight();
        } else {
            return 0;
        }
    }

    @Override
    public int getBottomY() {
        World world = MinecraftClient.getInstance().world;
        if (world != null) {
            return world.getBottomY();
        } else {
            return 0;
        }
    }
}
