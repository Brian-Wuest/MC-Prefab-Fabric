package com.wuest.prefab.structures.items;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.structures.gui.GuiStartHouseChooser;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;

/**
 * @author WuestMan
 */
@SuppressWarnings("ALL")
public class ItemStartHouse extends StructureItem {
    public ItemStartHouse() {
        super();
    }

    /**
     * Does something when the item is right-clicked.
     */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient) {
            if (context.getSide() == Direction.UP) {
                if (!Prefab.useScanningMode) {
                    // Open the client side gui to determine the house options.
                    ClientModRegistry.openGuiForItem(context);
                } else {
                    this.scanningMode(context);
                }
                return ActionResult.PASS;
            }
        }

        return ActionResult.FAIL;
    }

    @Override
    public void scanningMode(ItemUsageContext context) {
    }

    /**
     * Initializes common fields/properties for this structure item.
     */
    @Override
    protected void Initialize() {
        ModRegistry.guiRegistrations.add(x -> this.RegisterGui(GuiStartHouseChooser.class));
    }
}