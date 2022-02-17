package com.wuest.prefab.structures.custom.base;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.wuest.prefab.util.ResourceLocationSerializer;
import net.minecraft.resources.ResourceLocation;

/**
 * This class contains the item information.
 */
public class ItemInfo {

    /**
     * The item.
     */
    @Expose
    @JsonAdapter(ResourceLocationSerializer.class)
    public ResourceLocation item;

    /**
     * The required number of items.
     */
    @Expose
    public int count;
}
