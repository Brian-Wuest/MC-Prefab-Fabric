package com.wuest.prefab.config;


import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class StarterHouseOptions {

	@Comment("Adds a bed to the starter/moderate house.")
	public boolean addBed = true;

	@Comment("Adds a crafting table to the starter/moderate house.")
	public boolean addCraftingTable = true;

	@Comment("Adds a furnace to the starter/moderate house.")
	public boolean addFurnace = true;

	@Comment("Adds chests to the starter/moderate house.")
	public boolean addChests = true;

	@Comment("Adds chest contents to the starter/moderate house.")
	public boolean addChestContents = true;

	@Comment("Adds a mineshaft to the starter/moderate house.")
	public boolean addMineshaft = true;

	public StarterHouseOptions() {

	}
}