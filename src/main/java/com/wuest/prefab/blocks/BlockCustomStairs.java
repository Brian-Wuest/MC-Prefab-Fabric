package com.wuest.prefab.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

/**
 * This class just extends the vanilla stairs block for easier implementation.
 */
public class BlockCustomStairs extends StairsBlock {
    public BlockCustomStairs(BlockState baseBlockState, Settings settings) {
        super(baseBlockState, settings);
    }
}
