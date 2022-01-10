package com.wuest.prefab.items;

import com.wuest.prefab.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * @author WuestMan
 */
public class ItemBlockWoodenCrate extends BlockItem {
    public final ItemWoodenCrate.CrateType crateType;

    /**
     * Creates a new instance of the ItemWoodenCrateClass.
     */
    public ItemBlockWoodenCrate(Block linkedBlock, ItemWoodenCrate.CrateType crateType) {
        super(linkedBlock, new Item.Settings()
                .group(ModRegistry.PREFAB_GROUP)
                .recipeRemainder(ItemWoodenCrate.getRecipeRemainderForCrateType(crateType)));

        this.crateType = crateType;
    }
}
