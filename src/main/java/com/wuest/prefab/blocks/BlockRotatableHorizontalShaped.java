package com.wuest.prefab.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class BlockRotatableHorizontalShaped extends BlockShaped {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public BlockRotatableHorizontalShaped(BlockShape shape, AbstractBlock.Settings properties) {
        super(shape, properties);
        this.setDefaultState(this.stateManager.getDefaultState().with(BlockRotatableHorizontalShaped.FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(net.minecraft.state.StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(BlockRotatableHorizontalShaped.FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(BlockRotatableHorizontalShaped.FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(BlockRotatableHorizontalShaped.FACING, context.getPlayerFacing().getOpposite());
    }

}
