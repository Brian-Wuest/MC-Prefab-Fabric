package com.wuest.prefab.items;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.structures.custom.base.CustomStructureInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemBlueprint extends Item {
    public ItemBlueprint(Properties properties) {
        super(properties);
    }

    public static final String StructureTag = "structure_id";

    @Override
    public Component getName(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();

        if (tag.contains(ItemBlueprint.StructureTag)) {
            String structureId = tag.getString(ItemBlueprint.StructureTag);

            for (CustomStructureInfo clientStructure : ClientModRegistry.ServerRegisteredStructures) {
                if (clientStructure.displayName.equalsIgnoreCase(structureId)) {
                    TranslatableComponent initialComponent = new TranslatableComponent(this.getDescriptionId(itemStack));

                    return new TextComponent(initialComponent.getString() + " - " + clientStructure.displayName);
                }
            }
        }

        return new TranslatableComponent(this.getDescriptionId(itemStack));
    }
}
