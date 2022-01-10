package com.wuest.prefab.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockShaped extends Block {
    private final BlockShape shape;

    public BlockShaped(BlockShape shape, Settings properties) {
        super(properties);

        this.shape = shape;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.shape.getShape();
    }

    public enum BlockShape {

        PileOfBricks(Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 5.0D, 12.0D)),
        PalletOfBricks(Block.createCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 15.0D, 16.0D)),
        BundleOfTimber(Block.createCuboidShape(0.0D, 0.0D, 0.0D, 15.0D, 4.0D, 15.0D)),
        HeapOfTimber(Block.createCuboidShape(3.0D, 0.0D, 2.0D, 13.0D, 6.0D, 14.0D)),
        TonOfTimber(Block.createCuboidShape(1.0D, 0.0D, 2.0D, 14.0D, 9.0D, 14.0D));

        private final VoxelShape shape;

        BlockShape(VoxelShape shape) {
            this.shape = shape;
        }

        public VoxelShape getShape() {
            return this.shape;
        }
    }
}
