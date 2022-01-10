package com.wuest.prefab.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.StringIdentifiable;

/**
 * Provides a way to store large amounts of stone.
 *
 * @author WuestMan
 */
public class BlockCompressedStone extends Block {
	public final EnumType typeofStone;

	/**
	 * Initializes a new instance of the CompressedStone class.
	 */
	public BlockCompressedStone(EnumType typeOfStone) {
		super(AbstractBlock.Settings.of(Material.AGGREGATE)
				.strength(1.5F, 10.0F)
				.sounds(typeOfStone.getSoundType())
				.luminance(value -> typeOfStone == EnumType.COMPRESSED_GLOWSTONE || typeOfStone == EnumType.DOUBLE_COMPRESSED_GLOWSTONE ? 15 : 0));

		this.typeofStone = typeOfStone;
	}

	/**
	 * An enum which contains the various types of block variants.
	 *
	 * @author WuestMan
	 */
	@SuppressWarnings({"NullableProblems", "SpellCheckingInspection"})
	public enum EnumType implements StringIdentifiable {
		COMPRESSED_STONE(0, "block_compressed_stone", "block_compressed_stone", BlockSoundGroup.STONE),
		DOUBLE_COMPRESSED_STONE(1, "block_double_compressed_stone", "block_double_compressed_stone", BlockSoundGroup.STONE),
		TRIPLE_COMPRESSED_STONE(2, "block_triple_compressed_stone", "block_triple_compressed_stone", BlockSoundGroup.STONE),
		COMPRESSED_GLOWSTONE(3, "block_compressed_glowstone", "block_compressed_glowstone", BlockSoundGroup.GLASS),
		DOUBLE_COMPRESSED_GLOWSTONE(4, "block_double_compressed_glowstone", "block_double_compressed_glowstone", BlockSoundGroup.GLASS),
		COMPRESSED_DIRT(5, "block_compressed_dirt", "block_compressed_dirt", BlockSoundGroup.GRAVEL),
		DOUBLE_COMPRESSED_DIRT(6, "block_double_compressed_dirt", "block_double_compressed_dirt", BlockSoundGroup.GRAVEL);

		private final int meta;
		/**
		 * The EnumType's name.
		 */
		private final String name;
		private final String unlocalizedName;

		private final BlockSoundGroup soundType;

		EnumType(int meta, String name, String unlocalizedName, BlockSoundGroup soundType) {
			this.meta = meta;
			this.name = name;
			this.unlocalizedName = unlocalizedName;
			this.soundType = soundType;
		}

		/**
		 * The EnumType's meta data value.
		 *
		 * @return the meta data for this block.
		 */
		public int getMetadata() {
			return this.meta;
		}

		public BlockSoundGroup getSoundType() {
			return this.soundType;
		}

		/**
		 * Gets the name of this enum value.
		 */
		public String toString() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}

		/**
		 * The unlocalized name of this EnumType.
		 *
		 * @return A string containing the unlocalized name.
		 */
		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}
	}
}
