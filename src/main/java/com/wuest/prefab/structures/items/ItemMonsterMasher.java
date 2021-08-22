package com.wuest.prefab.structures.items;


import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.structures.gui.GuiMonsterMasher;
import com.wuest.prefab.structures.predefined.StructureMonsterMasher;
import net.minecraft.item.ItemUsageContext;

/**
 * @author WuestMan
 */
@SuppressWarnings("ConstantConditions")
public class ItemMonsterMasher extends StructureItem {
    public ItemMonsterMasher() {
        super();
    }

    @Override
    public void scanningMode(ItemUsageContext context) {
        StructureMonsterMasher.ScanStructure(
                context.getWorld(),
                context.getBlockPos(),
                context.getPlayer().getHorizontalFacing());
    }

    /**
     * Initializes common fields/properties for this structure item.
     */
    @Override
    protected void Initialize() {
        ModRegistry.guiRegistrations.add(x -> this.RegisterGui(GuiMonsterMasher.class));
    }
}