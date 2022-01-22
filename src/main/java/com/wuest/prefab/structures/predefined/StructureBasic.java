package com.wuest.prefab.structures.predefined;

import com.wuest.prefab.Prefab;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.blocks.FullDyeColor;
import com.wuest.prefab.structures.base.BuildBlock;
import com.wuest.prefab.structures.base.BuildingMethods;
import com.wuest.prefab.structures.base.Structure;
import com.wuest.prefab.structures.config.BasicStructureConfiguration;
import com.wuest.prefab.structures.config.BasicStructureConfiguration.EnumBasicStructureName;
import com.wuest.prefab.structures.config.StructureConfiguration;
import com.wuest.prefab.structures.config.enums.AdvancedFarmOptions;
import com.wuest.prefab.structures.config.enums.BaseOption;
import com.wuest.prefab.structures.config.enums.ModerateFarmOptions;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * This is the basic structure to be used for structures which don't need a lot of configuration or a custom player
 * created structures.
 *
 * @author WuestMan
 */
public class StructureBasic extends Structure {
    private BlockPos customBlockPos = null;
    private final ArrayList<BlockPos> mobSpawnerPos = new ArrayList<>();
    private BlockPos signPosition = null;

    @Override
    protected Boolean CustomBlockProcessingHandled(StructureConfiguration configuration, BuildBlock block, World world, BlockPos originalPos,
                                                   Direction assumedNorth, Block foundBlock, BlockState blockState, PlayerEntity player) {
        BasicStructureConfiguration config = (BasicStructureConfiguration) configuration;

        String structureName = config.basicStructureName.getName();
        BaseOption chosenOption = config.chosenOption;

        if (foundBlock instanceof HopperBlock && structureName.equals(EnumBasicStructureName.ModerateFarm.getName()) && chosenOption == ModerateFarmOptions.AutomatedChickenCoop) {
            this.customBlockPos = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);
        } else if (foundBlock instanceof TrapdoorBlock && structureName.equals(EnumBasicStructureName.MineshaftEntrance.getName())) {
            this.customBlockPos = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);
        } else if (foundBlock == Blocks.SPONGE
                && structureName.equals(BasicStructureConfiguration.EnumBasicStructureName.WorkShop.getName())) {
            // Sponges are sometimes used in-place of trapdoors when trapdoors are used for decoration.
            this.customBlockPos = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing).up();
        } else if (foundBlock instanceof BedBlock && config.chosenOption.getHasBedColor()) {
            // Even if a structure has a bed; we may want to keep a specific color to match what the design of the structure is.
            BlockPos bedHeadPosition = block.getStartingPosition().getRelativePosition(originalPos, this.getClearSpace().getShape().getDirection(), configuration.houseFacing);
            BlockPos bedFootPosition = block.getSubBlock().getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);

            Tuple<BlockState, BlockState> blockStateTuple = BuildingMethods.getBedState(bedHeadPosition, bedFootPosition, config.bedColor);
            block.setBlockState(blockStateTuple.getFirst());
            block.getSubBlock().setBlockState(blockStateTuple.getSecond());

            this.priorityOneBlocks.add(block);
            return true;
        } else if (foundBlock instanceof SpawnerBlock && structureName.equals(EnumBasicStructureName.AdvancedFarm.getName()) && chosenOption == AdvancedFarmOptions.MonsterMasher
                && Prefab.serverConfiguration.includeSpawnersInMasher) {
            this.mobSpawnerPos.add(block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing));
        } else if (foundBlock instanceof AbstractSignBlock && structureName.equals(EnumBasicStructureName.AdvancedFarm.getName()) && chosenOption == AdvancedFarmOptions.MonsterMasher) {
            this.signPosition = block.getStartingPosition().getRelativePosition(
                    originalPos,
                    this.getClearSpace().getShape().getDirection(),
                    configuration.houseFacing);
        }



        return false;
    }

    @Override
    protected Boolean BlockShouldBeClearedDuringConstruction(StructureConfiguration configuration, World world, BlockPos originalPos, Direction assumedNorth, BlockPos blockPos) {
        BasicStructureConfiguration config = (BasicStructureConfiguration) configuration;

        if (config.basicStructureName.getName().equals(EnumBasicStructureName.AquaBase.getName())
                || config.basicStructureName.getName().equals(EnumBasicStructureName.AdvancedAquaBase.getName())) {
            BlockState blockState = world.getBlockState(blockPos);
            // Don't clear water blocks for this building.
            return blockState.getMaterial() != Material.WATER;
        }

        return true;
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
    @Override
    public void AfterBuilding(StructureConfiguration configuration, ServerWorld world, BlockPos originalPos, Direction assumedNorth, PlayerEntity player) {
        BasicStructureConfiguration config = (BasicStructureConfiguration) configuration;
        String structureName = config.basicStructureName.getName();
        BaseOption chosenOption = config.chosenOption;

        if (this.customBlockPos != null) {
            if (structureName.equals(EnumBasicStructureName.ModerateFarm.getName()) && chosenOption == ModerateFarmOptions.AutomatedChickenCoop) {
                // For the advanced chicken coop, spawn 4 chickens above the hopper.
                for (int i = 0; i < 4; i++) {
                    ChickenEntity entity = new ChickenEntity(EntityType.CHICKEN, world);
                    entity.setPos(this.customBlockPos.getX(), this.customBlockPos.up().getY(), this.customBlockPos.getZ());
                    world.spawnEntity(entity);
                }
            } else if (structureName.equals(EnumBasicStructureName.MineshaftEntrance.getName())
                    || structureName.equals(BasicStructureConfiguration.EnumBasicStructureName.WorkShop.getName())) {
                // Build the mineshaft where the trap door exists.
                BuildingMethods.PlaceMineShaft(world, this.customBlockPos.down(), configuration.houseFacing, true);
            }

            this.customBlockPos = null;
        }

        if (structureName.equals(EnumBasicStructureName.AdvancedFarm.getName()) && chosenOption == AdvancedFarmOptions.MonsterMasher) {
            int monstersPlaced = 0;

            // Set the spawner.
            for (BlockPos pos : this.mobSpawnerPos) {
                BlockEntity tileEntity = world.getBlockEntity(pos);

                if (tileEntity instanceof MobSpawnerBlockEntity) {
                    MobSpawnerBlockEntity spawner = (MobSpawnerBlockEntity) tileEntity;

                    switch (monstersPlaced) {
                        case 0: {
                            // Zombie.
                            spawner.getLogic().setEntityId(EntityType.ZOMBIE);
                            break;
                        }

                        case 1: {
                            // Skeleton.
                            spawner.getLogic().setEntityId(EntityType.SKELETON);
                            break;
                        }

                        case 2: {
                            // Spider.
                            spawner.getLogic().setEntityId(EntityType.SPIDER);
                            break;
                        }

                        default: {
                            // Creeper.
                            spawner.getLogic().setEntityId(EntityType.CREEPER);
                            break;
                        }
                    }

                    monstersPlaced++;
                }
            }

            if (this.signPosition != null) {
                BlockEntity tileEntity = world.getBlockEntity(this.signPosition);

                if (tileEntity instanceof SignBlockEntity) {
                    SignBlockEntity signTile = (SignBlockEntity) tileEntity;
                    signTile.setTextOnRow(0, new LiteralText("Lamp On=Mobs"));

                    signTile.setTextOnRow(2, new LiteralText("Lamp Off=No Mobs"));
                }
            }
        }

        if (structureName.equals(EnumBasicStructureName.AquaBase.getName())
                || structureName.equals(EnumBasicStructureName.AdvancedAquaBase.getName())) {
            // Replace the entrance area with air blocks.
            BlockPos airPos = originalPos.up(4).offset(configuration.houseFacing.getOpposite(), 1);

            // This is the first wall.
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            airPos = airPos.down();
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            airPos = airPos.down();
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            airPos = airPos.down();
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            // Second part of the wall.
            airPos = airPos.offset(configuration.houseFacing.getOpposite()).up();
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            airPos = airPos.up();
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYClockwise()), false);
            world.removeBlock(airPos, false);
            world.removeBlock(airPos.offset(configuration.houseFacing.rotateYCounterclockwise()), false);

            airPos = airPos.up();
            world.removeBlock(airPos, false);
        }
    }

    @Override
    protected boolean hasGlassColor(StructureConfiguration configuration) {
        BasicStructureConfiguration config = (BasicStructureConfiguration) configuration;
        BaseOption chosenOption = config.chosenOption;
        return chosenOption.getHasGlassColor();
    }

    @Override
    protected FullDyeColor getGlassColor(StructureConfiguration configuration) {
        BasicStructureConfiguration config = (BasicStructureConfiguration) configuration;
        return config.glassColor;
    }
}
