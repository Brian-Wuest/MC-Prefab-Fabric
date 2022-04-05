package com.wuest.prefab.gui.screens.containers;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class DraftingTableContainer extends SimpleContainer {

    public DraftingTableContainer(int i) {
        super(i);
    }

    @Override
    public boolean canAddItem(ItemStack itemStack) {
        return false;
    }

    @Override
    public ItemStack addItem(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
    }
}
