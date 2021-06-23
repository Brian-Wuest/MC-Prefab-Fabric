package com.wuest.prefab.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;

@Config(name = "Prefab")
public class ModConfiguration implements ConfigData {

    @Comment("Item provided to new players when joining a world.")
    public StartingItemOptions startingItem = StartingItemOptions.StartingHouse;

    @Comment("Enables the loft starter house.")
    public boolean enableLoftHouse = false;

    @Comment("Add spawners to monster masher.")
    public boolean includeSpawnersInMasher = true;

    @Comment("Enables preview buttons in screens.")
    public boolean enableStructurePreview = true;

    @Comment("Bulldozer makes drops.")
    public boolean allowBulldozerToCreateDrops = true;

    @Comment("Generated mineshafts have chests with materials.")
    public boolean includeMineshaftChest = true;

    @ConfigEntry.Category("recipes")
    public HashMap<String, Boolean> recipes = new HashMap<>();

    @ConfigEntry.Category("chest_options")
    @ConfigEntry.Gui.TransitiveObject()
    public ChestOptions chestOptions = new ChestOptions();

    @ConfigEntry.Category("starter_options")
    @ConfigEntry.Gui.TransitiveObject()
    public StarterHouseOptions starterHouseOptions = new StarterHouseOptions();

    public ModConfiguration() {
        for (String key : ConfigKeyNames.Keys) {
            this.recipes.put(key, true);
        }
    }

    public NbtCompound writeNbtCompound() {
        NbtCompound tag = new NbtCompound();

        tag.putString(ConfigKeyNames.startingItemName, this.startingItem.toString());
        tag.putBoolean(ConfigKeyNames.enableLoftHouseName, this.enableLoftHouse);
        tag.putBoolean(ConfigKeyNames.includeSpawnersInMasherName, this.includeSpawnersInMasher);
        tag.putBoolean(ConfigKeyNames.enableStructurePreviewName, this.enableStructurePreview);
        tag.putBoolean(ConfigKeyNames.includeMineshaftChestName, this.includeMineshaftChest);
        tag.putBoolean(ConfigKeyNames.allowBulldozerToCreateDropsName, this.allowBulldozerToCreateDrops);

        tag.putBoolean(ConfigKeyNames.addSwordName, this.chestOptions.addSword);
        tag.putBoolean(ConfigKeyNames.addAxeName, this.chestOptions.addAxe);
        tag.putBoolean(ConfigKeyNames.addShovelName, this.chestOptions.addShovel);
        tag.putBoolean(ConfigKeyNames.addHoeName, this.chestOptions.addHoe);
        tag.putBoolean(ConfigKeyNames.addPickAxeName, this.chestOptions.addPickAxe);
        tag.putBoolean(ConfigKeyNames.addArmorName, this.chestOptions.addArmor);
        tag.putBoolean(ConfigKeyNames.addFoodName, this.chestOptions.addFood);
        tag.putBoolean(ConfigKeyNames.addCropsName, this.chestOptions.addCrops);
        tag.putBoolean(ConfigKeyNames.addDirtName, this.chestOptions.addDirt);
        tag.putBoolean(ConfigKeyNames.addCobbleName, this.chestOptions.addCobble);
        tag.putBoolean(ConfigKeyNames.addSaplingsName, this.chestOptions.addSaplings);
        tag.putBoolean(ConfigKeyNames.addTorchesName, this.chestOptions.addTorches);

        tag.putBoolean(ConfigKeyNames.addBedName, this.starterHouseOptions.addBed);
        tag.putBoolean(ConfigKeyNames.addCraftingTableName, this.starterHouseOptions.addCraftingTable);
        tag.putBoolean(ConfigKeyNames.addFurnaceName, this.starterHouseOptions.addFurnace);
        tag.putBoolean(ConfigKeyNames.addChestsName, this.starterHouseOptions.addChests);
        tag.putBoolean(ConfigKeyNames.addChestContentsName, this.starterHouseOptions.addChestContents);
        tag.putBoolean(ConfigKeyNames.addMineshaftName, this.starterHouseOptions.addMineshaft);

        for (Map.Entry<String, Boolean> entry : this.recipes.entrySet()) {
            tag.putBoolean(entry.getKey(), entry.getValue());
        }

        return tag;
    }

