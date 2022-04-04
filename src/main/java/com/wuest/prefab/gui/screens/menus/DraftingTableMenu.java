package com.wuest.prefab.gui.screens.menus;

import com.wuest.prefab.ModRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class DraftingTableMenu extends AbstractContainerMenu {
    private final Container container;

    public static DraftingTableMenu RegisteredMenuType(int i, Inventory inventory) {
        return new DraftingTableMenu(ModRegistry.DraftingTableMenuType, i, inventory);
    }

    public DraftingTableMenu(MenuType<?> menuType, int i, Inventory inventory, Container container) {
        super(menuType, i);
        this.container = container;
        container.startOpen(inventory.player);
        int k = 18;
        int l;
        int m;

        // Create the player inventory slots.
        for(l = 0; l < 3; ++l) {
            for(m = 0; m < 9; ++m) {
                this.addSlot(new Slot(inventory, m + l * 9 + 9, 8 + m * 18, 103 + l * 18 + k));
            }
        }

        // Create the player hot-bar slots
        for(l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventory, l, 8 + l * 18, 161 + k));
        }

    }

    private DraftingTableMenu(MenuType<?> menuType, int i, Inventory inventory) {
        this(menuType, i, inventory, new SimpleContainer(1));
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}
