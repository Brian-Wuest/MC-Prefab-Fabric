package com.wuest.prefab.structures.events;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.Utils;
import com.wuest.prefab.config.EntityPlayerConfiguration;
import com.wuest.prefab.config.ModConfiguration;
import com.wuest.prefab.network.message.PlayerEntityTagMessage;
import com.wuest.prefab.structures.base.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.chunk.Chunk;

import java.util.*;
import java.util.Map.Entry;

/**
 * This is the structure event handler.
 *
 * @author WuestMan
 */
public final class StructureEventHandler {
    /**
     * Contains a hashmap for the structures to build and for whom.
     */
    public static HashMap<PlayerEntity, ArrayList<Structure>> structuresToBuild = new HashMap<>();

    public static ArrayList<Tuple<Structure, BuildEntity>> entitiesToGenerate = new ArrayList<>();

    public static int ticksSinceLastEntitiesGenerated = 0;

    public static void registerStructureServerSideEvents() {
        StructureEventHandler.playerJoinedServer();

        StructureEventHandler.serverStarted();

        StructureEventHandler.serverStopped();

        StructureEventHandler.serverTick();
    }

    private static void playerJoinedServer() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (entity instanceof ServerPlayerEntity) {
                StructureEventHandler.playerLoggedIn((ServerPlayerEntity) entity, serverWorld);
            }
        });
    }

    private static void serverTick() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            StructureEventHandler.onServerTick();
        });
    }

    private static void serverStarted() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            EntityPlayerConfiguration.playerTagData.clear();
        });
    }

    private static void serverStopped() {
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
            EntityPlayerConfiguration.playerTagData.clear();
        });
    }

    /**
     * This event is used to determine if the player should be given the starting house item when they log in.
     */
    public static void playerLoggedIn(ServerPlayerEntity player, ServerWorld serverWorld) {
        EntityPlayerConfiguration playerConfig = EntityPlayerConfiguration.loadFromEntity(player);

        ModConfiguration.StartingItemOptions startingItem = Prefab.serverConfiguration.startingItem;

        if (!playerConfig.givenHouseBuilder && startingItem != null) {
            ItemStack stack = ItemStack.EMPTY;

            switch (startingItem) {
                case StartingHouse: {
                    stack = new ItemStack(ModRegistry.StartHouse);
                    break;
                }

                case ModerateHouse: {
                    stack = new ItemStack(ModRegistry.ModerateHouse);
                    break;
                }
            }

            if (!stack.isEmpty()) {
                System.out.println(player.getDisplayName().getString() + " joined the game for the first time. Giving them starting item.");

                player.getInventory().insertStack(stack);
                player.currentScreenHandler.sendContentUpdates();

                // Make sure to set the tag for this player; so they don't get the item again.
                playerConfig.givenHouseBuilder = true;

                //playerConfig.saveToCache(player);
            }
        }

        // Send the tag to the client.
        PlayerEntityTagMessage message = new PlayerEntityTagMessage();
        PacketByteBuf messagePacket = Utils.createMessageBuffer(playerConfig.createPlayerTag());
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ModRegistry.PlayerConfigSync, messagePacket);
    }

    /**
     * This event is primarily used to build 100 blocks for any queued structures for all players.
     */
    public static void onServerTick() {
        ArrayList<PlayerEntity> playersToRemove = new ArrayList<PlayerEntity>();

        StructureEventHandler.ticksSinceLastEntitiesGenerated++;

        if (StructureEventHandler.entitiesToGenerate.size() > 0) {
            StructureEventHandler.ticksSinceLastEntitiesGenerated++;

            if (StructureEventHandler.ticksSinceLastEntitiesGenerated > 40) {
                // Process any entities.
                StructureEventHandler.processStructureEntities();

                StructureEventHandler.ticksSinceLastEntitiesGenerated = 0;
            }
        }

        if (StructureEventHandler.structuresToBuild.size() > 0) {
            for (Entry<PlayerEntity, ArrayList<Structure>> entry : StructureEventHandler.structuresToBuild.entrySet()) {
                ArrayList<Structure> structuresToRemove = new ArrayList<>();

                // Build the first 100 blocks of each structure for this player.
                for (Structure structure : entry.getValue()) {
                    if (!structure.entitiesRemoved) {
                        // Go through each block and find any entities there. If there are any; kill them if they aren't players.
                        // If there is a player there...they will probably die anyways.....
                        for (BlockPos clearedPos : structure.clearedBlockPos) {
                            Box axisPos = VoxelShapes.fullCube().getBoundingBox().offset(clearedPos);

                            List<Entity> list = structure.world.getOtherEntities(null, axisPos);

                            if (!list.isEmpty()) {
                                for (Entity entity : list) {
                                    // Don't kill living entities.
                                    if (!(entity instanceof LivingEntity)) {
                                        if (entity instanceof AbstractDecorationEntity) {
                                            structure.BeforeHangingEntityRemoved((AbstractDecorationEntity) entity);
                                        }

                                        entity.remove(Entity.RemovalReason.DISCARDED);
                                    }
                                }
                            }
                        }

                        structure.entitiesRemoved = true;
                    }

                    if (structure.airBlocks.size() > 0) {
                        structure.hasAirBlocks = true;
                    }

                    for (int i = 0; i < 100; i++) {
                        i = StructureEventHandler.setBlock(i, structure, structuresToRemove);
                    }

                    // After building the blocks for this tick, find waterlogged blocks and remove them.
                    StructureEventHandler.removeWaterLogging(structure);
                }

                // Update the list of structures to remove this structure since it's done building.
                StructureEventHandler.removeStructuresFromList(structuresToRemove, entry);

                if (entry.getValue().size() == 0) {
                    playersToRemove.add(entry.getKey());
                }
            }
        }

        // Remove each player that has their structure's built.
        for (PlayerEntity player : playersToRemove) {
            StructureEventHandler.structuresToBuild.remove(player);
        }

    }

    private static int setBlock(int i, Structure structure, ArrayList<Structure> structuresToRemove) {
        // Structure clearing happens before anything else.
        // Don't bother clearing the area for water-based structures
        // Anything which should be air will be air
        if (structure.clearedBlockPos.size() > 0 && !structure.hasAirBlocks) {
            BlockPos currentPos = structure.clearedBlockPos.get(0);
            structure.clearedBlockPos.remove(0);

            BlockState clearBlockState = structure.world.getBlockState(currentPos);

            // If this block is not specifically air then set it to air.
            // This will also break other mod's logic blocks but they would probably be broken due to structure
            // generation anyways.
            if (clearBlockState.getMaterial() != Material.AIR) {
                structure.BeforeClearSpaceBlockReplaced(currentPos);

                for (Direction adjacentBlock : Direction.values()) {
                    BlockPos tempPos = currentPos.offset(adjacentBlock);
                    BlockState foundState = structure.world.getBlockState(tempPos);
                    Block foundBlock = foundState.getBlock();

                    // Check if this block is one that is attached to a facing, if it is, remove it first.
                    if (foundBlock instanceof TorchBlock
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
                            || foundBlock instanceof AbstractRailBlock) {
                        structure.BeforeClearSpaceBlockReplaced(currentPos);

                        if (!(foundBlock instanceof BedBlock)) {
                            structure.world.removeBlock(tempPos, false);
                        } else if (foundBlock instanceof DoorBlock) {
                            // Make sure to remove both parts before going on.
                            DoubleBlockHalf currentHalf = foundState.get(Properties.DOUBLE_BLOCK_HALF);

                            BlockPos otherHalfPos = currentHalf == DoubleBlockHalf.LOWER
                                    ? tempPos.up() : tempPos.down();

                            structure.world.setBlockState(tempPos, Blocks.AIR.getDefaultState(), 35);
                            structure.world.setBlockState(otherHalfPos, Blocks.AIR.getDefaultState(), 35);

                        } else {
                            // Found a bed, try to find the other part of the bed and remove it.
                            for (Direction currentDirection : Direction.values()) {
                                BlockPos bedPos = tempPos.offset(currentDirection);
                                BlockState bedState = structure.world.getBlockState(bedPos);

                                if (bedState.getBlock() instanceof BedBlock) {
                                    // found the other part of the bed. Remove the current block and this one.
                                    structure.world.setBlockState(tempPos, Blocks.AIR.getDefaultState(), 35);
                                    structure.world.setBlockState(bedPos, Blocks.AIR.getDefaultState(), 35);
                                    break;
                                }
                            }
                        }
                    }
                }

                structure.world.removeBlock(currentPos, false);
            } else {
                // This is just an air block, move onto the next block don't need to wait for the next tick.
                i--;
            }

            return i;
        }

        BuildBlock currentBlock = null;

        if (structure.priorityOneBlocks.size() > 0) {
            currentBlock = structure.priorityOneBlocks.get(0);
            structure.priorityOneBlocks.remove(0);
        } else {
            // There are no more blocks to set.
            structuresToRemove.add(structure);
            return 999;
        }

        BlockState state = currentBlock.getBlockState();

        BlockPos setBlockPos = currentBlock.getStartingPosition().getRelativePosition(structure.originalPos,
                structure.getClearSpace().getShape().getDirection(), structure.configuration.houseFacing);

        BuildingMethods.ReplaceBlock(structure.world, setBlockPos, state, 2);

        // After placing the initial block, set the sub-block. This needs to happen as the list isn't always in the
        // correct order.
        if (currentBlock.getSubBlock() != null) {
            BuildBlock subBlock = currentBlock.getSubBlock();

            BuildingMethods.ReplaceBlock(structure.world, subBlock.getStartingPosition().getRelativePosition(structure.originalPos,
                    structure.getClearSpace().getShape().getDirection(), structure.configuration.houseFacing), subBlock.getBlockState());
        }

        return i;
    }

    private static void removeStructuresFromList(ArrayList<Structure> structuresToRemove, Entry<PlayerEntity, ArrayList<Structure>> entry) {
        for (Structure structure : structuresToRemove) {
            StructureEventHandler.removeWaterLogging(structure);

            for (BuildEntity buildEntity : structure.entities) {
                Optional<EntityType<?>> entityType = EntityType.get(buildEntity.getEntityResourceString());

                if (entityType.isPresent()) {
                    StructureEventHandler.entitiesToGenerate.add(new Tuple<>(structure, buildEntity));
                }
            }

            // This structure is done building. Do any post-building operations.
            entry.getValue().remove(structure);
        }
    }

    private static void processStructureEntities() {
        for (Tuple<Structure, BuildEntity> entityRecords : StructureEventHandler.entitiesToGenerate) {
            BuildEntity buildEntity = entityRecords.second;
            Structure structure = entityRecords.first;

            Optional<EntityType<?>> entityType = EntityType.get(buildEntity.getEntityResourceString());

            if (entityType.isPresent()) {
                Entity entity = entityType.get().create(structure.world);

                if (entity != null) {
                    NbtCompound tagCompound = buildEntity.getEntityDataTag();
                    BlockPos entityPos = buildEntity.getStartingPosition().getRelativePosition(structure.originalPos,
                            structure.getClearSpace().getShape().getDirection(), structure.configuration.houseFacing);

                    if (tagCompound != null) {
                        if (tagCompound.containsUuid("UUID")) {
                            tagCompound.putUuid("UUID", UUID.randomUUID());
                        }

                        NbtList nbttaglist = new NbtList();
                        nbttaglist.add(NbtDouble.of(entityPos.getX()));
                        nbttaglist.add(NbtDouble.of(entityPos.getY()));
                        nbttaglist.add(NbtDouble.of(entityPos.getZ()));
                        tagCompound.put("Pos", nbttaglist);

                        entity.readNbt(tagCompound);
                    }

                    // Set item frame facing and rotation here.
                    if (entity instanceof ItemFrameEntity) {
                        entity = StructureEventHandler.setItemFrameFacingAndRotation((ItemFrameEntity) entity, buildEntity, entityPos, structure);
                    } else if (entity instanceof PaintingEntity) {
                        entity = StructureEventHandler.setPaintingFacingAndRotation((PaintingEntity) entity, buildEntity, entityPos, structure);
                    } else if (entity instanceof AbstractMinecartEntity) {
                        // Minecarts need to be slightly higher to account for the rails; otherwise they will fall through the rail and the block below the rail.
                        buildEntity.entityYAxisOffset = buildEntity.entityYAxisOffset + .2;
                        entity = StructureEventHandler.setEntityFacingAndRotation(entity, buildEntity, entityPos, structure);
                    } else {
                        // All other entities
                        entity = StructureEventHandler.setEntityFacingAndRotation(entity, buildEntity, entityPos, structure);
                    }

                    structure.world.spawnEntity(entity);
                }
            }
        }

        // All entities generated; clear out the list.
        StructureEventHandler.entitiesToGenerate.clear();
    }

    private static void removeWaterLogging(Structure structure) {
        if (structure.hasAirBlocks) {
            for (BlockPos currentPos : structure.allBlockPositions) {
                BlockState currentState = structure.world.getBlockState(currentPos);

                if (currentState.contains(Properties.WATERLOGGED)) {
                    // This is a water loggable block and there were air blocks, make sure that it's no longer water logged.
                    currentState = currentState.with((Properties.WATERLOGGED), false);
                    structure.world.setBlockState(currentPos, currentState, 3);
                } else if (currentState.getMaterial() == Material.WATER) {
                    structure.world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 3);

                }
            }
        }
    }

    private static Entity setPaintingFacingAndRotation(PaintingEntity entity, BuildEntity buildEntity, BlockPos entityPos, Structure structure) {
        float yaw = entity.getYaw();
        BlockRotation rotation = BlockRotation.NONE;
        double x_axis_offset = 0;
        double z_axis_offset = 0;
        Direction facing = entity.getHorizontalFacing();
        double y_axis_offset = buildEntity.entityYAxisOffset * -1;

        if (structure.configuration.houseFacing == structure.assumedNorth.getOpposite()) {
            rotation = BlockRotation.CLOCKWISE_180;
            facing = facing.getOpposite();
        } else if (structure.configuration.houseFacing == structure.assumedNorth.rotateYClockwise()) {
            rotation = BlockRotation.CLOCKWISE_90;

            if (structure.getClearSpace().getShape().getDirection() == Direction.NORTH) {
                facing = facing.rotateYCounterclockwise();
            } else if (structure.getClearSpace().getShape().getDirection() == Direction.SOUTH) {
                facing = facing.rotateYClockwise();
            }
        } else if (structure.configuration.houseFacing == structure.assumedNorth.rotateYCounterclockwise()) {
            rotation = BlockRotation.COUNTERCLOCKWISE_90;

            if (structure.getClearSpace().getShape().getDirection() == Direction.NORTH) {
                facing = facing.rotateYClockwise();
            } else if (structure.getClearSpace().getShape().getDirection() == Direction.SOUTH) {
                facing = facing.rotateYCounterclockwise();
            }
        }

        if (entity.motive.getHeight() > entity.motive.getWidth()
                || entity.motive.getHeight() > 16) {
            y_axis_offset--;
        }

        yaw = entity.applyRotation(rotation);

        AbstractDecorationEntity hangingEntity = entity;
        NbtCompound compound = new NbtCompound();
        hangingEntity.writeCustomDataToNbt(compound);
        compound.putByte("Facing", (byte) facing.getHorizontal());
        hangingEntity.writeCustomDataToNbt(compound);
        StructureEventHandler.updateEntityHangingBoundingBox(hangingEntity);

        entity.refreshPositionAndAngles(entityPos.getX() + x_axis_offset, entityPos.getY() + y_axis_offset, entityPos.getZ() + z_axis_offset, yaw,
                entity.getPitch());

        StructureEventHandler.updateEntityHangingBoundingBox(entity);
        Chunk chunk = structure.world.getChunk(entityPos);

        chunk.setShouldSave(true);

        return entity;
    }

    private static Entity setItemFrameFacingAndRotation(ItemFrameEntity frame, BuildEntity buildEntity, BlockPos entityPos, Structure structure) {
        float yaw = frame.getYaw();
        BlockRotation rotation = BlockRotation.NONE;
        double x_axis_offset = buildEntity.entityXAxisOffset;
        double z_axis_offset = buildEntity.entityZAxisOffset;
        Direction facing = frame.getHorizontalFacing();
        double y_axis_offset = buildEntity.entityYAxisOffset;
        x_axis_offset = x_axis_offset * -1;
        z_axis_offset = z_axis_offset * -1;

        if (facing != Direction.UP && facing != Direction.DOWN) {
            if (structure.configuration.houseFacing == structure.assumedNorth.getOpposite()) {
                rotation = BlockRotation.CLOCKWISE_180;
                facing = facing.getOpposite();
            } else if (structure.configuration.houseFacing == structure.assumedNorth.rotateYClockwise()) {
                if (structure.getClearSpace().getShape().getDirection() == Direction.NORTH) {
                    rotation = BlockRotation.CLOCKWISE_90;
                    facing = facing.rotateYCounterclockwise();
                } else if (structure.getClearSpace().getShape().getDirection() == Direction.SOUTH) {
                    facing = facing.rotateYCounterclockwise();
                    rotation = BlockRotation.CLOCKWISE_90;
                }
            } else if (structure.configuration.houseFacing == structure.assumedNorth.rotateYCounterclockwise()) {
                if (structure.getClearSpace().getShape().getDirection() == Direction.NORTH) {
                    rotation = BlockRotation.COUNTERCLOCKWISE_90;
                    facing = facing.rotateYClockwise();
                } else if (structure.getClearSpace().getShape().getDirection() == Direction.SOUTH) {
                    facing = facing.rotateYClockwise();
                    rotation = BlockRotation.COUNTERCLOCKWISE_90;
                }
            } else {
                x_axis_offset = 0;
                z_axis_offset = 0;
            }
        }

        yaw = frame.applyRotation(rotation);

        AbstractDecorationEntity hangingEntity = frame;
        NbtCompound compound = new NbtCompound();
        hangingEntity.writeCustomDataToNbt(compound);
        compound.putByte("Facing", (byte) facing.getId());
        hangingEntity.writeCustomDataToNbt(compound);
        StructureEventHandler.updateEntityHangingBoundingBox(hangingEntity);

        frame.refreshPositionAndAngles(entityPos.getX() + x_axis_offset, entityPos.getY() + y_axis_offset, entityPos.getZ() + z_axis_offset, yaw,
                frame.getPitch());

        StructureEventHandler.updateEntityHangingBoundingBox(frame);
        Chunk chunk = structure.world.getChunk(entityPos);

        chunk.setShouldSave(true);

        return frame;
    }

    private static Entity setEntityFacingAndRotation(Entity entity, BuildEntity buildEntity, BlockPos entityPos, Structure structure) {
        float yaw = entity.getYaw();
        BlockRotation rotation = BlockRotation.NONE;
        double x_axis_offset = buildEntity.entityXAxisOffset;
        double z_axis_offset = buildEntity.entityZAxisOffset;
        Direction facing = structure.assumedNorth;
        double y_axis_offset = buildEntity.entityYAxisOffset;

        if (structure.configuration.houseFacing == structure.assumedNorth.getOpposite()) {
            rotation = BlockRotation.CLOCKWISE_180;
            x_axis_offset = x_axis_offset * -1;
            z_axis_offset = z_axis_offset * -1;
            facing = facing.getOpposite();
        } else if (structure.configuration.houseFacing == structure.assumedNorth.rotateYClockwise()) {
            rotation = BlockRotation.CLOCKWISE_90;
            x_axis_offset = x_axis_offset * -1;
            z_axis_offset = z_axis_offset * -1;
            facing = facing.rotateYClockwise();
        } else if (structure.configuration.houseFacing == structure.assumedNorth.rotateYCounterclockwise()) {
            rotation = BlockRotation.COUNTERCLOCKWISE_90;
            x_axis_offset = x_axis_offset * -1;
            z_axis_offset = z_axis_offset * -1;
            facing = facing.rotateYCounterclockwise();
        } else {
            x_axis_offset = 0;
            z_axis_offset = 0;
        }

        yaw = entity.applyRotation(rotation);

        entity.refreshPositionAndAngles(entityPos.getX() + x_axis_offset, entityPos.getY() + y_axis_offset, entityPos.getZ() + z_axis_offset, yaw,
                entity.getPitch());

        return entity;
    }

    private static void updateEntityHangingBoundingBox(AbstractDecorationEntity entity) {
        double d0 = (double) entity.getDecorationBlockPos().getX() + 0.5D;
        double d1 = (double) entity.getDecorationBlockPos().getY() + 0.5D;
        double d2 = (double) entity.getDecorationBlockPos().getZ() + 0.5D;
        double d3 = 0.46875D;
        double d4 = entity.getWidthPixels() % 32 == 0 ? 0.5D : 0.0D;
        double d5 = entity.getHeightPixels() % 32 == 0 ? 0.5D : 0.0D;
        Direction horizontal = entity.getHorizontalFacing();
        d0 = d0 - (double) horizontal.getOffsetX() * 0.46875D;
        d2 = d2 - (double) horizontal.getOffsetZ() * 0.46875D;
        d1 = d1 + d5;
        Direction direction = horizontal == Direction.DOWN || horizontal == Direction.UP ? horizontal.getOpposite() : horizontal.rotateYCounterclockwise();
        d0 = d0 + d4 * (double) direction.getOffsetX();
        d2 = d2 + d4 * (double) direction.getOffsetZ();

        // The function call below set the following fields from the "entity" class. posX, posY, posZ.
        // This will probably have to change when the mappings get updated.
        entity.setPos(d0, d1, d2);
        double d6 = entity.getWidthPixels();
        double d7 = entity.getHeightPixels();
        double d8 = entity.getWidthPixels();

        if (horizontal.getAxis() == Direction.Axis.Z) {
            d8 = 1.0D;
        } else {
            d6 = 1.0D;
        }

        d6 = d6 / 32.0D;
        d7 = d7 / 32.0D;
        d8 = d8 / 32.0D;
        entity.setBoundingBox(new Box(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
    }
}
