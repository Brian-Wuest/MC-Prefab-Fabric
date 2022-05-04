package com.wuest.prefab.gui.screens.menus;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Utils;
import com.wuest.prefab.items.ItemBlueprint;
import com.wuest.prefab.structures.custom.base.CustomStructureInfo;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DraftingTableMenu extends AbstractContainerMenu {
    public final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private CustomStructureInfo selectedStructureInfo;
    private boolean isTakingStructure;
    private Player player;

    private IStructureMaterialLoader parent;

    public static DraftingTableMenu RegisteredMenuType(int i, Inventory inventory) {
        return new DraftingTableMenu(ModRegistry.DraftingTableMenuType, i, inventory, ContainerLevelAccess.NULL);
    }

    public DraftingTableMenu(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        this(ModRegistry.DraftingTableMenuType, i, inventory, containerLevelAccess);
    }

    public DraftingTableMenu(MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i);
        this.player = inventory.player;
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
                super.onTake(player, itemStack);
                DraftingTableMenu.this.onTake(player, itemStack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                DraftingTableMenu.this.resultSlotChanged();
            }
        });

        // Create the player hot-bar slots
        for (l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventory, l, 8 + l * 18, 194 + k) {
                @Override
                public void setChanged() {
                    super.setChanged();

                    DraftingTableMenu.this.triggerSlotChanged();
                }
            });
        }

        // Create the player inventory slots.
        for (l = 0; l < 3; ++l) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(inventory, m + l * 9 + 9, 8 + m * 18, 136 + l * 18 + k) {
                    @Override
                    public void setChanged() {
                        super.setChanged();

                        DraftingTableMenu.this.triggerSlotChanged();
                    }
                });
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
        return (Boolean) this.access.evaluate((level, blockPos) -> {
            return this.isValidBlock(level.getBlockState(blockPos)) && player.distanceToSqr((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D) <= 64.0D;
        }, true);
    }

    public void setSelectedStructureInfo(CustomStructureInfo selectedStructureInfo) {
        this.selectedStructureInfo = selectedStructureInfo;

        this.setResultSlot();
    }

    public void setParent(IStructureMaterialLoader parent) {
        this.parent = parent;
    }

    protected void setResultSlot() {
        // Note: This is only called from client-side.
        if (this.selectedStructureInfo != null) {
            if (this.player.level.isClientSide) {
                // This way the server can update the result slot with the new item when an item is taken.
                CompoundTag structureTag = new CompoundTag();
                this.selectedStructureInfo.writeToTag(structureTag);

                FriendlyByteBuf messagePacket = new FriendlyByteBuf(Unpooled.buffer());
                messagePacket.writeNbt(structureTag);
                ClientPlayNetworking.send(ModRegistry.DraftingTableResultSync, messagePacket);
            }
            else {
                ItemStack stack = new ItemStack(ModRegistry.BlankBlueprint);
                CompoundTag tag = stack.getOrCreateTag();
                tag.putString(ItemBlueprint.StructureTag, this.selectedStructureInfo.displayName);
                
                // Server-side, just set the result slot.
                this.resultSlots.setItem(0, stack);
            }
        }
    }

    protected boolean isValidBlock(BlockState blockState) {
        return blockState.getBlock() == ModRegistry.DraftingTable;
    }

    protected boolean mayPickup(Player player, boolean bl) {
        // TODO: Check player inventory for enough materials for the currently selected structure.
        return true;
    }

    protected void onTake(Player player, ItemStack itemStack) {
        this.isTakingStructure = true;

        // TODO: Remove items from player inventory for currently selected structure.

        this.isTakingStructure = false;

        // Make sure to re-trigger slot changed to update the GUI.
        // This may also allow the player to take another item from the slot.
        //this.triggerSlotChanged();
        this.setResultSlot();
    }

    protected void triggerSlotChanged() {
        // Make sure to not try to trigger the GUI re-load when the player is currently taking the item or the parent hasn't been set yet.
        if (this.parent != null && !this.isTakingStructure) {
            // This will update the UI to show the potentially updated needed/has values for materials.
            this.parent.loadMaterialEntries();
        }
    }

    protected void resultSlotChanged() {
        if (!this.player.level.isClientSide) {
            this.setResultSlot();
        }
    }

    /**
     * This interface is used to trigger a re-load of the item-list control on the GUI.
     */
    public interface IStructureMaterialLoader {
        /**
         * Re-loads the item list on the UI.
         */
        void loadMaterialEntries();
    }
}
