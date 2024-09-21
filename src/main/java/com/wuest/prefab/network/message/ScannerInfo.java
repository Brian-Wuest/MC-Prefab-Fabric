package com.wuest.prefab.network.message;

import com.wuest.prefab.config.StructureScannerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public class ScannerInfo{
    private int blocksToTheLeft = 0;
    private int blocksParallel = 1;
    private int blocksDown = 0;
    private int blocksWide = 1;
    private int blocksLong = 1;
    private int blocksTall = 1;
    private String structureZipName = "";
    private Direction direction = Direction.NORTH;
    private @Nullable BlockPos blockPos = null;

    public ScannerInfo(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readInt(), friendlyByteBuf.readInt(), friendlyByteBuf.readInt(), friendlyByteBuf.readInt(),friendlyByteBuf.readInt(),
                friendlyByteBuf.readInt(), friendlyByteBuf.readUtf(),  friendlyByteBuf.readEnum(Direction.class),friendlyByteBuf.readNullable(BlockPos.STREAM_CODEC));
    }

    public ScannerInfo(StructureScannerConfig structureScannerConfig) {
        this.blocksToTheLeft = structureScannerConfig.blocksToTheLeft;
        this.blocksParallel = structureScannerConfig.blocksParallel;
        this.blocksDown = structureScannerConfig.blocksDown;
        this.blocksWide = structureScannerConfig.blocksWide;
        this.blocksLong = structureScannerConfig.blocksLong;
        this.blocksTall = structureScannerConfig.blocksTall;
        this.structureZipName = structureScannerConfig.structureZipName;
        this.direction = structureScannerConfig.direction;
        this.blockPos = structureScannerConfig.blockPos;
    }

    public ScannerInfo(int blocksToTheLeft,
                       int blocksParallel,
                       int blocksDown,
                       int blocksWide,
                       int blocksLong,
                       int blocksTall,
                       String structureZipName,
                       Direction direction,
                       @Nullable BlockPos blockPos) {
        this.blocksToTheLeft = blocksToTheLeft;
        this.blocksParallel = blocksParallel;
        this.blocksDown = blocksDown;
        this.blocksWide = blocksWide;
        this.blocksLong = blocksLong;
        this.blocksTall = blocksTall;
        this.structureZipName = structureZipName;
        this.direction = direction;
        this.blockPos = blockPos;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.blocksToTheLeft);
        buf.writeInt(this.blocksParallel);
        buf.writeInt(this.blocksDown);
        buf.writeInt(this.blocksWide);
        buf.writeInt(this.blocksLong);
        buf.writeInt(this.blocksTall);
        buf.writeUtf(this.structureZipName);
        buf.writeEnum(this.direction);
        buf.writeBlockPos(this.blockPos);
    }

    public StructureScannerConfig ToConfig() {
        StructureScannerConfig config = new StructureScannerConfig();
        config.blockPos = this.blockPos;
        config.blocksLong = this.blocksLong;
        config.blocksParallel = this.blocksParallel;
        config.blocksToTheLeft = this.blocksToTheLeft;
        config.blocksDown = this.blocksDown;
        config.blocksTall = this.blocksTall;
        config.blocksWide = this.blocksWide;
        config.direction = this.direction;
        config.structureZipName = this.structureZipName;

        return config;
    }
}