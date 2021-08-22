package com.wuest.prefab.items;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemCompressedChest extends Item {
	/**
	 * Initializes a new instance of the ItemCondensedChest class.
	 */
	public ItemCompressedChest() {
		super(new Item.Settings().group(ModRegistry.PREFAB_GROUP));

	}

	/**
	 * allows items to add custom lines of information to the mouse-over description
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext options) {
		tooltip.addAll(Utils.WrapStringToLiterals("Used in the recipes for structures, not for direct storage", 30));
	}
}
