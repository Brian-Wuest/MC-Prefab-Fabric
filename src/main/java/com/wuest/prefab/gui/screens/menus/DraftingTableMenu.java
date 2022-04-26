package com.wuest.prefab.gui.screens.menus;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.structures.custom.base.CustomStructureInfo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DraftingTableMenu extends AbstractContainerMenu {
    public final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private CustomStructureInfo selectedStructureInfo;

    public static DraftingTableMenu RegisteredMenuType(int i, Inventory inventory) {
        return new DraftingTableMenu(ModRegistry.DraftingTableMenuType, i, inventory, ContainerLevelAccess.NULL);
    }

    public DraftingTableMenu(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        this(ModRegistry.DraftingTableMenuType, i, inventory,containerLevelAccess);
    }

    public DraftingTableMenu(MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i);
        this.access = containerLevelAccess;
        int k = 18;
        int l;
        int m;

        this.addSlot(new Slot(this.resultSlots, 2, 152, 130) {
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }

            public boolean mayPickup(Player player) {
                return DraftingTableMenu.this.mayPickup(player, this.hasItem());
            }

            public void onTake(Player player, ItemStack itemStack) {
                DraftingTableMenu.this.onTake(player, itemStack);
            }
        });

        // Create the player hot-bar slots
        for(l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventory, l, 8 + l * 18, 194 + k));
        }

        // Create the player inventory slots.
        for(l = 0; l < 3; ++l) {
            for(m = 0; m < 9; ++m) {
                this.addSlot(new Slot(inventory, m + l * 9 + 9, 8 + m * 18, 136 + l * 18 + k));
            }
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> {
            this.clearContainer(player, this.resultSlots);
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return (Boolean)this.access.evaluate((level, blockPos) -> {
            return this.isValidBlock(level.getBlockState(blockPos)) && player.distanceToSqr((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D) <= 64.0D;
        }, true);
    }

    public void setSelectedStructureInfo(CustomStructureInfo selectedStructureInfo) {
        this.selectedStructureInfo = selectedStructureInfo;

        // TODO: Create blueprint item with this identifier as part of the meta data.
        // this.resultSlots.setItem(0, ItemStack.EMPTY);
    }

    protected boolean isValidBlock(BlockState blockState) {
        return blockState.getBlock() == ModRegistry.DraftingTable;
    }

    protected boolean mayPickup(Player player, boolean bl) {
        // TODO: Check player inventory for enough materials for the currently selected structure.
        return true;
    }

    protected void onTake(Player player, ItemStack itemStack) {
        // TODO: Remove items from player inventory for currently selected structure.
    }
}
