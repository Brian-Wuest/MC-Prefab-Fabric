package com.wuest.prefab.blocks.entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.base.TileEntityBase;
import com.wuest.prefab.config.block_entities.StructureScannerConfig;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.structures.base.BuildClear;
import com.wuest.prefab.structures.base.BuildShape;
import com.wuest.prefab.structures.base.Structure;
import com.wuest.prefab.structures.custom.base.CustomStructureInfo;
import com.wuest.prefab.structures.custom.base.ItemInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;

public class StructureScannerBlockEntity extends TileEntityBase<StructureScannerConfig> {
    public StructureScannerBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.StructureScannerEntityType, pos, state);

        this.config = new StructureScannerConfig();
        this.config.blockPos = pos;
    }

    public static void ScanShape(StructureScannerConfig config, ServerPlayer playerEntity, ServerLevel serverWorld) {
        BuildClear clearedSpace = new BuildClear();
        clearedSpace.getShape().setDirection(config.direction);
        clearedSpace.getShape().setHeight(config.blocksTall);
        clearedSpace.getShape().setWidth(config.blocksWide);
        clearedSpace.getShape().setLength(config.blocksLong);

        BuildShape buildShape = clearedSpace.getShape().Clone();

        Direction playerFacing = config.direction;

        // Scanning the structure doesn't contain the starting corner block but the clear does.
        buildShape.setWidth(buildShape.getWidth() - 1);
        buildShape.setLength(buildShape.getLength() - 1);

        clearedSpace.getShape().setWidth(clearedSpace.getShape().getWidth());
        clearedSpace.getShape().setLength(clearedSpace.getShape().getLength());

        // Down is inverse on the GUI so make sure that it's negative when saving to the file.
        clearedSpace.getStartingPosition().setHeightOffset(config.blocksDown);
        clearedSpace.getStartingPosition().setHorizontalOffset(playerFacing, config.blocksParallel);
        clearedSpace.getStartingPosition().setHorizontalOffset(playerFacing.getCounterClockWise(), config.blocksToTheLeft);

        BlockPos cornerPos = config.blockPos
                .relative(playerFacing.getCounterClockWise(), config.blocksToTheLeft)
                .relative(playerFacing, config.blocksParallel)
                .below(config.blocksDown);

        BlockPos otherCorner = cornerPos
                .relative(playerFacing, buildShape.getLength())
                .relative(playerFacing.getClockWise(), buildShape.getWidth())
                .above(buildShape.getHeight());

        String fileLocation;

        if (Prefab.isDebug) {
            fileLocation = "..\\src\\main\\resources\\assets\\prefab\\structures\\" + config.structureZipName + ".zip";
        } else {
            // Since this is a custom structure. Make sure this name doesn't already exist.
            Optional<CustomStructureInfo> existingInfo = ModRegistry.CustomStructures.stream().filter(x ->
                    x.displayName.equalsIgnoreCase(config.structureZipName) || x.structureFileName.equalsIgnoreCase(config.structureZipName + ".zip")).findAny();

            if (existingInfo.isPresent()) {
                // Found an existing custom structure with this display name or structure file name. Do not scan this structure.
                Prefab.logger.warn("Found duplicate custom structure with name: [" + config.structureZipName + "]");

                TranslatableComponent message = new TranslatableComponent(GuiLangKeys.DUPLICATE_STRUCTURE_SCAN);
                TextComponent textMessage = new TextComponent(message.getString() + config.structureZipName);
                playerEntity.sendMessage(textMessage, playerEntity.getUUID());

                return;
            }

            try {
                fileLocation = Prefab.customStructuresFolder.resolve(config.structureZipName + ".zip").toString();
            } catch (InvalidPathException exception) {
                Prefab.logger.error("The structure file name is invalid for the current operating system. The name: ["
                        + config.structureZipName + "] is invalid. See below for OS error message");

                Prefab.logger.error(exception);
                return;
            }
        }

        Structure.ScanStructure(
                serverWorld,
                config.blockPos,
                cornerPos,
                otherCorner,
                fileLocation,
                clearedSpace,
                playerFacing,
                false,
                false);

        if (!Prefab.isDebug) {
            // At this point the structure file is created.
            // Create the structure meta-data file so this item can be created.
            CustomStructureInfo customStructureInfo = new CustomStructureInfo();
            customStructureInfo.displayName = config.structureZipName;
            customStructureInfo.structureFileName = config.structureZipName + ".zip";
            customStructureInfo.hasBedColorOptions = config.hasBedColorOptions;

            customStructureInfo.hasGlassColorOptions = config.hasGlassColorOptions;

            ItemInfo baseItem = new ItemInfo();
            baseItem.item = Registry.BLOCK.getKey(Blocks.DIRT);
            baseItem.count = 1;
            customStructureInfo.requiredItems.add(baseItem);

            Path filePath = Prefab.configFolder.resolve(config.structureZipName + ".zip");

            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String textToWrite = gson.toJson(customStructureInfo);

            try {
                Files.writeString(filePath, textToWrite);
            } catch (IOException e) {
                Prefab.logger.error("There was an error writing the custom structure information file: ["
                        + config.structureZipName + ".zip]. See below for OS error");
                Prefab.logger.error(e);
                return;
            }

            // File wrote successfully. Add to the list of custom structures!
            ModRegistry.CustomStructures.add(customStructureInfo);
        }
    }
}
