package com.wuest.prefab.config;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class ChestOptions {

	@Comment("Include a stone sword in the chest")
	public boolean addSword = true;

	@Comment("Include a stone axe in the chest")
	public boolean addAxe = true;

	@Comment("Include a stone shovel in the chest")
	public boolean addShovel = true;

	@Comment("Include a stone hoe in the chest")
	public boolean addHoe = true;

	@Comment("Include a stone pickaxe in the chest")
	public boolean addPickAxe = true;

	@Comment("Include leather armor in the chest")
	public boolean addArmor = true;

	@Comment("Include some bread in the chest")
	public boolean addFood = true;

	@Comment("Include some wheat seeds in the chest")
	public boolean addCrops = true;

	@Comment("Include a stack of dirt in the chest")
	public boolean addDirt = true;

	@Comment("Include a stack of cobblestone in the chest")
	public boolean addCobble = true;

	@Comment("Include some saplings in the chest")
	public boolean addSaplings = true;

	@Comment("Include a few torches in the chest")
	public boolean addTorches = true;

	public ChestOptions() {
	}
}
