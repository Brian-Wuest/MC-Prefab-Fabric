package com.wuest.prefab.structures.config;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.structures.predefined.StructureFishPond;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * @author WuestMan
 */
public class FishPondConfiguration extends StructureConfiguration {
	/**
	 * Custom method to read the NBTTagCompound message.
	 *
	 * @param messageTag The message to create the configuration from.
	 * @return An new configuration object with the values derived from the NBTTagCompound.
	 */
	@Override
	public FishPondConfiguration ReadFromCompoundNBT(CompoundTag messageTag) {
		FishPondConfiguration config = new FishPondConfiguration();

		return (FishPondConfiguration) super.ReadFromCompoundNBT(messageTag, config);
	}

	/**
	 * This is used to actually build the structure as it creates the structure instance and calls build structure.
	 *
	 * @param player      The player which requested the build.
	 * @param world       The world instance where the build will occur.
	 * @param hitBlockPos This hit block position.
	 */
	@Override
	protected void ConfigurationSpecificBuildStructure(PlayerEntity player, ServerWorld world, BlockPos hitBlockPos) {
		StructureFishPond structure = StructureFishPond.CreateInstance(StructureFishPond.ASSETLOCATION, StructureFishPond.class);

		if (structure.BuildStructure(this, world, hitBlockPos, Direction.NORTH, player)) {
			this.RemoveStructureItemFromPlayer(player, ModRegistry.FishPond);
		}
	}
}
