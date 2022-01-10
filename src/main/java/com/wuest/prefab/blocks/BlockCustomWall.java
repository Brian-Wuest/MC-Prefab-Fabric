package com.wuest.prefab.blocks;


import com.wuest.prefab.ModRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.WallBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class BlockCustomWall extends WallBlock implements IGrassSpreadable {
    public BlockCustomWall.EnumType BlockVariant;

    public BlockCustomWall(Block modelBlock, BlockCustomWall.EnumType variant) {
        super(FabricBlockSettings.of(Material.AGGREGATE)
                .strength(modelBlock.getHardness(),
                        modelBlock.getBlastResistance() * 5.0F / 3.0F)
                .sounds(modelBlock.getSoundGroup(modelBlock.getDefaultState())));

        this.BlockVariant = variant;
    }

    /**
     * Returns whether or not this block is of a type that needs random ticking.
     * Called for ref-counting purposes by ExtendedBlockStorage in order to broadly
     * cull a chunk from the random chunk update list for efficiency's sake.
     */
    @Override
    public boolean hasRandomTicks(BlockState state) {
        return this.BlockVariant == EnumType.DIRT;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        this.DetermineGrassSpread(state, worldIn, pos, random);
    }

    @Override
    public BlockState getGrassBlockState(BlockState originalState) {
        return ModRegistry.GrassWall.getDefaultState()
                .with(WallBlock.EAST_SHAPE, originalState.get(WallBlock.EAST_SHAPE))
                .with(WallBlock.WEST_SHAPE, originalState.get(WallBlock.WEST_SHAPE))
                .with(WallBlock.NORTH_SHAPE, originalState.get(WallBlock.NORTH_SHAPE))
                .with(WallBlock.SOUTH_SHAPE, originalState.get(WallBlock.SOUTH_SHAPE))
                .with(WallBlock.WATERLOGGED, originalState.get(WallBlock.WATERLOGGED))
                .with(WallBlock.UP, originalState.get(WallBlock.UP));
    }

    public enum EnumType implements StringIdentifiable {
        DIRT(0, "block_dirt_wall", "block_dirt_wall", Material.AGGREGATE),
        GRASS(1, "block_grass_wall", "block_grass_wall", Material.AGGREGATE);

        private static final BlockCustomWall.EnumType[] META_LOOKUP = new BlockCustomWall.EnumType[values().length];

        static {
            for (BlockCustomWall.EnumType customwall$enumtype : values()) {
                META_LOOKUP[customwall$enumtype.getMetadata()] = customwall$enumtype;
            }
        }

        private final int meta;
        private final String name;
        private String unlocalizedName;
        private Material material;

        EnumType(int meta, String name, String unlocalizedName, Material blockMaterial) {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
            this.material = blockMaterial;
        }

        public static BlockCustomWall.EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public int getMetadata() {
            return this.meta;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public Material getMaterial() {
            return this.material;
        }

        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }
    }
}