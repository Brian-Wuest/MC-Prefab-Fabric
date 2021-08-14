package com.wuest.prefab.structures.config;

import com.wuest.prefab.structures.base.BuildBlock;
import com.wuest.prefab.structures.items.StructureItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


/**
 * This is the base configuration class used by all structures.
 *
 * @author WuestMan
 */
@SuppressWarnings("WeakerAccess")
public class StructureConfiguration {
	public static String houseFacingName = "House Facing";

	private static String hitXTag = "hitX";
	private static String hitYTag = "hitY";
	private static String hitZTag = "hitZ";
	private static String houseFacingTag = "wareHouseFacing";

	/**
	 * The structure facing property.
	 */
	public Direction houseFacing;

	/**
	 * The position of the structure.
	 */
	public BlockPos pos;

	/**
	 * Initializes a new instance of the StructureConfiguration class.
	 */
	public StructureConfiguration() {
		this.Initialize();
	}

	/**
	 * Initializes any properties for this class.
	 */
	public void Initialize() {
		this.houseFacing = Direction.NORTH;
	}

	/**
	 * Writes the properties to an CompoundNBT.
	 *
	 * @return An CompoundNBT with the updated properties.
	 */
	public NbtCompound WriteToCompoundNBT() {
		NbtCompound tag = new NbtCompound();

		if (this.pos != null) {
			tag.putInt(StructureConfiguration.hitXTag, this.pos.getX());
			tag.putInt(StructureConfiguration.hitYTag, this.pos.getY());
			tag.putInt(StructureConfiguration.hitZTag, this.pos.getZ());
		}

		tag.putString(StructureConfiguration.houseFacingTag, this.houseFacing.asString());

		tag = this.CustomWriteToCompoundNBT(tag);

		return tag;
	}

	/**
	 * Reads CompoundNBT to create a StructureConfiguration object from.
	 *
	 * @param messageTag The CompoundNBT to read the properties from.
	 * @return The updated StructureConfiguration instance.
	 */
	public StructureConfiguration ReadFromCompoundNBT(NbtCompound messageTag) {
		return null;
	}

	/**
	 * Reads CompoundNBT to create a StructureConfiguration object from.
	 *
	 * @param messageTag The CompoundNBT to read the properties from.
	 * @param config     The existing StructureConfiguration instance to fill the properties in for.
	 * @return The updated StructureConfiguration instance.
	 */
	public StructureConfiguration ReadFromCompoundNBT(NbtCompound messageTag, StructureConfiguration config) {
		if (messageTag != null) {
			if (messageTag.contains(StructureConfiguration.hitXTag)) {
				config.pos = new BlockPos(
						messageTag.getInt(StructureConfiguration.hitXTag),
						messageTag.getInt(StructureConfiguration.hitYTag),
						messageTag.getInt(StructureConfiguration.hitZTag));
			}

			if (messageTag.contains(StructureConfiguration.houseFacingTag)) {
				config.houseFacing = BuildBlock.getDirectionByName(messageTag.getString(StructureConfiguration.houseFacingTag));
			}

			this.CustomReadFromNBTTag(messageTag, config);
		}

		return config;
	}

	/**
	 * Generic method to start the building of the structure.
	 *
	 * @param player The player which requested the build.
	 * @param world  The world instance where the build will occur.
	 */
	public void BuildStructure(PlayerEntity player, ServerWorld world) {
		// This is always on the server.
		BlockPos hitBlockPos = this.pos;

		this.ConfigurationSpecificBuildStructure(player, world, hitBlockPos);
	}

	/**
	 * This is used to actually build the structure as it creates the structure instance and calls build structure.
	 *
	 * @param player      The player which requested the build.
	 * @param world       The world instance where the build will occur.
	 * @param hitBlockPos This hit block position.
	 */
	protected void ConfigurationSpecificBuildStructure(PlayerEntity player, ServerWorld world, BlockPos hitBlockPos) {
	}

	/**
	 * Custom method which can be overridden to write custom properties to the tag.
	 *
	 * @param tag The CompoundNBT to write the custom properties too.
	 * @return The updated tag.
	 */
	protected NbtCompound CustomWriteToCompoundNBT(NbtCompound tag) {
		return tag;
	}

	/**
	 * Custom method to read the CompoundNBT message.
	 *
	 * @param messageTag The message to create the configuration from.
	 * @param config     The configuration to read the settings into.
	 */
	protected void CustomReadFromNBTTag(NbtCompound messageTag, StructureConfiguration config) {
	}

	/**
	 * This method will remove 1 structure item from the player's inventory, it is expected that the item is in the
	 * player's hand.
	 *
	 * @param player The player to remove the item from.
	 * @param item   the structure item to find.
	 */
	protected void RemoveStructureItemFromPlayer(PlayerEntity player, StructureItem item) {
		ItemStack stack = player.getMainHandStack();

		if (stack.getItem() != item) {
			stack = player.getOffHandStack();
		}

		int slot = this.getSlotFor(player.inventory, stack);

		if (slot != -1) {
			stack.decrement(1);

			if (stack.isEmpty()) {
				player.inventory.setStack(slot, ItemStack.EMPTY);
			}

			player.currentScreenHandler.sendContentUpdates();
		}
	}

	protected void DamageHeldItem(PlayerEntity player, StructureItem item) {
		ItemStack stack = player.getMainHandStack().getItem() == item ? player.getMainHandStack() : player.getOffHandStack();
		EquipmentSlot hand = player.getMainHandStack().getItem() == item ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

		ItemStack copy = stack.copy();

		stack.damage(1, player, (player1) ->
		{
			player1.sendEquipmentBreakStatus(hand);
		});

		if (stack.isEmpty()) {
			player.equipStack(hand, ItemStack.EMPTY);
		}

		player.currentScreenHandler.sendContentUpdates();
	}

	/**
	 * Checks item, NBT, and meta if the item is not damageable
	 */
	private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && ItemStack.areTagsEqual(stack1, stack2);
	}

	/**
	 * Get's the first slot which contains the item in the supplied item stack in the player's main inventory.
	 * This method was copied directly from teh player inventory class since it was needed server side.
	 *
	 * @param playerInventory The player's inventory to try and find a slot.
	 * @param stack           The stack to find an associated slot.
	 * @return The slot index or -1 if the item wasn't found.
	 */
	public int getSlotFor(PlayerInventory playerInventory, ItemStack stack) {
		for (int i = 0; i < playerInventory.main.size(); ++i) {
			if (!playerInventory.main.get(i).isEmpty() && this.stackEqualExact(stack, playerInventory.main.get(i))) {
				return i;
			}
		}

		return -1;
	}
}
