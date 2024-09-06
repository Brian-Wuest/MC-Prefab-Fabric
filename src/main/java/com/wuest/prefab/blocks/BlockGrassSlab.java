package com.wuest.prefab.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class BlockGrassSlab extends SlabBlock {
    public BlockGrassSlab() {
        super(FabricBlockSettings
                .ofFullCopy(Blocks.GRASS_BLOCK)
                .mapColor(MapColor.GRASS)
                .sound(SoundType.GRASS)
                .strength(0.5f, 0.5f));

    }
}