    public void readFromTag(NbtCompound tag) {
        this.startingItem = StartingItemOptions.getByName(tag.getString(ConfigKeyNames.startingItemName));

        this.enableLoftHouse = tag.getBoolean(ConfigKeyNames.enableLoftHouseName);
        this.includeSpawnersInMasher = tag.getBoolean(ConfigKeyNames.includeSpawnersInMasherName);
        this.enableStructurePreview = tag.getBoolean(ConfigKeyNames.enableStructurePreviewName);
        this.includeMineshaftChest = tag.getBoolean(ConfigKeyNames.includeMineshaftChestName);
        this.allowBulldozerToCreateDrops = tag.getBoolean(ConfigKeyNames.allowBulldozerToCreateDropsName);

        this.chestOptions.addSword = tag.getBoolean(ConfigKeyNames.addSwordName);
        this.chestOptions.addAxe = tag.getBoolean(ConfigKeyNames.addAxeName);
        this.chestOptions.addShovel = tag.getBoolean(ConfigKeyNames.addShovelName);
        this.chestOptions.addHoe = tag.getBoolean(ConfigKeyNames.addHoeName);
        this.chestOptions.addPickAxe = tag.getBoolean(ConfigKeyNames.addPickAxeName);
        this.chestOptions.addArmor = tag.getBoolean(ConfigKeyNames.addArmorName);
        this.chestOptions.addFood = tag.getBoolean(ConfigKeyNames.addFoodName);
        this.chestOptions.addCrops = tag.getBoolean(ConfigKeyNames.addCropsName);
        this.chestOptions.addDirt = tag.getBoolean(ConfigKeyNames.addDirtName);
        this.chestOptions.addCobble = tag.getBoolean(ConfigKeyNames.addCobbleName);
        this.chestOptions.addSaplings = tag.getBoolean(ConfigKeyNames.addSaplingsName);
        this.chestOptions.addTorches = tag.getBoolean(ConfigKeyNames.addTorchesName);

        this.starterHouseOptions.addBed = tag.getBoolean(ConfigKeyNames.addBedName);
        this.starterHouseOptions.addCraftingTable = tag.getBoolean(ConfigKeyNames.addCraftingTableName);
        this.starterHouseOptions.addFurnace = tag.getBoolean(ConfigKeyNames.addFurnaceName);
        this.starterHouseOptions.addChests = tag.getBoolean(ConfigKeyNames.addChestsName);
        this.starterHouseOptions.addChestContents = tag.getBoolean(ConfigKeyNames.addChestContentsName);
        this.starterHouseOptions.addMineshaft = tag.getBoolean(ConfigKeyNames.addMineshaftName);

        this.recipes.clear();

        for (String key : ConfigKeyNames.Keys) {
            this.recipes.put(key, tag.getBoolean(key));
        }
    }

    public enum StartingItemOptions {
        StartingHouse("Starting House"),
        ModerateHouse("Moderate House"),
        StructureParts("Structure Parts"),
        Nothing("Nothing");

        private final String name;

        StartingItemOptions(String name) {
            this.name = name;
        }

