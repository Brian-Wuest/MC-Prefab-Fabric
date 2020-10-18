package com.wuest.prefab.structures.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.structures.events.StructureEventHandler;
import com.wuest.prefab.Triple;
import com.wuest.prefab.ZipUtil;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.structures.config.StructureConfiguration;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Each structure represents a building which is pre-defined in a JSON file.
 *
 * @author WuestMan
 */
@SuppressWarnings({"unchecked", "WeakerAccess", "ConstantConditions"})
public class Structure {
	public ArrayList<BlockPos> allBlockPositions = new ArrayList<>();
	public ArrayList<BlockPos> clearedBlockPos = new ArrayList<BlockPos>();
	public ArrayList<BuildBlock> priorityOneBlocks = new ArrayList<BuildBlock>();
	public ArrayList<BuildBlock> priorityTwoBlocks = new ArrayList<>();
	public ArrayList<BuildBlock> priorityThreeBlocks = new ArrayList<BuildBlock>();
	public ArrayList<BuildBlock> airBlocks = new ArrayList<>();
	public StructureConfiguration configuration;
	public ServerWorld world;
	public BlockPos originalPos;
	public Direction assumedNorth;
	public boolean hasAirBlocks = false;
	public boolean entitiesRemoved = false;

	@Expose
	public ArrayList<BuildTileEntity> tileEntities = new ArrayList<BuildTileEntity>();
	@Expose
	public ArrayList<BuildEntity> entities = new ArrayList<BuildEntity>();
	@Expose
	private String name;
	@Expose
	private BuildClear clearSpace;
	@Expose
	private ArrayList<BuildBlock> blocks;

	public Structure() {
		this.Initialize();
	}

	/**
	 * Creates an instance of the structure after reading from a resource location and converting it from JSON.
	 *
	 * @param <T>              The type which extends Structure.
	 * @param resourceLocation The location of the JSON file to load. Example: "assets/prefab/structures/warehouse.json"
	 * @param child            The child class which extends Structure.
	 * @return Null if the resource wasn't found or the JSON could not be parsed, otherwise the de-serialized object.
	 */
	public static <T extends Structure> T CreateInstance(String resourceLocation, Class<? extends Structure> child) {
		T structure = null;

		Gson file = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		structure = (T) file.fromJson(ZipUtil.decompressResource(resourceLocation), child);

		return structure;
	}

