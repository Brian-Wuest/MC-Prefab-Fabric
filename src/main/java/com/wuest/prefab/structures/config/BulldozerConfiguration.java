package com.wuest.prefab.structures.config;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.structures.predefined.StructureBulldozer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * @author WuestMan
 */
public class BulldozerConfiguration extends StructureConfiguration {

	public boolean creativeMode = false;

	/**
	 * Initializes a new instance of the {@link BulldozerConfiguration} class.
	 */
	public BulldozerConfiguration() {
	}

	/**
	 * Custom method to read the NBTTagCompound message.
	 *
	 * @param messageTag The message to create the configuration from.
	 * @return An new configuration object with the values derived from the NBTTagCompound.
	 */
	@Override
	public BulldozerConfiguration ReadFromCompoundNBT(NbtCompound messageTag) {
		BulldozerConfiguration config = new BulldozerConfiguration();

		return (BulldozerConfiguration) super.ReadFromCompoundNBT(messageTag, config);
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
		StructureBulldozer structure = new StructureBulldozer();

		if (player.isCreative()) {
			this.creativeMode = true;
		}

		if (structure.BuildStructure(this, world, hitBlockPos, Direction.NORTH, player)) {
			ItemStack stack = player.getOffHandStack();
			Hand hand = Hand.OFF_HAND;

			if (stack.getItem() == ModRegistry.CreativeBulldozer) {
				this.creativeMode = true;
			}

			if (stack.getItem() != ModRegistry.Bulldozer) {
				stack = player.getMainHandStack();
				hand = Hand.MAIN_HAND;
			}

			if (stack.getItem() == ModRegistry.Bulldozer) {
				Hand hand1 = hand;
				stack.damage(1, player, (player1) ->
				{
					player1.sendToolBreakStatus(hand1);
				});

				player.currentScreenHandler.sendContentUpdates();
			}
		}
	}

}