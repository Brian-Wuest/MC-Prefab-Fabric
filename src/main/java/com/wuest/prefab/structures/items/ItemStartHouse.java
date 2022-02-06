package com.wuest.prefab.structures.items;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.structures.gui.GuiStartHouseChooser;
import net.minecraft.world.item.context.UseOnContext;

/**
 * @author WuestMan
 */
@SuppressWarnings("ALL")
public class ItemStartHouse extends StructureItem {
    public ItemStartHouse() {
        super();
    }

    @Override
    public void scanningMode(UseOnContext context) {
    }

    /**
     * Initializes common fields/properties for this structure item.
     */
    @Override
    protected void Initialize() {
        ModRegistry.guiRegistrations.add(x -> this.RegisterGui(GuiStartHouseChooser.class));
    }
}