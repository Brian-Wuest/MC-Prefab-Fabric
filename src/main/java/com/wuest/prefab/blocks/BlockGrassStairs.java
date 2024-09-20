package com.wuest.prefab.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * This class defines a set of grass stairs.
 *
 * @author WuestMan
 */
public class BlockGrassStairs extends StairBlock {

    public BlockGrassStairs() {
        super(Blocks.GRASS_BLOCK.defaultBlockState(),
                FabricBlockSettings.ofFullCopy(Blocks.GRASS_BLOCK));
    }
}
