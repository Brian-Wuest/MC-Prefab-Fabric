package com.wuest.prefab.structures.items;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.structures.gui.GuiStructure;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;

/**
 * @author WuestMan
 */
@SuppressWarnings("NullableProblems")
public class StructureItem extends Item {

    /**
     * Initializes a new instance of the StructureItem class.
     */
    public StructureItem() {
        super(new Item.Settings().group(ModRegistry.PREFAB_GROUP));
        this.Initialize();
    }

    public StructureItem(Item.Settings properties) {
        super(properties);
        this.Initialize();
    }

    /**
     * Does something when the item is right-clicked.
     */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient) {
            if (context.getSide() == Direction.UP) {
                if (Prefab.useScanningMode) {
                    this.scanningMode(context);
                } else {
                    // Open the client side gui to determine the house options.
                    ClientModRegistry.openGuiForItem(context);
                }

                return ActionResult.PASS;
            }
        }

        return ActionResult.FAIL;
    }

    public void scanningMode(ItemUsageContext context) {
    }

    /**
     * Initializes common fields/properties for this structure item.
     */
    protected void Initialize() {
    }

    protected void RegisterGui(Class<?> classToRegister) {
        try {
            GuiStructure userInterface = (GuiStructure) classToRegister.newInstance();
            ClientModRegistry.ModGuis.put(this, userInterface);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