	public static void CreateStructureFile(Structure structure, String fileLocation) {
		try {
			Gson converter = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			StringWriter stringWriter = new StringWriter();
			converter.toJson(structure, stringWriter);

			ZipUtil.zipStringToFile(stringWriter.toString(), fileLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void ScanStructure(World world, BlockPos originalPos, BlockPos cornerPos1, BlockPos cornerPos2, String fileLocation, BuildClear clearedSpace,
									 Direction playerFacing, boolean includeAir, boolean excludeWater) {
		Structure scannedStructure = new Structure();
		scannedStructure.setClearSpace(clearedSpace);

		for (BlockPos currentPos : BlockPos.iterate(cornerPos1, cornerPos2)) {
			if (world.isAir(currentPos) && !includeAir) {
				continue;
			}

			BlockState currentState = world.getBlockState(currentPos);
			Block currentBlock = currentState.getBlock();

			if (currentState.getMaterial() == Material.WATER && excludeWater) {
				continue;
			}

			BuildBlock buildBlock = Structure.createBuildBlockFromBlockState(currentState, currentBlock, currentPos, originalPos);

			if (currentBlock instanceof DoorBlock) {
				DoubleBlockHalf blockHalf = currentState.get(DoorBlock.HALF);

				if (blockHalf == DoubleBlockHalf.LOWER) {
					BlockState upperHalfState = world.getBlockState(currentPos.up());

					if (upperHalfState.getBlock() instanceof DoorBlock) {
						Block upperBlock = upperHalfState.getBlock();
						BuildBlock upperHalf = Structure.createBuildBlockFromBlockState(upperHalfState, upperBlock, currentPos.up(), originalPos);

						buildBlock.setSubBlock(upperHalf);
					}
				} else {
					// Don't process upper door halves. These were already done.
					continue;
				}
			} else if (currentBlock instanceof BedBlock) {
				BedPart bedPart = currentState.get(BedBlock.PART);

				if (bedPart == BedPart.HEAD) {
					BlockState bedFoot = null;
					boolean foundFoot = false;
					Direction facing = Direction.NORTH;

					while (!foundFoot) {
						bedFoot = world.getBlockState(currentPos.offset(facing));

						if (bedFoot.getBlock() instanceof BedBlock && bedFoot.get(BedBlock.PART) == BedPart.FOOT) {
							foundFoot = true;
							break;
						}

						facing = facing.rotateYClockwise();

						if (facing == Direction.NORTH) {
							// Got back to north, break out to avoid infinite loop.
							break;
						}
					}

					if (foundFoot) {
						Block footBedBlock = bedFoot.getBlock();
						BuildBlock bed = Structure.createBuildBlockFromBlockState(bedFoot, footBedBlock, currentPos.offset(facing), originalPos);
						buildBlock.setSubBlock(bed);
					}
				} else {
					// Don't process foot of bed, it was already done.
					continue;
				}
			}

			scannedStructure.getBlocks().add(buildBlock);

			BlockEntity tileEntity = world.getBlockEntity(currentPos);

			if (tileEntity != null) {
				// Don't write data for empty tile entities.
				if ((tileEntity instanceof ChestBlockEntity && ((ChestBlockEntity) tileEntity).isEmpty())
						|| (tileEntity instanceof FurnaceBlockEntity && ((FurnaceBlockEntity) tileEntity).isEmpty())) {
					continue;
				}

				Identifier resourceLocation = Registry.BLOCK_ENTITY_TYPE.getId(tileEntity.getType());
				CompoundTag tagCompound = new CompoundTag();
				tagCompound = tileEntity.toTag(tagCompound);

				BuildTileEntity buildTileEntity = new BuildTileEntity();
				assert resourceLocation != null;
				buildTileEntity.setEntityDomain(resourceLocation.getNamespace());
				buildTileEntity.setEntityName(resourceLocation.getPath());
				buildTileEntity.setStartingPosition(Structure.getStartingPositionFromOriginalAndCurrentPosition(currentPos, originalPos));
				buildTileEntity.setEntityNBTData(tagCompound);
				scannedStructure.tileEntities.add(buildTileEntity);
			}
		}

		int x_radiusRangeBegin = Math.min(cornerPos1.getX(), cornerPos2.getX());
		int x_radiusRangeEnd = Math.max(cornerPos1.getX(), cornerPos2.getX());
		int y_radiusRangeBegin = Math.min(cornerPos1.getY(), cornerPos2.getY());
		int y_radiusRangeEnd = Math.max(cornerPos1.getY(), cornerPos2.getY());
		int z_radiusRangeBegin = Math.min(cornerPos1.getZ(), cornerPos2.getZ());
		int z_radiusRangeEnd = Math.max(cornerPos1.getZ(), cornerPos2.getZ());

		Box axis = new Box(cornerPos1, cornerPos2);

		for (Entity entity : world.getEntitiesIncludingUngeneratedChunks(null, axis)) {
			// TODO: This was the "getPosition" method.
			BlockPos entityPos = entity.getBlockPos();

			if (entityPos.getX() >= x_radiusRangeBegin && entityPos.getX() <= x_radiusRangeEnd
					&& entityPos.getZ() >= z_radiusRangeBegin && entityPos.getZ() <= z_radiusRangeEnd
					&& entityPos.getY() >= y_radiusRangeBegin && entityPos.getY() <= y_radiusRangeEnd) {
				BuildEntity buildEntity = new BuildEntity();
				buildEntity.setEntityResourceString(Registry.ENTITY_TYPE.getId(entity.getType()));
				buildEntity.setStartingPosition(Structure.getStartingPositionFromOriginalAndCurrentPosition(entityPos, originalPos));

				// The function calls below get the following fields from the "entity" class. posX, posY, posZ.
				// This will probably have to change when the mappings get updated.
				buildEntity.entityXAxisOffset = entityPos.getX() - entity.getPos().getX();
				buildEntity.entityYAxisOffset = entityPos.getY() - entity.getPos().getY();
				buildEntity.entityZAxisOffset = entityPos.getZ() - entity.getPos().getZ();

				if (entity instanceof ItemFrameEntity) {
					buildEntity.entityYAxisOffset = buildEntity.entityYAxisOffset * -1;
				}


				if (entity instanceof AbstractDecorationEntity) {
					buildEntity.entityFacing = entity.getHorizontalFacing();
				}

				CompoundTag entityTagCompound = new CompoundTag();
				entityTagCompound = entity.toTag(entityTagCompound);
				buildEntity.setEntityNBTData(entityTagCompound);
				scannedStructure.entities.add(buildEntity);
			}
		}

		Structure.CreateStructureFile(scannedStructure, fileLocation);
	}

	/**
	 * Creates a build block from the current block state.
	 *
	 * @param currentState The block state.
	 * @param currentBlock The current block.
	 * @param currentPos   The current position.
	 * @return A new Build block object.
	 */
	public static BuildBlock createBuildBlockFromBlockState(BlockState currentState, Block currentBlock, BlockPos currentPos, BlockPos originalPos) {
		BuildBlock buildBlock = new BuildBlock();
		Identifier blockIdentifier = Registry.BLOCK.getId(currentBlock);
		buildBlock.setBlockDomain(blockIdentifier.getNamespace());
		buildBlock.setBlockName(blockIdentifier.getPath());
		buildBlock.setStartingPosition(Structure.getStartingPositionFromOriginalAndCurrentPosition(currentPos, originalPos));
		buildBlock.blockPos = currentPos;

		Collection<Property<?>> properties = currentState.getProperties();

		for (Property<?> entry : properties) {
			BuildProperty property = new BuildProperty();

			property.setName(entry.getName());

			Comparable<?> value = currentState.get(entry);

			try {
				if (currentBlock instanceof PillarBlock && property.getName().equals("axis")) {
					property.setValue(((Direction.Axis) value).asString());
				} else if (currentBlock instanceof CarpetBlock && property.getName().equals("color")) {
					DyeColor dyeColor = (DyeColor) value;
					property.setValue(dyeColor.asString());
				} else if (value instanceof StringIdentifiable) {
					StringIdentifiable stringSerializable = (StringIdentifiable) value;
					property.setValue(stringSerializable.asString());
				} else {
					property.setValue(value.toString());
				}
			} catch (Exception ex) {
				Prefab.logger.error("Unable to set property [" + property.getName() + "] to value [" + value + "] for Block [" + buildBlock.getBlockDomain() + ":" + buildBlock.getBlockName() + "].");
				throw ex;
			}

			buildBlock.getProperties().add(property);
		}

		return buildBlock;
	}

	public static PositionOffset getStartingPositionFromOriginalAndCurrentPosition(BlockPos currentPos, BlockPos originalPos) {
		// if (currentPos.getX() > originalPos.getX()). currentPos is "East"
		// of hitBlock
		// if (currentPos.getZ() > originalPos.getZ()). currentPos is
		// "South" of hitBlock
		PositionOffset positionOffSet = new PositionOffset();

		if (currentPos.getX() > originalPos.getX()) {
			positionOffSet.setEastOffset(currentPos.getX() - originalPos.getX());
		} else {
			positionOffSet.setWestOffset(originalPos.getX() - currentPos.getX());
		}

		if (currentPos.getZ() > originalPos.getZ()) {
			positionOffSet.setSouthOffset(currentPos.getZ() - originalPos.getZ());
		} else {
			positionOffSet.setNorthOffset(originalPos.getZ() - currentPos.getZ());
		}

		positionOffSet.setHeightOffset(currentPos.getY() - originalPos.getY());

		return positionOffSet;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		this.name = value;
	}

	public BuildClear getClearSpace() {
		return this.clearSpace;
	}

	public void setClearSpace(BuildClear value) {
		this.clearSpace = value;
	}

	public ArrayList<BuildBlock> getBlocks() {
		return this.blocks;
	}

	public void setBlocks(ArrayList<BuildBlock> value) {
		this.blocks = value;
	}

	public void Initialize() {
		this.name = "";
		this.clearSpace = new BuildClear();
		this.blocks = new ArrayList<>();
	}

	/**
	 * This is the main building method for this structure.
	 *
	 * @param configuration The configuration the user updated.
	 * @param world         The current world.
	 * @param originalPos   The block the user clicked on.
	 * @param assumedNorth  This should always be "NORTH" when the file is based on a scan.
	 * @param player        The player requesting the structure.
	 * @return True if the build can occur, otherwise false.
	 */
	public boolean BuildStructure(StructureConfiguration configuration, ServerWorld world, BlockPos originalPos, Direction assumedNorth, PlayerEntity player) {
		BlockPos startBlockPos = this.clearSpace.getStartingPosition().getRelativePosition(originalPos, this.clearSpace.getShape().getDirection(), configuration.houseFacing);
		BlockPos endBlockPos = startBlockPos.offset(configuration.houseFacing.rotateYCounterclockwise(), this.clearSpace.getShape().getWidth() - 1)
				.offset(configuration.houseFacing.getOpposite(), this.clearSpace.getShape().getWidth() - 1)
				.offset(Direction.UP, this.clearSpace.getShape().getHeight());

		// Make sure this structure can be placed here.
		Triple<Boolean, BlockState, BlockPos> checkResult = BuildingMethods.CheckBuildSpaceForAllowedBlockReplacement(world, startBlockPos, endBlockPos, player);

		if (!checkResult.getFirst()) {
			// Send a message to the player saying that the structure could not
			// be built.
			TranslatableText message = new TranslatableText(
					GuiLangKeys.GUI_STRUCTURE_NOBUILD,
					Registry.BLOCK.getId(checkResult.getSecond().getBlock()).toString(),
					checkResult.getThird().getX(),
					checkResult.getThird().getY(),
					checkResult.getThird().getZ());

			message.setStyle(Style.EMPTY.withColor(Formatting.GREEN));
			player.sendMessage(message, true);
			return false;
		}

		if (!this.BeforeBuilding(configuration, world, originalPos, assumedNorth, player)) {
			// First, clear the area where the structure will be built.
			this.ClearSpace(configuration, world, originalPos, assumedNorth);

			boolean blockPlacedWithCobbleStoneInstead = false;

			// Now place all of the blocks.
			for (BuildBlock block : this.getBlocks()) {
				Block foundBlock = Registry.BLOCK.get(block.getResourceLocation());

				if (foundBlock != null) {
					BlockState blockState = foundBlock.getDefaultState();
					BuildBlock subBlock = null;

					// Check if water should be replaced with cobble.
					if (!this.WaterReplacedWithCobbleStone(configuration, block, world, originalPos, assumedNorth, foundBlock, blockState, player)
							&& !this.CustomBlockProcessingHandled(configuration, block, world, originalPos, assumedNorth, foundBlock, blockState, player)) {
						block = BuildBlock.SetBlockState(configuration, world, originalPos, assumedNorth, block, foundBlock, blockState, this);

						if (block.getSubBlock() != null) {
							foundBlock = Registry.BLOCK.get(block.getSubBlock().getResourceLocation());
							blockState = foundBlock.getDefaultState();

							subBlock = BuildBlock.SetBlockState(configuration, world, originalPos, assumedNorth, block.getSubBlock(), foundBlock, blockState, this);
						}

						if (subBlock != null) {
							block.setSubBlock(subBlock);
						}

						boolean priorityThreeBlock = foundBlock instanceof TorchBlock
								|| foundBlock instanceof AbstractSignBlock
								|| foundBlock instanceof LeverBlock
								|| foundBlock instanceof AbstractButtonBlock
								|| foundBlock instanceof BedBlock
								|| foundBlock instanceof CarpetBlock
								|| foundBlock instanceof FlowerPotBlock
								|| foundBlock instanceof SugarCaneBlock
								|| foundBlock instanceof AbstractPressurePlateBlock
								|| foundBlock instanceof DoorBlock
								|| foundBlock instanceof LadderBlock
								|| foundBlock instanceof VineBlock
								|| foundBlock instanceof RedstoneWireBlock
								|| foundBlock instanceof AbstractRedstoneGateBlock
								|| foundBlock instanceof AbstractBannerBlock
								|| foundBlock instanceof LanternBlock
								|| foundBlock instanceof MushroomBlock;

						if (!block.getHasFacing()) {
							if (subBlock != null) {
								block.setSubBlock(subBlock);
							}

							if (priorityThreeBlock) {
								this.priorityThreeBlocks.add(block);
							} else if (foundBlock instanceof AirBlock) {
								this.airBlocks.add(block);
							} else if (foundBlock instanceof BlockEntityProvider) {
								this.priorityTwoBlocks.add(block);
							} else {
								this.priorityOneBlocks.add(block);
							}
						} else {
							// These blocks may be attached to other facing blocks and must be done later.
							if (priorityThreeBlock) {
								this.priorityThreeBlocks.add(block);
							} else {
								this.priorityTwoBlocks.add(block);
							}
						}
					}
				} else {
					// Cannot find this block in the registry. This can happen if a structure file has a mod block that
					// no longer exists.
					// In this case, print an informational message and replace it with cobblestone.
					String blockTypeNotFound = block.getResourceLocation().toString();
					block = BuildBlock.SetBlockState(configuration, world, originalPos, assumedNorth, block, Blocks.COBBLESTONE, Blocks.COBBLESTONE.getDefaultState(), this);
					this.priorityOneBlocks.add(block);

					if (!blockPlacedWithCobbleStoneInstead) {
						blockPlacedWithCobbleStoneInstead = true;
						Prefab.logger
								.warn("A Block was in the structure, but it is not registred. This block was replaced with vanilla cobblestone instead. Block type not found: ["
										+ blockTypeNotFound + "]");
					}
				}
			}

			this.configuration = configuration;
			this.world = world;
			this.assumedNorth = assumedNorth;
			this.originalPos = originalPos;

			if (StructureEventHandler.structuresToBuild.containsKey(player)) {
				StructureEventHandler.structuresToBuild.get(player).add(this);
			} else {
				ArrayList<Structure> structures = new ArrayList<Structure>();
				structures.add(this);
				StructureEventHandler.structuresToBuild.put(player, structures);
			}
		}

		return true;
	}

	/**
	 * This method is to process before a clear space block is set to air.
	 *
	 * @param pos The block position being processed.
	 */
	public void BeforeClearSpaceBlockReplaced(BlockPos pos) {
	}

	public void BeforeHangingEntityRemoved(AbstractDecorationEntity hangingEntity) {
	}

	public BlockState getStainedGlassBlock(DyeColor color) {
		switch (color) {
			case BLACK: {
				return Blocks.BLACK_STAINED_GLASS.getDefaultState();
			}
			case BLUE: {
				return Blocks.BLUE_STAINED_GLASS.getDefaultState();
			}
			case BROWN: {
				return Blocks.BROWN_STAINED_GLASS.getDefaultState();
			}
			case GRAY: {
				return Blocks.GRAY_STAINED_GLASS.getDefaultState();
			}
			case GREEN: {
				return Blocks.GREEN_STAINED_GLASS.getDefaultState();
			}
			case LIGHT_BLUE: {
				return Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState();
			}
			case LIGHT_GRAY: {
				return Blocks.LIGHT_GRAY_STAINED_GLASS.getDefaultState();
			}
			case LIME: {
				return Blocks.LIME_STAINED_GLASS.getDefaultState();
			}
			case MAGENTA: {
				return Blocks.MAGENTA_STAINED_GLASS.getDefaultState();
			}
			case ORANGE: {
				return Blocks.ORANGE_STAINED_GLASS.getDefaultState();
			}
			case PINK: {
				return Blocks.PINK_STAINED_GLASS.getDefaultState();
			}
			case PURPLE: {
				return Blocks.PURPLE_STAINED_GLASS.getDefaultState();
			}
			case RED: {
				return Blocks.RED_STAINED_GLASS.getDefaultState();
			}
			case WHITE: {
				return Blocks.WHITE_STAINED_GLASS.getDefaultState();
			}
			case YELLOW: {
				return Blocks.YELLOW_STAINED_GLASS.getDefaultState();
			}
			default: {
				return Blocks.CYAN_STAINED_GLASS.getDefaultState();
			}
		}
	}

	public BlockState getStainedGlassPaneBlock(DyeColor color) {
		switch (color) {
			case BLACK: {
				return Blocks.BLACK_STAINED_GLASS_PANE.getDefaultState();
			}
			case BLUE: {
				return Blocks.BLUE_STAINED_GLASS_PANE.getDefaultState();
			}
			case BROWN: {
				return Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState();
			}
			case GRAY: {
				return Blocks.GRAY_STAINED_GLASS_PANE.getDefaultState();
			}
			case GREEN: {
				return Blocks.GREEN_STAINED_GLASS_PANE.getDefaultState();
			}
			case LIGHT_BLUE: {
				return Blocks.LIGHT_BLUE_STAINED_GLASS_PANE.getDefaultState();
			}
			case LIGHT_GRAY: {
				return Blocks.LIGHT_GRAY_STAINED_GLASS_PANE.getDefaultState();
			}
			case LIME: {
				return Blocks.LIME_STAINED_GLASS_PANE.getDefaultState();
			}
			case MAGENTA: {
				return Blocks.MAGENTA_STAINED_GLASS_PANE.getDefaultState();
			}
			case ORANGE: {
				return Blocks.ORANGE_STAINED_GLASS_PANE.getDefaultState();
			}
			case PINK: {
				return Blocks.PINK_STAINED_GLASS_PANE.getDefaultState();
			}
			case PURPLE: {
				return Blocks.PURPLE_STAINED_GLASS_PANE.getDefaultState();
			}
			case RED: {
				return Blocks.RED_STAINED_GLASS_PANE.getDefaultState();
			}
			case WHITE: {
				return Blocks.WHITE_STAINED_GLASS_PANE.getDefaultState();
			}
			case YELLOW: {
				return Blocks.YELLOW_STAINED_GLASS_PANE.getDefaultState();
			}
			default: {
				return Blocks.CYAN_STAINED_GLASS_PANE.getDefaultState();
			}
		}
	}

	/**
	 * This method is used before any building occurs to check for things or possibly pre-build locations. Note: This is
	 * even done before blocks are cleared.
	 *
	 * @param configuration The structure configuration.
	 * @param world         The current world.
	 * @param originalPos   The original position clicked on.
	 * @param assumedNorth  The assumed northern direction.
	 * @param player        The player which initiated the construction.
	 * @return False if processing should continue, otherwise true to cancel processing.
	 */
	protected boolean BeforeBuilding(StructureConfiguration configuration, World world, BlockPos originalPos, Direction assumedNorth, PlayerEntity player) {
		return false;
	}

	/**
	 * This method is used after the main building is build for any additional structures or modifications.
	 *
	 * @param configuration The structure configuration.
	 * @param world         The current world.
	 * @param originalPos   The original position clicked on.
	 * @param assumedNorth  The assumed northern direction.
	 * @param player        The player which initiated the construction.
	 */
	public void AfterBuilding(StructureConfiguration configuration, ServerWorld world, BlockPos originalPos, Direction assumedNorth, PlayerEntity player) {
	}

	protected void ClearSpace(StructureConfiguration configuration, World world, BlockPos originalPos, Direction assumedNorth) {
		if (this.clearSpace.getShape().getWidth() > 0
				&& this.clearSpace.getShape().getLength() > 0) {
			BlockPos startBlockPos = this.clearSpace.getStartingPosition().getRelativePosition(originalPos, this.clearSpace.getShape().getDirection(), configuration.houseFacing);

			BlockPos endBlockPos = startBlockPos
					.offset(configuration.houseFacing.getOpposite().rotateYClockwise(), this.clearSpace.getShape().getWidth() - 1)
					.offset(configuration.houseFacing.getOpposite(), this.clearSpace.getShape().getLength() - 1)
					.offset(Direction.UP, this.clearSpace.getShape().getHeight());

			this.clearedBlockPos = new ArrayList<>();

			for (BlockPos pos : BlockPos.iterate(startBlockPos, endBlockPos)) {

				if (this.BlockShouldBeClearedDuringConstruction(configuration, world, originalPos, assumedNorth, pos)) {
					this.clearedBlockPos.add(new BlockPos(pos));
					this.allBlockPositions.add(new BlockPos(pos));
				}
			}
		} else {
			this.clearedBlockPos = new ArrayList<>();
		}
	}

	protected Boolean CustomBlockProcessingHandled(StructureConfiguration configuration, BuildBlock block, World world, BlockPos originalPos,
												   Direction assumedNorth, Block foundBlock, BlockState blockState, PlayerEntity player) {
		return false;
	}

	protected Boolean BlockShouldBeClearedDuringConstruction(StructureConfiguration configuration, World world, BlockPos originalPos, Direction assumedNorth, BlockPos blockPos) {
		return true;
	}

	/**
	 * Determines if a water block was replaced with cobblestone because this structure was built in the nether or the
	 * end.
	 *
	 * @param configuration The structure configuration.
	 * @param block         The build block object.
	 * @param world         The workd object.
	 * @param originalPos   The original block position this structure was built on.
	 * @param assumedNorth  The assumed north direction (typically north).
	 * @param foundBlock    The actual block found at the current location.
	 * @param blockState    The block state to set for the current block.
	 * @param player        The player requesting this build.
	 * @return Returns true if the water block was replaced by cobblestone, otherwise false.
	 */
	protected Boolean WaterReplacedWithCobbleStone(StructureConfiguration configuration, BuildBlock block, World world, BlockPos originalPos,
												   Direction assumedNorth, Block foundBlock, BlockState blockState, PlayerEntity player) {
		// Replace water blocks with cobblestone when this is not the overworld.
		if (foundBlock instanceof FluidBlock && blockState.getMaterial() == Material.WATER
				&& (!World.OVERWORLD.getValue().toString().equals(world.getRegistryKey().getValue().toString()))) {
			Identifier cobbleIdentifier = Registry.BLOCK.getId(Blocks.COBBLESTONE);
			block.setBlockDomain(cobbleIdentifier.getNamespace());
			block.setBlockName(cobbleIdentifier.getPath());
			block.setBlockState(Blocks.COBBLESTONE.getDefaultState());

			// Add this as a priority 3 block since it should be done at the end.
			this.priorityThreeBlocks.add(block);
			return true;
		}

		return false;
	}
}