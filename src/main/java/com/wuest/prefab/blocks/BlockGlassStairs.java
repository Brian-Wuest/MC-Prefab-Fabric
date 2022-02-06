package com.wuest.prefab.blocks;

import com.wuest.prefab.ModRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * This class allows custom stairs blocks to be created.
 *
 * @author Brian
 */
public class BlockGlassStairs extends StairBlock {
    public BlockGlassStairs(BlockState state, Block.Properties properties) {
        super(state, properties);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        Tag<Block> tags = BlockTags.getAllTags().getTag(new ResourceLocation("c", "glass"));
        Block adjacentBlock = adjacentBlockState.getBlock();

        return tags.contains(adjacentBlock) || adjacentBlock == this
                || (adjacentBlock == ModRegistry.GlassSlab
                && adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.DOUBLE);
    }
}
