package com.wuest.prefab.blocks;

import com.mojang.serialization.MapCodec;
import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.base.TileBlockBase;
import com.wuest.prefab.blocks.entities.StructureScannerBlockEntity;
import com.wuest.prefab.config.StructureScannerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BlockStructureScanner extends TileBlockBase<StructureScannerBlockEntity> {
    public static final DirectionProperty FACING;
    public static final MapCodec<BlockStructureScanner> CODEC = simpleCodec(BlockStructureScanner::new);

    static {
        FACING = HorizontalDirectionalBlock.FACING;
    }

    /**
     * Initializes a new instance of the BlockStructureScanner class.
     */
    public BlockStructureScanner() {
        super(Block.Properties.ofFullCopy(Blocks.STONE));

        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH));
    }

    public BlockStructureScanner(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(BlockStructureScanner.FACING, ctx.getHorizontalDirection());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStructureScanner.FACING, rotation.rotate(state.getValue(BlockStructureScanner.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(BlockStructureScanner.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStructureScanner.FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof StructureScannerBlockEntity) {
                StructureScannerConfig config = ((StructureScannerBlockEntity) blockEntity).getConfig();
                ClientModRegistry.openGuiForBlock(pos, world, config);
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StructureScannerBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
