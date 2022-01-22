package com.wuest.prefab.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class BlockGlassSlab extends GlassBlock implements Waterloggable {

	private static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public BlockGlassSlab(AbstractBlock.Settings properties) {
		super(properties);
		this.setDefaultState(this.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM).with(WATERLOGGED, Boolean.FALSE));
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return state.get(SlabBlock.TYPE) != SlabType.DOUBLE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SlabBlock.TYPE, WATERLOGGED);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		Tag<Block> tags = BlockTags.getTagGroup().getTag(new Identifier("c", "glass"));
		Block adjacentBlock = adjacentBlockState.getBlock();

		/*
			Hide this side under the following conditions
			1. The other block is a "Glass" block (this includes colored glass).
			2. This block and the other block has a matching type.
			3. The other block is a double slab and this is a single slab.
		*/
		return tags.contains(adjacentBlock) || (adjacentBlock == this
				&& (adjacentBlockState.get(SlabBlock.TYPE) == state.get(SlabBlock.TYPE)
				|| (adjacentBlockState.get(SlabBlock.TYPE) == SlabType.DOUBLE
				&& state.get(SlabBlock.TYPE) != SlabType.DOUBLE)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		SlabType slabtype = state.get(SlabBlock.TYPE);
		switch (slabtype) {
			case DOUBLE:
				return VoxelShapes.fullCube();
			case TOP:
				return BlockGlassSlab.TOP_SHAPE;
			default:
				return BlockGlassSlab.BOTTOM_SHAPE;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockPos blockpos = context.getBlockPos();
		BlockState blockstate = context.getWorld().getBlockState(blockpos);
		if (blockstate.getBlock() == this) {
			boolean wasWaterlogged = blockstate.get(WATERLOGGED);
			return blockstate.with(SlabBlock.TYPE, SlabType.DOUBLE).with(WATERLOGGED, wasWaterlogged);
		} else {
			FluidState fluidState = context.getWorld().getFluidState(blockpos);
			BlockState defaultState = this.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
			Direction direction = context.getSide();
			return direction != Direction.DOWN && (direction == Direction.UP || !(context.getHitPos().y - (double) blockpos.getY() > 0.5D)) ? defaultState : defaultState.with(SlabBlock.TYPE, SlabType.TOP);
		}
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext useContext) {
		ItemStack itemstack = useContext.getStack();
		SlabType slabtype = state.get(SlabBlock.TYPE);
		if (slabtype != SlabType.DOUBLE && itemstack.getItem() == this.asItem()) {
			if (useContext.canReplaceExisting()) {
				boolean flag = useContext.getHitPos().y - (double) useContext.getBlockPos().getY() > 0.5D;
				Direction direction = useContext.getSide();
				if (slabtype == SlabType.BOTTOM) {
					return direction == Direction.UP || flag && direction.getAxis().isHorizontal();
				} else {
					return direction == Direction.DOWN || !flag && direction.getAxis().isHorizontal();
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
		return state.get(SlabBlock.TYPE) != SlabType.DOUBLE && this.slabReceiveFluid(worldIn, pos, state, fluidStateIn);
	}

	@Override
	public boolean canFillWithFluid(BlockView worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
		return state.get(SlabBlock.TYPE) != SlabType.DOUBLE && this.slabCanContainFluid(state, fluidIn);
	}

	private boolean slabCanContainFluid(BlockState state, Fluid fluidIn) {
		return !state.get(Properties.WATERLOGGED) && fluidIn == Fluids.WATER;
	}

	private boolean slabReceiveFluid(WorldAccess worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
		if (!state.get(Properties.WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
			if (!worldIn.isClient()) {
				worldIn.setBlockState(pos, state.with(Properties.WATERLOGGED, Boolean.TRUE), 3);
				worldIn.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public ItemStack tryDrainFluid(WorldAccess worldIn, BlockPos pos, BlockState state) {
		if (state.get(Properties.WATERLOGGED) && state.get(SlabBlock.TYPE) != SlabType.DOUBLE) {
			worldIn.setBlockState(pos, state.with(Properties.WATERLOGGED, Boolean.FALSE), 3);
			return new ItemStack(Items.WATER_BUCKET);
		} else {
			return ItemStack.EMPTY;
		}
	}

	/**
	 * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
	 * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
	 * returns its solidified counterpart.
	 * Note that this method should ideally consider only the specific face passed in.
	 */
	@Override
	public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.createAndScheduleFluidTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}

		return super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView worldIn, BlockPos pos, NavigationType type) {
		if (type == NavigationType.WATER) {
			return worldIn.getFluidState(pos).isIn(FluidTags.WATER);
		}

		return false;
	}
}
