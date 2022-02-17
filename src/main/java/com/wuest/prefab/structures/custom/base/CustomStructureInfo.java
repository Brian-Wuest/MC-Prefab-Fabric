package com.wuest.prefab.structures.custom.base;

import com.google.gson.annotations.Expose;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Contains the details of a custom structure.
 */
public class CustomStructureInfo {

    /**
     * The display name to show in the GUI.
     */
    @Expose
    public String displayName;

    /**
     * The structure file name containing all the blocks used to generate the building.
     */
    @Expose
    public String structureFileName;

    /**
     * The required items for this custom structure.
     */
    @Expose
    public ArrayList<ItemInfo> requiredItems;

    /**
     * Contains the structure file path determined during registration.
     */
    public Path structureFilePath;
}
