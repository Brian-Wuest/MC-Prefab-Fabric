package com.wuest.prefab.blocks;

import com.wuest.prefab.ModRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.SlabBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class BlockDirtSlab extends SlabBlock implements IGrassSpreadable {
    public BlockDirtSlab() {
        super(FabricBlockSettings.of(Material.AGGREGATE, MapColor.DIRT_BROWN).sounds(BlockSoundGroup.GRAVEL)
                .strength(0.5f, 0.5f));
    }

    /**
     * Returns whether or not this block is of a type that needs random ticking.
     * Called for ref-counting purposes by ExtendedBlockStorage in order to broadly
     * cull a chunk from the random chunk update list for efficiency's sake.
     */
    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        this.DetermineGrassSpread(state, worldIn, pos, random);
    }

    @Override
    public BlockState getGrassBlockState(BlockState originalState) {
        return ModRegistry.GrassSlab.getDefaultState().with(SlabBlock.TYPE,
                originalState.get(SlabBlock.TYPE));
    }
}