        public static StartingItemOptions getByName(String name) {
            StartingItemOptions returnValue = StartingItemOptions.Nothing;

            for (StartingItemOptions options : StartingItemOptions.values()) {
                if (options.name.equals(name)
                        || options.name().equals(name)) {
                    returnValue = options;
                    break;
                }
            }

            return returnValue;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static class ConfigKeyNames {
        public static String compressedStoneKey = "Compressed Stone";
        public static String compressedGlowStoneKey = "Compressed Glowstone";
        public static String compressedDirtKey = "Compressed Dirt";
        public static String compressedChestKey = "Compressed Chest";
        public static String pileOfBricksKey = "Pile of Bricks";
        public static String warehouseKey = "Warehouse";
        public static String produceFarmKey = "Produce Farm";
        public static String treeFarmKey = "Tree Farm";
        public static String chickenCoopKey = "Chicken Coop";
        public static String fishFarmKey = "Fish Farm";
        public static String warehouseUpgradeKey = "Warehouse Upgrade";
        public static String advancedWarehouseKey = "Advanced Warehouse";
        public static String monsterMasherKey = "Monster Masher";
        public static String bundleofTimberKey = "Bundle of Timber";
        public static String horseStableKey = "Horse Stable";
        public static String netherGateKey = "Nether Gate";
        public static String advancedChickenCoopKey = "Advanced Chicken Coop";
        public static String advancedHorseStableKey = "Advanced Horse Stable";
        public static String barnKey = "Barn";
        public static String machineryTowerKey = "Machinery Tower";
        public static String defenseBunkerKey = "Defense Bunker";
        public static String mineshaftEntranceKey = "Mineshaft Entrance";
        public static String enderGatewayKey = "Ender Gateway";
        public static String aquaBaseKey = "Aqua Base";
        public static String grassyPlainsKey = "Grassy Plains";
        public static String magicTempleKey = "Magic Temple";
        public static String instantBridgeKey = "Instant Bridge";
        public static String paperLanternKey = "Paper Lantern";
        public static String compressedObsidianKey = "Compressed Obsidian";
        public static String villagerHousesKey = "Villager Houses";
        public static String phasicBlockKey = "Phasic Block";
        public static String smartGlassKey = "Boundary Block";
        public static String greenHouseKey = "Green House";
        public static String startingHouseKey = "Starting House";
        public static String glassStairsKey = "Glass Stairs";
        public static String glassSlabsKey = "Glass Slabs";
        public static String moderateHouseKey = "Moderate House";
        public static String watchTowerKey = "Watch Tower";
        public static String bulldozerKey = "Bulldozer";
        public static String structurePartKey = "StructurePart";
        public static String jailKey = "Jail";
        public static String saloonKey = "Saloon";
        public static String skiLodgeKey = "Ski Lodge";
        public static String windMillKey = "Windmill";
        public static String townHallKey = "Town Hall";
        public static String heapOfTimberKey = "Heap of Timber";
        public static String tonOfTimberKey = "Ton of Timber";
        public static String sugarCaneFarmKey = "Sugar Cane Farm";
        private static final String workshopKey = "Workshop";

        public static String[] Keys = new String[]
                {ConfigKeyNames.compressedStoneKey, ConfigKeyNames.compressedGlowStoneKey, ConfigKeyNames.compressedDirtKey, ConfigKeyNames.compressedChestKey, ConfigKeyNames.pileOfBricksKey,
                        ConfigKeyNames.warehouseKey, ConfigKeyNames.produceFarmKey, ConfigKeyNames.treeFarmKey, ConfigKeyNames.chickenCoopKey, ConfigKeyNames.fishFarmKey,
                        ConfigKeyNames.warehouseUpgradeKey, ConfigKeyNames.advancedWarehouseKey, ConfigKeyNames.monsterMasherKey, ConfigKeyNames.bundleofTimberKey, ConfigKeyNames.horseStableKey,
                        ConfigKeyNames.netherGateKey, ConfigKeyNames.advancedChickenCoopKey, ConfigKeyNames.advancedHorseStableKey, ConfigKeyNames.barnKey,
                        ConfigKeyNames.machineryTowerKey, ConfigKeyNames.defenseBunkerKey, ConfigKeyNames.mineshaftEntranceKey, ConfigKeyNames.enderGatewayKey, ConfigKeyNames.magicTempleKey,
                        ConfigKeyNames.instantBridgeKey, ConfigKeyNames.paperLanternKey, ConfigKeyNames.compressedObsidianKey, ConfigKeyNames.villagerHousesKey,
                        ConfigKeyNames.phasicBlockKey, ConfigKeyNames.smartGlassKey, ConfigKeyNames.greenHouseKey, ConfigKeyNames.startingHouseKey, ConfigKeyNames.glassStairsKey,
                        ConfigKeyNames.glassSlabsKey, ConfigKeyNames.moderateHouseKey, ConfigKeyNames.grassyPlainsKey, ConfigKeyNames.aquaBaseKey, ConfigKeyNames.watchTowerKey,
                        ConfigKeyNames.bulldozerKey, ConfigKeyNames.structurePartKey, ConfigKeyNames.jailKey, ConfigKeyNames.saloonKey, ConfigKeyNames.skiLodgeKey, ConfigKeyNames.windMillKey,
                        ConfigKeyNames.townHallKey, ConfigKeyNames.heapOfTimberKey, ConfigKeyNames.tonOfTimberKey, sugarCaneFarmKey, workshopKey};

        // Config file option names.
        static String enableLoftHouseName = "Enable Loft House";
        static String includeSpawnersInMasherName = "Include Spawners in Monster Masher";
        static String enableStructurePreviewName = "Include Structure Previews";
        static String includeMineshaftChestName = "Include Mineshaft Chest";
        // Chest content option names.
        static String addSwordName = "Add Sword";
        static String addAxeName = "Add Axe";
        static String addHoeName = "Add Hoe";
        static String addShovelName = "Add Shovel";
        static String addPickAxeName = "Add Pickaxe";
        static String addArmorName = "Add Armor";
        static String addFoodName = "Add Food";
        static String addCropsName = "Add Crops";
        static String addDirtName = "Add Dirt";
        static String addCobbleName = "Add Cobblestone";
        static String addSaplingsName = "Add Saplings";
        static String addTorchesName = "Add Torches";
        static String startingItemName = "Starting Item";
        // Starter House option names.
        static String addBedName = "Add Bed";
        static String addCraftingTableName = "Add Crafting Table";
        static String addFurnaceName = "Add Furnace";
        static String addChestsName = "Add Chests";
        static String addChestContentsName = "Add Chest Contents";
        static String addMineshaftName = "Add Mineshaft";
        static String allowBulldozerToCreateDropsName = "Bulldozer Creates Drops";
    }
}
