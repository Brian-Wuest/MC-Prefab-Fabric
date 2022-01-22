package com.wuest.prefab.blocks;

import com.wuest.prefab.Prefab;
import com.wuest.prefab.Utils;
import com.wuest.prefab.events.ServerEvents;
import com.wuest.prefab.gui.GuiLangKeys;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockBoundary extends Block {
	/**
	 * The powered meta data property.
	 */
	public static final BooleanProperty Powered = BooleanProperty.of("powered");

	public final ItemGroup itemGroup;

	/**
	 * Initializes a new instance of the BlockBoundary class.
	 */
	public BlockBoundary() {
		super(AbstractBlock.Settings.of(Prefab.SeeThroughImmovable)
				.sounds(BlockSoundGroup.STONE)
				.strength(0.6F)
				.nonOpaque());

		this.itemGroup = ItemGroup.BUILDING_BLOCKS;
		this.setDefaultState(this.stateManager.getDefaultState().with(Powered, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(BlockBoundary.Powered);
	}

	/**
	 * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
	 * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
	 */
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		boolean powered = state.get(Powered);
		return powered ? BlockRenderType.MODEL : BlockRenderType.INVISIBLE;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		boolean powered = state.get(Powered);

		return powered ? VoxelShapes.fullCube() : VoxelShapes.empty();
	}

	/**
	 * Called when a player removes a block. This is responsible for actually destroying the block, and the block is
	 * intact at time of call. This is called regardless of whether the player can harvest the block or not.
	 * <p>
	 * Return true if the block is actually destroyed.
	 * <p>
	 * Note: When used in multi-player, this is called on both client and server sides!
	 *
	 * @param state  The current state.
	 * @param world  The current world
	 * @param player The player damaging the block, may be null
	 * @param pos    Block position in world
	 */
	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);

		ServerEvents.RedstoneAffectedBlockPositions.remove(pos);

		boolean poweredSide = world.isReceivingRedstonePower(pos);

		if (poweredSide) {
			this.setNeighborGlassBlocksPoweredStatus(world, pos, false, 0, new ArrayList<>(), false);
		}
	}

	/**
	 * Gets the {@link BlockState} to place
	 *
	 * @param context The {@link ItemPlacementContext}.
	 * @return The state to be placed in the world
	 */
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		/*
		 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
		 * BlockState
		 */
		boolean poweredSide = context.getWorld().isReceivingRedstonePower(context.getBlockPos());

		if (poweredSide) {
			this.setNeighborGlassBlocksPoweredStatus(context.getWorld(), context.getBlockPos(), true, 0, new ArrayList<>(), false);
		}

		return this.getDefaultState().with(Powered, poweredSide);
	}

	/**
	 * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
	 * change. Cases may include when red-stone power is updated, cactus blocks popping off due to a neighboring solid
	 * block, etc.
	 */
	@Override
	public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos p_189540_5_, boolean p_220069_6_) {
		if (!worldIn.isClient) {
			// Only worry about powering blocks.
			if (blockIn.getDefaultState().emitsRedstonePower()) {
				boolean poweredSide = worldIn.isReceivingRedstonePower(pos);

				this.setNeighborGlassBlocksPoweredStatus(worldIn, pos, poweredSide, 0, new ArrayList<>(), true);
			}
		}
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
		super.appendTooltip(stack, world, tooltip, options);
		boolean advancedKeyDown = Screen.hasShiftDown();

		if (!advancedKeyDown) {
			tooltip.add(GuiLangKeys.translateToComponent(GuiLangKeys.SHIFT_TOOLTIP));
		} else {
			tooltip.addAll(Utils.WrapStringToLiterals(GuiLangKeys.translateString(GuiLangKeys.BOUNDARY_TOOLTIP)));
		}
	}

	@Override
	public int getOpacity(BlockState state, BlockView worldIn, BlockPos pos) {
		boolean powered = state.get(Powered);

		if (powered && state.isOpaqueFullCube(worldIn, pos)) {
			return worldIn.getMaxLightLevel();
		} else {
			return state.isTranslucent(worldIn, pos) ? 0 : 1;
		}
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView reader, BlockPos pos) {
		boolean powered = state.get(Powered);

		return !powered || (!isShapeFullCube(state.getOutlineShape(reader, pos)) && state.getFluidState().isEmpty());
	}

	/**
	 * Get's the collision shape.
	 *
	 * @param state   The block state.
	 * @param worldIn The world object.
	 * @param pos     The block Position.
	 * @param context The selection context.
	 * @return Returns a shape.
	 */
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		return VoxelShapes.fullCube();
	}

	@Deprecated
	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView worldIn, BlockPos pos) {
		if (!state.get(Powered)) {
			return VoxelShapes.empty();
		} else {
			return VoxelShapes.fullCube();
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		return !state.get(Powered);
	}

	/**
	 * Sets the neighbor powered status
	 *
	 * @param world            The world where the block resides.
	 * @param pos              The position of the block.
	 * @param isPowered        Determines if the block is powered.
	 * @param cascadeCount     How many times this has been cascaded.
	 * @param cascadedBlockPos All of the block positions which have been cascaded too.
	 * @param setCurrentBlock  Determines if the current block should be set.
	 */
	protected void setNeighborGlassBlocksPoweredStatus(World world, BlockPos pos, boolean isPowered, int cascadeCount, ArrayList<BlockPos> cascadedBlockPos,
													   boolean setCurrentBlock) {
		cascadeCount++;

		if (cascadeCount > 100) {
			return;
		}

		if (setCurrentBlock) {
			BlockState state = world.getBlockState(pos);
			world.setBlockState(pos, state.with(Powered, isPowered));
		}

		cascadedBlockPos.add(pos);

		for (Direction facing : Direction.values()) {
			Block neighborBlock = world.getBlockState(pos.offset(facing)).getBlock();

			if (neighborBlock instanceof BlockBoundary) {
				// If the block is already in the correct state, there is no need to cascade to it's neighbors.
				if (cascadedBlockPos.contains(pos.offset(facing))) {
					continue;
				}

				// running this method for the neighbor block will cascade out to it's other neighbors until there are
				// no more Phasic blocks around.
				((BlockBoundary) neighborBlock).setNeighborGlassBlocksPoweredStatus(world, pos.offset(facing), isPowered, cascadeCount, cascadedBlockPos, true);
			}
		}
	}
}
