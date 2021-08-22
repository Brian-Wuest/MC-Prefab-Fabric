package com.wuest.prefab;

import com.wuest.prefab.blocks.*;
import com.wuest.prefab.items.ItemCompressedChest;
import com.wuest.prefab.recipe.ConditionedShapedRecipe;
import com.wuest.prefab.recipe.ConditionedShaplessRecipe;
import com.wuest.prefab.structures.config.BasicStructureConfiguration;
import com.wuest.prefab.structures.config.StructureConfiguration;
import com.wuest.prefab.structures.items.*;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * This is the mod registry so there is a way to get to all instances of the blocks/items created by this mod.
 *
 * @author WuestMan
 */
public class ModRegistry {
    public static ArrayList<Consumer<Object>> guiRegistrations = new ArrayList<>();

    public static final ItemGroup PREFAB_GROUP = FabricItemGroupBuilder.build(
            new Identifier(Prefab.MODID, "logo"),
            () -> new ItemStack(ModRegistry.LogoItem));

    /* *********************************** Blocks *********************************** */

    public static final BlockCompressedStone CompressedStone = new BlockCompressedStone(BlockCompressedStone.EnumType.COMPRESSED_STONE);
    public static final BlockCompressedStone DoubleCompressedStone = new BlockCompressedStone(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE);
    public static final BlockCompressedStone TripleCompressedStone = new BlockCompressedStone(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE);
    public static final BlockCompressedStone CompressedDirt = new BlockCompressedStone(BlockCompressedStone.EnumType.COMPRESSED_DIRT);
    public static final BlockCompressedStone DoubleCompressedDirt = new BlockCompressedStone(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT);
    public static final BlockCompressedStone CompressedGlowstone = new BlockCompressedStone(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE);
    public static final BlockCompressedStone DoubleCompressedGlowstone = new BlockCompressedStone(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE);
    public static final BlockCompressedObsidian CompressedObsidian = new BlockCompressedObsidian(BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN);
    public static final BlockCompressedObsidian DoubleCompressedObsidian = new BlockCompressedObsidian(BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN);
    public static final BlockGlassSlab GlassSlab = new BlockGlassSlab(AbstractBlock.Settings.copy(Blocks.GLASS));
    public static final BlockGlassStairs GlassStairs = new BlockGlassStairs(Blocks.GLASS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.GLASS));
    public static final BlockPaperLantern PaperLantern = new BlockPaperLantern();
    public static final BlockPhasic Phasic = new BlockPhasic();
    public static final BlockBoundary Boundary = new BlockBoundary();

    /* *********************************** Item Blocks *********************************** */

    public static final BlockItem CompressedStoneItem = new BlockItem(ModRegistry.CompressedStone, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem DoubleCompressedStoneItem = new BlockItem(ModRegistry.DoubleCompressedStone, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem TripleCompressedStoneItem = new BlockItem(ModRegistry.TripleCompressedStone, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem CompressedDirtItem = new BlockItem(ModRegistry.CompressedDirt, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem DoubleCompressedDirtItem = new BlockItem(ModRegistry.DoubleCompressedDirt, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem CompressedGlowstoneItem = new BlockItem(ModRegistry.CompressedGlowstone, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem DoubleCompressedGlowstoneItem = new BlockItem(ModRegistry.DoubleCompressedGlowstone, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem CompressedObsidianItem = new BlockItem(ModRegistry.CompressedObsidian, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem DoubleCompressedObsidianItem = new BlockItem(ModRegistry.DoubleCompressedObsidian, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem GlassSlabItem = new BlockItem(ModRegistry.GlassSlab, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem GlassStairsItem = new BlockItem(ModRegistry.GlassStairs, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem PaperLanternItem = new BlockItem(ModRegistry.PaperLantern, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem PhasicItem = new BlockItem(ModRegistry.Phasic, new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final BlockItem BoundaryItem = new BlockItem(ModRegistry.Boundary, new Item.Settings().group(ModRegistry.PREFAB_GROUP));

    /* *********************************** Messages *********************************** */

    public static final Identifier ConfigSync = new Identifier(Prefab.MODID, "config_sync");
    public static final Identifier PlayerConfigSync = new Identifier(Prefab.MODID, "player_config_sync");
    public static final Identifier StructureBuild = new Identifier(Prefab.MODID, "structure_build");

    /* *********************************** Items *********************************** */

    public static final Item PileOfBricks = new Item(new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final Item PalletOfBricks = new Item(new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final Item BundleOfTimber = new Item(new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final Item HeapOfTimber = new Item(new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final Item TonOfTimber = new Item(new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final Item StringOfLanterns = new Item(new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final Item CoilOfLanterns = new Item(new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final ItemCompressedChest CompressedChest = new ItemCompressedChest();
    public static final Item WarehouseUpgrade = new Item(new Item.Settings().group(ModRegistry.PREFAB_GROUP));
    public static final Item LogoItem = new Item(new Item.Settings());

    /* *********************************** Blueprint Items *********************************** */

    public static final ItemWareHouse Warehouse = new ItemWareHouse();
    public static final ItemChickenCoop ChickenCoop = new ItemChickenCoop();
    public static final ItemProduceFarm ProduceFarm = new ItemProduceFarm();
    public static final ItemTreeFarm TreeFarm = new ItemTreeFarm();
    public static final ItemFishPond FishPond = new ItemFishPond();
    public static final ItemAdvancedWareHouse AdvancedWareHouse = new ItemAdvancedWareHouse();
    public static final ItemMonsterMasher MonsterMasher = new ItemMonsterMasher();
    public static final ItemHorseStable HorseStable = new ItemHorseStable();
    public static final ItemInstantBridge InstantBridge = new ItemInstantBridge();
    public static final ItemModerateHouse ModerateHouse = new ItemModerateHouse();
    public static final ItemStartHouse StartHouse = new ItemStartHouse();
    public static final ItemBulldozer Bulldozer = new ItemBulldozer();
    public static final ItemBulldozer CreativeBulldozer = new ItemBulldozer(true);
    public static final ItemStructurePart StructurePart = new ItemStructurePart();
    public static final ItemVillagerHouses VillagerHouses = new ItemVillagerHouses();

    public static final ItemBasicStructure Barn = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.Barn);
    public static final ItemBasicStructure AdvancedCoop = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.AdvancedCoop);
    public static final ItemBasicStructure AdvancedHorseStable = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.AdvancedHorseStable);
    public static final ItemBasicStructure MachineryTower = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.MachineryTower);
    public static final ItemBasicStructure DefenseBunker = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.DefenseBunker);
    public static final ItemBasicStructure MineshaftEntrance = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.MineshaftEntrance);
    public static final ItemBasicStructure EnderGateway = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.EnderGateway);
    public static final ItemBasicStructure AquaBase = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.AquaBase);
    public static final ItemBasicStructure GrassyPlain = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.GrassyPlain);
    public static final ItemBasicStructure MagicTemple = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.MagicTemple);
    public static final ItemBasicStructure GreenHouse = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.GreenHouse);
    public static final ItemBasicStructure WatchTower = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WatchTower);
    public static final ItemBasicStructure WelcomeCenter = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WelcomeCenter);
    public static final ItemBasicStructure Jail = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.Jail);
    public static final ItemBasicStructure Saloon = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.Saloon);
    public static final ItemBasicStructure SkiLodge = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.SkiLodge);
    public static final ItemBasicStructure WindMill = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WindMill);
    public static final ItemBasicStructure TownHall = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.TownHall);
    public static final ItemBasicStructure NetherGate = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.NetherGate);
    public static final ItemBasicStructure SugarCaneFarm = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.SugarCaneFarm);
    public static final ItemBasicStructure AdvancedAquaBase = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.AdvancedAquaBase);
    public static final ItemBasicStructure WorkShop = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WorkShop);

    /* *********************************** Recipe Serializers *********************************** */
    public static final RecipeSerializer<ConditionedShapedRecipe> ConditionedShapedRecipeSeriaizer = new ConditionedShapedRecipe.Serializer();
    public static final RecipeSerializer<ConditionedShaplessRecipe> ConditionedShapelessRecipeSeriaizer = new ConditionedShaplessRecipe.Serializer();

    public static void registerModComponents() {
        ModRegistry.registerBlocks();

        ModRegistry.registerItems();

        ModRegistry.registerBluePrints();

        ModRegistry.registerItemBlocks();

        ModRegistry.RegisterClientToServerMessageHandlers();

        ModRegistry.RegisterRecipeSerializers();
    }

    private static void registerBlocks() {
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.CompressedStone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.DoubleCompressedStone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.TripleCompressedStone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_DIRT.getUnlocalizedName(), ModRegistry.CompressedDirt);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT.getUnlocalizedName(), ModRegistry.DoubleCompressedDirt);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistry.CompressedGlowstone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistry.DoubleCompressedGlowstone);
        ModRegistry.registerBlock(BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.asString(), ModRegistry.CompressedObsidian);
        ModRegistry.registerBlock(BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.asString(), ModRegistry.DoubleCompressedObsidian);
        ModRegistry.registerBlock("block_glass_slab", ModRegistry.GlassSlab);
        ModRegistry.registerBlock("block_glass_stairs", ModRegistry.GlassStairs);
        ModRegistry.registerBlock("block_paper_lantern", ModRegistry.PaperLantern);
        ModRegistry.registerBlock("block_phasic", ModRegistry.Phasic);
        ModRegistry.registerBlock("block_boundary", ModRegistry.Boundary);
    }

    private static void registerItems() {
        ModRegistry.registerItem("item_logo", ModRegistry.LogoItem);
        ModRegistry.registerItem("item_pile_of_bricks", ModRegistry.PileOfBricks);
        ModRegistry.registerItem("item_pallet_of_bricks", ModRegistry.PalletOfBricks);
        ModRegistry.registerItem("item_bundle_of_timber", ModRegistry.BundleOfTimber);
        ModRegistry.registerItem("item_heap_of_timber", ModRegistry.HeapOfTimber);
        ModRegistry.registerItem("item_ton_of_timber", ModRegistry.TonOfTimber);
        ModRegistry.registerItem("item_string_of_lanterns", ModRegistry.StringOfLanterns);
        ModRegistry.registerItem("item_coil_of_lanterns", ModRegistry.CoilOfLanterns);
        ModRegistry.registerItem("item_compressed_chest", ModRegistry.CompressedChest);
        ModRegistry.registerItem("item_warehouse_upgrade", ModRegistry.WarehouseUpgrade);
    }

    private static void registerBluePrints() {
        ModRegistry.registerItem("item_start_house", ModRegistry.StartHouse);
        ModRegistry.registerItem("item_warehouse", ModRegistry.Warehouse);
        ModRegistry.registerItem("item_chicken_coop", ModRegistry.ChickenCoop);
        ModRegistry.registerItem("item_produce_farm", ModRegistry.ProduceFarm);
        ModRegistry.registerItem("item_tree_farm", ModRegistry.TreeFarm);
        ModRegistry.registerItem("item_fish_pond", ModRegistry.FishPond);
        ModRegistry.registerItem("item_advanced_warehouse", ModRegistry.AdvancedWareHouse);
        ModRegistry.registerItem("item_monster_masher", ModRegistry.MonsterMasher);
        ModRegistry.registerItem("item_horse_stable", ModRegistry.HorseStable);
        ModRegistry.registerItem("item_instant_bridge", ModRegistry.InstantBridge);
        ModRegistry.registerItem("item_moderate_house", ModRegistry.ModerateHouse);
        ModRegistry.registerItem("item_bulldozer", ModRegistry.Bulldozer);
        ModRegistry.registerItem("item_creative_bulldozer", ModRegistry.CreativeBulldozer);
        ModRegistry.registerItem("item_structure_part", ModRegistry.StructurePart);
        ModRegistry.registerItem("item_villager_houses", ModRegistry.VillagerHouses);

        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Barn.getItemTextureLocation().getPath(), ModRegistry.Barn);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.AdvancedCoop.getItemTextureLocation().getPath(), ModRegistry.AdvancedCoop);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.AdvancedHorseStable.getItemTextureLocation().getPath(), ModRegistry.AdvancedHorseStable);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MachineryTower.getItemTextureLocation().getPath(), ModRegistry.MachineryTower);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.DefenseBunker.getItemTextureLocation().getPath(), ModRegistry.DefenseBunker);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MineshaftEntrance.getItemTextureLocation().getPath(), ModRegistry.MineshaftEntrance);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.EnderGateway.getItemTextureLocation().getPath(), ModRegistry.EnderGateway);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.AquaBase.getItemTextureLocation().getPath(), ModRegistry.AquaBase);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.GrassyPlain.getItemTextureLocation().getPath(), ModRegistry.GrassyPlain);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MagicTemple.getItemTextureLocation().getPath(), ModRegistry.MagicTemple);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.GreenHouse.getItemTextureLocation().getPath(), ModRegistry.GreenHouse);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WatchTower.getItemTextureLocation().getPath(), ModRegistry.WatchTower);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WelcomeCenter.getItemTextureLocation().getPath(), ModRegistry.WelcomeCenter);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Jail.getItemTextureLocation().getPath(), ModRegistry.Jail);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Saloon.getItemTextureLocation().getPath(), ModRegistry.Saloon);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.SkiLodge.getItemTextureLocation().getPath(), ModRegistry.SkiLodge);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WindMill.getItemTextureLocation().getPath(), ModRegistry.WindMill);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.TownHall.getItemTextureLocation().getPath(), ModRegistry.TownHall);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.NetherGate.getItemTextureLocation().getPath(), ModRegistry.NetherGate);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.SugarCaneFarm.getItemTextureLocation().getPath(), ModRegistry.SugarCaneFarm);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.AdvancedAquaBase.getItemTextureLocation().getPath(), ModRegistry.AdvancedAquaBase);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WorkShop.getItemTextureLocation().getPath(), ModRegistry.WorkShop);
    }

    private static void registerItemBlocks() {
        ModRegistry.registerItem(BlockCompressedStone.EnumType.COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.CompressedStoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.DoubleCompressedStoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.TripleCompressedStoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.COMPRESSED_DIRT.getUnlocalizedName(), ModRegistry.CompressedDirtItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT.getUnlocalizedName(), ModRegistry.DoubleCompressedDirtItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistry.CompressedGlowstoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistry.DoubleCompressedGlowstoneItem);
        ModRegistry.registerItem(BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.asString(), ModRegistry.CompressedObsidianItem);
        ModRegistry.registerItem(BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.asString(), ModRegistry.DoubleCompressedObsidianItem);
        ModRegistry.registerItem("block_glass_slab", ModRegistry.GlassSlabItem);
        ModRegistry.registerItem("block_glass_stairs", ModRegistry.GlassStairsItem);
        ModRegistry.registerItem("block_paper_lantern", ModRegistry.PaperLanternItem);
        ModRegistry.registerItem("block_phasic", ModRegistry.PhasicItem);
        ModRegistry.registerItem("block_boundary", ModRegistry.BoundaryItem);
    }

    /**
     * This is where the mod messages are registered.
     */
    private static void RegisterClientToServerMessageHandlers() {

        ModRegistry.registerStructureBuilderMessageHandler();
    }

    private static void RegisterRecipeSerializers() {
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(Prefab.MODID, "condition_crafting_shaped"), ModRegistry.ConditionedShapedRecipeSeriaizer);
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(Prefab.MODID, "condition_crafting_shapeless"), ModRegistry.ConditionedShapelessRecipeSeriaizer);
    }

    private static void registerBlock(String registryName, Block block) {
        Registry.register(Registry.BLOCK, new Identifier(Prefab.MODID, registryName), block);
    }

    private static void registerItem(String registryName, Item item) {
        Registry.register(Registry.ITEM, new Identifier(Prefab.MODID, registryName), item);
    }

    private static void registerStructureBuilderMessageHandler() {
        ServerSidePacketRegistry.INSTANCE.register(ModRegistry.StructureBuild,
                (packetContext, attachedData) -> {
                    // Can only access the "attachedData" on the "network thread" which is here.
                    StructureTagMessage message = StructureTagMessage.decode(attachedData);
                    StructureTagMessage.EnumStructureConfiguration structureConfig = message.getStructureConfig();

                    packetContext.getTaskQueue().execute(() -> {
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) packetContext.getPlayer();
                        // This is now on the "main" server thread and things can be done in the world!
                        StructureConfiguration configuration = structureConfig.structureConfig.ReadFromCompoundNBT(message.getMessageTag());

                        configuration.BuildStructure(serverPlayerEntity, serverPlayerEntity.getServerWorld());
                    });
                }
        );
    }
}