package com.wuest.prefab.structures.items;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.structures.config.BasicStructureConfiguration;
import com.wuest.prefab.structures.gui.GuiBasicStructure;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;

/**
 * This class is used for basic structures to show the basic GUI.
 *
 * @author WuestMan
 */
@SuppressWarnings({"AccessStaticViaInstance", "ConstantConditions"})
public class ItemBasicStructure extends StructureItem {
    public final BasicStructureConfiguration.EnumBasicStructureName structureType;

    public ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName structureType) {
        super();

        this.structureType = structureType;
    }

    public ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName structureType, int durability) {
        super(new Item.Settings()
                .group(ModRegistry.PREFAB_GROUP)
                .maxDamage(durability));
        this.structureType = structureType;
    }

    public static ItemStack getBasicStructureItemInHand(PlayerEntity player) {
        ItemStack stack = player.getOffHandStack();

        // Get off hand first since that is the right-click hand if there is
        // something in there.
        if (!(stack.getItem() instanceof ItemBasicStructure)) {
            if (player.getMainHandStack().getItem() instanceof ItemBasicStructure) {
                stack = player.getMainHandStack();
            } else {
                stack = null;
            }
        }

        return stack;
    }

    /**
     * Initializes common fields/properties for this structure item.
     */
    @Override
    protected void Initialize() {
        ModRegistry.guiRegistrations.add(x -> this.RegisterGui(GuiBasicStructure.class));
    }

    /**
     * Does something when the item is right-clicked.
     */
    @Override
    public void scanningMode(ItemUsageContext context) {
    }
}