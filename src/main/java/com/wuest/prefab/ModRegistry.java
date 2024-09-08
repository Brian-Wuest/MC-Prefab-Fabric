package com.wuest.prefab;

import com.wuest.prefab.blocks.*;
import com.wuest.prefab.blocks.entities.LightSwitchBlockEntity;
import com.wuest.prefab.blocks.entities.StructureScannerBlockEntity;
import com.wuest.prefab.config.StructureScannerConfig;
import com.wuest.prefab.items.*;
import com.wuest.prefab.recipe.ConditionedShapedRecipe;
import com.wuest.prefab.recipe.ConditionedShapelessRecipe;
import com.wuest.prefab.recipe.ConditionedSmeltingRecipe;
import com.wuest.prefab.registries.ModRegistries;
import com.wuest.prefab.structures.config.BasicStructureConfiguration;
import com.wuest.prefab.structures.config.StructureConfiguration;
import com.wuest.prefab.structures.items.*;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.item.Tiers.WOOD;

/**
 * This is the mod registry so there is a way to get to all instances of the blocks/items created by this mod.
 *
 * @author WuestMan
 */
public class ModRegistry {
    public static final ArrayList<Consumer<Object>> guiRegistrations = new ArrayList<>();

    public static ModRegistries serverModRegistries;

    /* *********************************** Blocks *********************************** */
    public static final BlockCompressedStone CompressedStone = new BlockCompressedStone(BlockCompressedStone.EnumType.COMPRESSED_STONE);
    public static final BlockCompressedStone DoubleCompressedStone = new BlockCompressedStone(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE);
    public static final BlockCompressedStone TripleCompressedStone = new BlockCompressedStone(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE);
    public static final BlockCompressedStone CompressedDirt = new BlockCompressedStone(BlockCompressedStone.EnumType.COMPRESSED_DIRT);
    public static final BlockCompressedStone DoubleCompressedDirt = new BlockCompressedStone(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT);
    public static final BlockCompressedStone CompressedGlowstone = new BlockCompressedStone(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE);
    public static final BlockCompressedStone DoubleCompressedGlowstone = new BlockCompressedStone(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE);
    public static final BlockCompressedStone CompressedQuartzCrete = new BlockCompressedStone(BlockCompressedStone.EnumType.COMPRESSED_QUARTZCRETE);
    public static final BlockCompressedStone DoubleCompressedQuartzCrete = new BlockCompressedStone(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_QUARTZCRETE);

    public static final BlockCompressedObsidian CompressedObsidian = new BlockCompressedObsidian(BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN);
    public static final BlockCompressedObsidian DoubleCompressedObsidian = new BlockCompressedObsidian(BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN);
    public static final BlockGlassSlab GlassSlab = new BlockGlassSlab(Block.Properties.ofFullCopy(Blocks.GLASS));
    public static final BlockGlassStairs GlassStairs = new BlockGlassStairs(Blocks.GLASS.defaultBlockState(), Block.Properties.ofFullCopy(Blocks.GLASS));
    public static final BlockPaperLantern PaperLantern = new BlockPaperLantern();
    public static final BlockPhasic Phasic = new BlockPhasic();
    public static final BlockBoundary Boundary = new BlockBoundary();
    public static final BlockGrassSlab GrassSlab = new BlockGrassSlab();
    public static final BlockGrassStairs GrassStairs = new BlockGrassStairs();
    public static final BlockCustomWall GrassWall = new BlockCustomWall(Blocks.GRASS_BLOCK, BlockCustomWall.EnumType.GRASS);
    public static final BlockCustomWall DirtWall = new BlockCustomWall(Blocks.DIRT, BlockCustomWall.EnumType.DIRT);
    public static final BlockDirtStairs DirtStairs = new BlockDirtStairs();
    public static final BlockDirtSlab DirtSlab = new BlockDirtSlab();
    public static BlockStructureScanner StructureScanner = null;
    public static BlockLightSwitch LightSwitch = new BlockLightSwitch();
    public static BlockDarkLamp DarkLamp = new BlockDarkLamp();
    public static final BlockRotatableHorizontalShaped PileOfBricks = new BlockRotatableHorizontalShaped(BlockShaped.BlockShape.PileOfBricks, Block.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.COLOR_RED).noOcclusion().isViewBlocking(ModRegistry::never));
    public static final BlockRotatableHorizontalShaped PalletOfBricks = new BlockRotatableHorizontalShaped(BlockShaped.BlockShape.PalletOfBricks, Block.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.COLOR_RED).noOcclusion().isViewBlocking(ModRegistry::never));
    public static final BlockRotatableHorizontalShaped BundleOfTimber = new BlockRotatableHorizontalShaped(BlockShaped.BlockShape.BundleOfTimber, Block.Properties.ofFullCopy(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noOcclusion().isViewBlocking(ModRegistry::never));
    public static final BlockRotatableHorizontalShaped HeapOfTimber = new BlockRotatableHorizontalShaped(BlockShaped.BlockShape.HeapOfTimber, Block.Properties.ofFullCopy(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noOcclusion().isViewBlocking(ModRegistry::never));
    public static final BlockRotatableHorizontalShaped TonOfTimber = new BlockRotatableHorizontalShaped(BlockShaped.BlockShape.TonOfTimber, Block.Properties.ofFullCopy(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noOcclusion().isViewBlocking(ModRegistry::never));
    public static final BlockRotatable EmptyCrate = new BlockRotatable(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).sound(SoundType.WOOD));
    public static final BlockRotatable CartonOfEggs = new BlockRotatable(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).sound(SoundType.WOOD));
    public static final BlockRotatable CrateOfPotatoes = new BlockRotatable(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).sound(SoundType.WOOD));
    public static final BlockRotatable CrateOfCarrots = new BlockRotatable(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).sound(SoundType.WOOD));
    public static final BlockRotatable CrateOfBeets = new BlockRotatable(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).sound(SoundType.WOOD));
    public static final Block QuartzCrete = new Block(Block.Properties.ofFullCopy(Blocks.QUARTZ_BLOCK));
    public static final WallBlock QuartzCreteWall = new WallBlock(Block.Properties.ofFullCopy(ModRegistry.QuartzCrete));
    public static final Block QuartzCreteBricks = new Block(Block.Properties.ofFullCopy(ModRegistry.QuartzCrete));
    public static final Block ChiseledQuartzCrete = new Block(Block.Properties.ofFullCopy(Blocks.CHISELED_QUARTZ_BLOCK));
    public static final RotatedPillarBlock QuartzCretePillar = new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.QUARTZ_PILLAR));
    public static final BlockCustomStairs QuartzCreteStairs = new BlockCustomStairs(ModRegistry.QuartzCrete.defaultBlockState(), Block.Properties.ofFullCopy(ModRegistry.QuartzCrete));
    public static final SlabBlock QuartzCreteSlab = new SlabBlock(Block.Properties.ofFullCopy(ModRegistry.QuartzCrete));
    public static final Block SmoothQuartzCrete = new Block(Block.Properties.ofFullCopy(ModRegistry.QuartzCrete));
    public static final WallBlock SmoothQuartzCreteWall = new WallBlock(Block.Properties.ofFullCopy(ModRegistry.SmoothQuartzCrete));
    public static final BlockCustomStairs SmoothQuartzCreteStairs = new BlockCustomStairs(ModRegistry.SmoothQuartzCrete.defaultBlockState(), Block.Properties.ofFullCopy(ModRegistry.SmoothQuartzCrete));
    public static final SlabBlock SmoothQuartzCreteSlab = new SlabBlock(Block.Properties.ofFullCopy(SmoothQuartzCrete));

    /* *********************************** Messages *********************************** */
    public static final ResourceLocation ConfigSync = new ResourceLocation(Prefab.MODID, "config_sync");
    public static final ResourceLocation PlayerConfigSync = new ResourceLocation(Prefab.MODID, "player_config_sync");
    public static final ResourceLocation StructureBuild = new ResourceLocation(Prefab.MODID, "structure_build");
    public static final ResourceLocation StructureScannerSync = new ResourceLocation(Prefab.MODID, "structure_scanner_sync");
    public static final ResourceLocation StructureScannerAction = new ResourceLocation(Prefab.MODID, "structure_scanner_action");
    public static final Item LogoItem = new Item(new Item.Properties());

    /* *********************************** Item Blocks *********************************** */
    public static final BlockItem CompressedStoneItem = new BlockItem(ModRegistry.CompressedStone, new Item.Properties());
    public static final BlockItem DoubleCompressedStoneItem = new BlockItem(ModRegistry.DoubleCompressedStone, new Item.Properties());
    public static final BlockItem TripleCompressedStoneItem = new BlockItem(ModRegistry.TripleCompressedStone, new Item.Properties());
    public static final BlockItem CompressedDirtItem = new BlockItem(ModRegistry.CompressedDirt, new Item.Properties());
    public static final BlockItem DoubleCompressedDirtItem = new BlockItem(ModRegistry.DoubleCompressedDirt, new Item.Properties());
    public static final BlockItem CompressedGlowstoneItem = new BlockItem(ModRegistry.CompressedGlowstone, new Item.Properties());
    public static final BlockItem DoubleCompressedGlowstoneItem = new BlockItem(ModRegistry.DoubleCompressedGlowstone, new Item.Properties());
    public static final BlockItem CompressedQuartzCreteItem = new BlockItem(ModRegistry.CompressedQuartzCrete, new Item.Properties());
    public static final BlockItem DoubleCompressedQuartzCreteItem = new BlockItem(ModRegistry.DoubleCompressedQuartzCrete, new Item.Properties());

    public static final BlockItem CompressedObsidianItem = new BlockItem(ModRegistry.CompressedObsidian, new Item.Properties());
    public static final BlockItem DoubleCompressedObsidianItem = new BlockItem(ModRegistry.DoubleCompressedObsidian, new Item.Properties());
    public static final BlockItem GlassSlabItem = new BlockItem(ModRegistry.GlassSlab, new Item.Properties());
    public static final BlockItem GlassStairsItem = new BlockItem(ModRegistry.GlassStairs, new Item.Properties());
    public static final BlockItem PaperLanternItem = new BlockItem(ModRegistry.PaperLantern, new Item.Properties());
    public static final BlockItem PhasicItem = new BlockItem(ModRegistry.Phasic, new Item.Properties());
    public static final BlockItem BoundaryItem = new BlockItem(ModRegistry.Boundary, new Item.Properties());
    public static final BlockItem GrassSlabItem = new BlockItem(ModRegistry.GrassSlab, new Item.Properties());
    public static final BlockItem GrassStairsItem = new BlockItem(ModRegistry.GrassStairs, new Item.Properties());
    public static final BlockItem GrassWallItem = new BlockItem(ModRegistry.GrassWall, new Item.Properties());
    public static final BlockItem DirtWallItem = new BlockItem(ModRegistry.DirtWall, new Item.Properties());
    public static final BlockItem DirtStairsItem = new BlockItem(ModRegistry.DirtStairs, new Item.Properties());
    public static final BlockItem DirtSlabItem = new BlockItem(ModRegistry.DirtSlab, new Item.Properties());
    public static BlockItem StructureScannerItem = null;
    public static BlockItem LightSwitchItem = new BlockItem(ModRegistry.LightSwitch, new Item.Properties());
    public static BlockItem DarkLampItem = new BlockItem(ModRegistry.DarkLamp, new Item.Properties());
    public static final BlockItem QuartzCreteItem = new BlockItem(ModRegistry.QuartzCrete, new Item.Properties());
    public static final BlockItem QuartzCreteWallItem = new BlockItem(ModRegistry.QuartzCreteWall, new Item.Properties());
    public static final BlockItem QuartzCreteBricksItem = new BlockItem(ModRegistry.QuartzCreteBricks, new Item.Properties());
    public static final BlockItem ChiseledQuartzCreteItem = new BlockItem(ModRegistry.ChiseledQuartzCrete, new Item.Properties());
    public static final BlockItem QuartzCretePillarItem = new BlockItem(ModRegistry.QuartzCretePillar, new Item.Properties());
    public static final BlockItem QuartzCreteStairsItem = new BlockItem(ModRegistry.QuartzCreteStairs, new Item.Properties());
    public static final BlockItem QuartzCreteSlabItem = new BlockItem(ModRegistry.QuartzCreteSlab, new Item.Properties());
    public static final BlockItem SmoothQuartzCreteItem = new BlockItem(ModRegistry.SmoothQuartzCrete, new Item.Properties());
    public static final BlockItem SmoothQuartzCreteWallItem = new BlockItem(ModRegistry.SmoothQuartzCreteWall, new Item.Properties());
    public static final BlockItem SmoothQuartzCreteStairsItem = new BlockItem(ModRegistry.SmoothQuartzCreteStairs, new Item.Properties());
    public static final BlockItem SmoothQuartzCreteSlabItem = new BlockItem(ModRegistry.SmoothQuartzCreteSlab, new Item.Properties());

    /* *********************************** Items *********************************** */
    public static final ItemCompressedChest CompressedChest = new ItemCompressedChest();
    public static final Item ItemPileOfBricks = new BlockItem(ModRegistry.PileOfBricks, new Item.Properties());
    public static final Item ItemPalletOfBricks = new BlockItem(ModRegistry.PalletOfBricks, new Item.Properties());
    public static final Item ItemBundleOfTimber = new BlockItem(ModRegistry.BundleOfTimber, new Item.Properties());
    public static final Item ItemHeapOfTimber = new BlockItem(ModRegistry.HeapOfTimber, new Item.Properties());
    public static final Item ItemTonOfTimber = new BlockItem(ModRegistry.TonOfTimber, new Item.Properties());
    public static final Item StringOfLanterns = new Item(new Item.Properties());
    public static final Item CoilOfLanterns = new Item(new Item.Properties());
    public static final Item Upgrade = new Item(new Item.Properties());
    public static final Item SwiftBladeWood = new ItemSwiftBlade(WOOD, 2, .5f);
    public static final Item SwiftBladeStone = new ItemSwiftBlade(Tiers.STONE, 2, .5f);
    public static final Item SwiftBladeIron = new ItemSwiftBlade(Tiers.IRON, 2, .5f);
    public static final Item SwiftBladeDiamond = new ItemSwiftBlade(Tiers.DIAMOND, 2, .5f);
    public static final Item SwiftBladeGold = new ItemSwiftBlade(Tiers.GOLD, 2, .5f);
    public static final Item SwiftBladeCopper = new ItemSwiftBlade(CustomItemTier.COPPER, 2, .5f);
    public static final Item SwiftBladeOsmium = new ItemSwiftBlade(CustomItemTier.OSMIUM, 2, .5f);
    public static final Item SwiftBladeBronze = new ItemSwiftBlade(CustomItemTier.BRONZE, 2, .5f);
    public static final Item SwiftBladeSteel = new ItemSwiftBlade(CustomItemTier.STEEL, 2, .5f);
    public static final Item SwiftBladeObsidian = new ItemSwiftBlade(CustomItemTier.OBSIDIAN, 2, .5f);
    public static final Item SwiftBladeNetherite = new ItemSwiftBlade(Tiers.NETHERITE, 2, .5f);
    public static final ItemSickle SickleWood = new ItemSickle(WOOD);
    public static final ItemSickle SickleStone = new ItemSickle(Tiers.STONE);
    public static final ItemSickle SickleGold = new ItemSickle(Tiers.GOLD);
    public static final ItemSickle SickleIron = new ItemSickle(Tiers.IRON);
    public static final ItemSickle SickleDiamond = new ItemSickle(Tiers.DIAMOND);
    public static final ItemSickle SickleNetherite = new ItemSickle(Tiers.NETHERITE);

    // Note: Empty crate must be registered first to avoid null-pointer errors with the rest of the ItemWoodenCrate items.
    public static final ItemBlockWoodenCrate ItemEmptyCrate = new ItemBlockWoodenCrate(ModRegistry.EmptyCrate, ItemWoodenCrate.CrateType.Empty);
    public static final ItemWoodenCrate ClutchOfEggs = new ItemWoodenCrate(ItemWoodenCrate.CrateType.Clutch_Of_Eggs);
    public static final ItemBlockWoodenCrate ItemCartonOfEggs = new ItemBlockWoodenCrate(ModRegistry.CartonOfEggs, ItemWoodenCrate.CrateType.Carton_Of_Eggs);
    public static final ItemWoodenCrate BunchOfPotatoes = new ItemWoodenCrate(ItemWoodenCrate.CrateType.Bunch_Of_Potatoes);
    public static final ItemBlockWoodenCrate ItemCrateOfPotatoes = new ItemBlockWoodenCrate(ModRegistry.CrateOfPotatoes, ItemWoodenCrate.CrateType.Crate_Of_Potatoes);
    public static final ItemWoodenCrate BunchOfCarrots = new ItemWoodenCrate(ItemWoodenCrate.CrateType.Bunch_Of_Carrots);
    public static final ItemBlockWoodenCrate ItemCrateOfCarrots = new ItemBlockWoodenCrate(ModRegistry.CrateOfCarrots, ItemWoodenCrate.CrateType.Crate_Of_Carrots);
    public static final ItemWoodenCrate BunchOfBeets = new ItemWoodenCrate(ItemWoodenCrate.CrateType.Bunch_Of_Beets);
    public static final ItemBlockWoodenCrate ItemCrateOfBeets = new ItemBlockWoodenCrate(ModRegistry.CrateOfBeets, ItemWoodenCrate.CrateType.Crate_Of_Beets);

    /* *********************************** Blueprint Items *********************************** */
    public static final ItemInstantBridge InstantBridge = new ItemInstantBridge();
    public static final ItemHouse House = new ItemHouse();
    public static final ItemHouseImproved HouseImproved = new ItemHouseImproved();
    public static final ItemHouseAdvanced HouseAdvanced = new ItemHouseAdvanced();
    public static final ItemBulldozer Bulldozer = new ItemBulldozer();
    public static final ItemBulldozer CreativeBulldozer = new ItemBulldozer(true);
    public static final ItemBasicStructure MachineryTower = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.MachineryTower);
    public static final ItemBasicStructure DefenseBunker = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.DefenseBunker);
    public static final ItemBasicStructure MineshaftEntrance = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.MineshaftEntrance);
    public static final ItemBasicStructure EnderGateway = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.EnderGateway);
    public static final ItemBasicStructure AquaBase = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.AquaBase);
    public static final ItemBasicStructure GrassyPlain = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.GrassyPlain);
    public static final ItemBasicStructure MagicTemple = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.MagicTemple);
    public static final ItemBasicStructure WatchTower = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WatchTower);
    public static final ItemBasicStructure WelcomeCenter = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WelcomeCenter);
    public static final ItemBasicStructure Jail = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.Jail);
    public static final ItemBasicStructure Saloon = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.Saloon);
    public static final ItemBasicStructure SkiLodge = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.SkiLodge);
    public static final ItemBasicStructure WindMill = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WindMill);
    public static final ItemBasicStructure TownHall = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.TownHall);
    public static final ItemBasicStructure NetherGate = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.NetherGate);
    public static final ItemBasicStructure AquaBaseImproved = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.AquaBaseImproved);
    public static final ItemBasicStructure Warehouse = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.Warehouse);
    public static final ItemBasicStructure WareHouseImproved = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WarehouseImproved);
    public static final ItemBasicStructure VillagerHouses = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.VillagerHouses, 10);
    public static final ItemBasicStructure ModernBuildings = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildings);
    public static final ItemBasicStructure ModernBuildingsImproved = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildingsImproved);
    public static final ItemBasicStructure ModernBuildingsAdvanced = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildingsAdvanced);
    public static final ItemBasicStructure Farm = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.Farm);
    public static final ItemBasicStructure FarmImproved = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.FarmImproved);
    public static final ItemBasicStructure FarmAdvanced = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.FarmAdvanced);

    /* *********************************** Recipe Serializers *********************************** */
    public static final RecipeSerializer<ConditionedShapedRecipe> ConditionedShapedRecipeSeriaizer = new ConditionedShapedRecipe.Serializer();
    public static final RecipeSerializer<ConditionedShapelessRecipe> ConditionedShapelessRecipeSeriaizer = new ConditionedShapelessRecipe.Serializer();
    public static final RecipeSerializer<ConditionedSmeltingRecipe> ConditionedSmeltingRecipeSeriaizer = new ConditionedSmeltingRecipe.Serializer();

    /* *********************************** Sounds *********************************** */
    public static final SoundEvent BuildingBlueprint = SoundEvent.createVariableRangeEvent(new ResourceLocation(Prefab.MODID, "building_blueprint"));

    /* *********************************** Block Entities Types *********************************** */
    public static BlockEntityType<StructureScannerBlockEntity> StructureScannerEntityType;
    public static BlockEntityType<LightSwitchBlockEntity> LightSwitchEntityType;

    /* *********************************** Block Entities *********************************** */
    public static StructureScannerBlockEntity StructureScannerEntity;

    /* *********************************** Item Group *********************************** */
    private static final CreativeModeTab ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModRegistry.LogoItem))
            .displayItems((context, entries) -> {
                entries.accept(ModRegistry.CompressedStoneItem);
                entries.accept(ModRegistry.DoubleCompressedStoneItem);
                entries.accept(ModRegistry.TripleCompressedStoneItem);
                entries.accept(ModRegistry.CompressedDirtItem);
                entries.accept(ModRegistry.DoubleCompressedDirtItem);
                entries.accept(ModRegistry.CompressedGlowstoneItem);
                entries.accept(ModRegistry.DoubleCompressedGlowstoneItem);
                entries.accept(ModRegistry.CompressedQuartzCreteItem);
                entries.accept(ModRegistry.DoubleCompressedQuartzCreteItem);
                entries.accept(ModRegistry.CompressedObsidianItem);
                entries.accept(ModRegistry.DoubleCompressedObsidianItem);
                entries.accept(ModRegistry.GlassSlabItem);
                entries.accept(ModRegistry.GlassStairsItem);
                entries.accept(ModRegistry.PaperLanternItem);
                entries.accept(ModRegistry.PhasicItem);
                entries.accept(ModRegistry.BoundaryItem);
                entries.accept(ModRegistry.GrassSlabItem);
                entries.accept(ModRegistry.GrassStairsItem);
                entries.accept(ModRegistry.GrassWallItem);
                entries.accept(ModRegistry.DirtWallItem);
                entries.accept(ModRegistry.DirtStairsItem);
                entries.accept(ModRegistry.DirtSlabItem);
                entries.accept(ModRegistry.LightSwitchItem);
                entries.accept(ModRegistry.DarkLampItem);
                entries.accept(ModRegistry.QuartzCreteItem);
                entries.accept(ModRegistry.QuartzCreteWallItem);
                entries.accept(ModRegistry.QuartzCreteBricksItem);
                entries.accept(ModRegistry.ChiseledQuartzCreteItem);
                entries.accept(ModRegistry.QuartzCretePillarItem);
                entries.accept(ModRegistry.QuartzCreteStairsItem);
                entries.accept(ModRegistry.QuartzCreteSlabItem);
                entries.accept(ModRegistry.SmoothQuartzCreteItem);
                entries.accept(ModRegistry.SmoothQuartzCreteWallItem);
                entries.accept(ModRegistry.SmoothQuartzCreteStairsItem);
                entries.accept(ModRegistry.SmoothQuartzCreteSlabItem);

                entries.accept(ModRegistry.CompressedChest);
                entries.accept(ModRegistry.ItemPileOfBricks);
                entries.accept(ModRegistry.ItemPalletOfBricks);
                entries.accept(ModRegistry.ItemBundleOfTimber);
                entries.accept(ModRegistry.ItemHeapOfTimber);
                entries.accept(ModRegistry.ItemTonOfTimber);
                entries.accept(ModRegistry.StringOfLanterns);
                entries.accept(ModRegistry.CoilOfLanterns);
                entries.accept(ModRegistry.Upgrade);
                entries.accept(ModRegistry.SwiftBladeWood);
                entries.accept(ModRegistry.SwiftBladeStone);
                entries.accept(ModRegistry.SwiftBladeIron);
                entries.accept(ModRegistry.SwiftBladeDiamond);
                entries.accept(ModRegistry.SwiftBladeGold);
                entries.accept(ModRegistry.SwiftBladeCopper);
                entries.accept(ModRegistry.SwiftBladeOsmium);
                entries.accept(ModRegistry.SwiftBladeBronze);
                entries.accept(ModRegistry.SwiftBladeSteel);
                entries.accept(ModRegistry.SwiftBladeObsidian);
                entries.accept(ModRegistry.SwiftBladeNetherite);
                entries.accept(ModRegistry.SickleWood);
                entries.accept(ModRegistry.SickleStone);
                entries.accept(ModRegistry.SickleGold);
                entries.accept(ModRegistry.SickleIron);
                entries.accept(ModRegistry.SickleDiamond);
                entries.accept(ModRegistry.SickleNetherite);
                entries.accept(ModRegistry.ItemEmptyCrate);
                entries.accept(ModRegistry.ClutchOfEggs);
                entries.accept(ModRegistry.ItemCartonOfEggs);
                entries.accept(ModRegistry.BunchOfPotatoes);
                entries.accept(ModRegistry.ItemCrateOfPotatoes);
                entries.accept(ModRegistry.BunchOfCarrots);
                entries.accept(ModRegistry.ItemCrateOfCarrots);
                entries.accept(ModRegistry.BunchOfBeets);
                entries.accept(ModRegistry.ItemCrateOfBeets);

                entries.accept(ModRegistry.InstantBridge);
                entries.accept(ModRegistry.House);
                entries.accept(ModRegistry.HouseImproved);
                entries.accept(ModRegistry.HouseAdvanced);
                entries.accept(ModRegistry.Bulldozer);
                entries.accept(ModRegistry.CreativeBulldozer);
                entries.accept(ModRegistry.MachineryTower);
                entries.accept(ModRegistry.DefenseBunker);
                entries.accept(ModRegistry.MineshaftEntrance);
                entries.accept(ModRegistry.EnderGateway);
                entries.accept(ModRegistry.GrassyPlain);
                entries.accept(ModRegistry.MagicTemple);
                entries.accept(ModRegistry.WatchTower);
                entries.accept(ModRegistry.WelcomeCenter);
                entries.accept(ModRegistry.Jail);
                entries.accept(ModRegistry.Saloon);
                entries.accept(ModRegistry.SkiLodge);
                entries.accept(ModRegistry.WindMill);
                entries.accept(ModRegistry.TownHall);
                entries.accept(ModRegistry.NetherGate);
                entries.accept(ModRegistry.AquaBase);
                entries.accept(ModRegistry.AquaBaseImproved);
                entries.accept(ModRegistry.Warehouse);
                entries.accept(ModRegistry.WareHouseImproved);
                entries.accept(ModRegistry.VillagerHouses);
                entries.accept(ModRegistry.ModernBuildings);
                entries.accept(ModRegistry.ModernBuildingsImproved);
                entries.accept(ModRegistry.ModernBuildingsAdvanced);
                entries.accept(ModRegistry.Farm);
                entries.accept(ModRegistry.FarmImproved);
                entries.accept(ModRegistry.FarmAdvanced);

                if (Prefab.isDebug) {
                    entries.accept(ModRegistry.StructureScannerItem);
                }
            })
            .title(Utils.createTextComponent("Prefab"))
            .build();

    public static final CreativeModeTab creativeModeTab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(Prefab.MODID, "logo"), ITEM_GROUP);


    public static void registerModComponents() {
        ModRegistry.registerSounds();

        ModRegistry.registerBlocks();

        ModRegistry.registerBlockEntities();

        ModRegistry.registerItems();

        ModRegistry.registerBluePrints();

        ModRegistry.registerItemBlocks();

        ModRegistry.RegisterClientToServerMessageHandlers();

        ModRegistry.RegisterRecipeSerializers();
    }

    private static void registerSounds() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Prefab.MODID, "building_blueprint"), ModRegistry.BuildingBlueprint);
    }

    private static void registerBlockEntities() {
        if (Prefab.isDebug) {
            StructureScannerEntityType = Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    "prefab:structure_scanner_entity",
                    FabricBlockEntityTypeBuilder
                            .create(StructureScannerBlockEntity::new, ModRegistry.StructureScanner)
                            .build(null));
        }

        ModRegistry.LightSwitchEntityType = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                "prefab:light_switch_entity",
                FabricBlockEntityTypeBuilder
                        .create(LightSwitchBlockEntity::new, ModRegistry.LightSwitch)
                        .build(null));
    }

    private static void registerBlocks() {
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.CompressedStone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.DoubleCompressedStone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.TripleCompressedStone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_DIRT.getUnlocalizedName(), ModRegistry.CompressedDirt);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT.getUnlocalizedName(), ModRegistry.DoubleCompressedDirt);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistry.CompressedGlowstone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistry.DoubleCompressedGlowstone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistry.CompressedQuartzCrete);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistry.DoubleCompressedQuartzCrete);
        ModRegistry.registerBlock(BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.toString(), ModRegistry.CompressedObsidian);
        ModRegistry.registerBlock(BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.toString(), ModRegistry.DoubleCompressedObsidian);
        ModRegistry.registerBlock("block_glass_slab", ModRegistry.GlassSlab);
        ModRegistry.registerBlock("block_glass_stairs", ModRegistry.GlassStairs);
        ModRegistry.registerBlock("block_paper_lantern", ModRegistry.PaperLantern);
        ModRegistry.registerBlock("block_phasic", ModRegistry.Phasic);
        ModRegistry.registerBlock("block_boundary", ModRegistry.Boundary);
        ModRegistry.registerBlock("block_grass_slab", ModRegistry.GrassSlab);
        ModRegistry.registerBlock("block_grass_stairs", ModRegistry.GrassStairs);
        ModRegistry.registerBlock(BlockCustomWall.EnumType.GRASS.getUnlocalizedName(), ModRegistry.GrassWall);
        ModRegistry.registerBlock(BlockCustomWall.EnumType.DIRT.getUnlocalizedName(), ModRegistry.DirtWall);
        ModRegistry.registerBlock("block_dirt_stairs", ModRegistry.DirtStairs);
        ModRegistry.registerBlock("block_dirt_slab", ModRegistry.DirtSlab);

        ModRegistry.registerBlock("item_pile_of_bricks", ModRegistry.PileOfBricks);
        ModRegistry.registerBlock("item_pallet_of_bricks", ModRegistry.PalletOfBricks);
        ModRegistry.registerBlock("item_bundle_of_timber", ModRegistry.BundleOfTimber);
        ModRegistry.registerBlock("item_heap_of_timber", ModRegistry.HeapOfTimber);
        ModRegistry.registerBlock("item_ton_of_timber", ModRegistry.TonOfTimber);

        ModRegistry.registerBlock("item_wooden_crate", ModRegistry.EmptyCrate);
        ModRegistry.registerBlock("item_carton_of_eggs", ModRegistry.CartonOfEggs);
        ModRegistry.registerBlock("item_crate_of_potatoes", ModRegistry.CrateOfPotatoes);
        ModRegistry.registerBlock("item_crate_of_carrots", ModRegistry.CrateOfCarrots);
        ModRegistry.registerBlock("item_crate_of_beets", ModRegistry.CrateOfBeets);

        if (Prefab.isDebug) {
            ModRegistry.StructureScanner = new BlockStructureScanner();
            ModRegistry.registerBlock("block_structure_scanner", ModRegistry.StructureScanner);
        }

        ModRegistry.registerBlock("block_light_switch", ModRegistry.LightSwitch);
        ModRegistry.registerBlock("block_dark_lamp", ModRegistry.DarkLamp);

        ModRegistry.registerBlock("block_quartz_crete", ModRegistry.QuartzCrete);
        ModRegistry.registerBlock("block_quartz_crete_wall", ModRegistry.QuartzCreteWall);
        ModRegistry.registerBlock("block_quartz_crete_bricks", ModRegistry.QuartzCreteBricks);
        ModRegistry.registerBlock("block_quartz_crete_chiseled", ModRegistry.ChiseledQuartzCrete);
        ModRegistry.registerBlock("block_quartz_crete_pillar", ModRegistry.QuartzCretePillar);
        ModRegistry.registerBlock("block_quartz_crete_stairs", ModRegistry.QuartzCreteStairs);
        ModRegistry.registerBlock("block_quartz_crete_slab", ModRegistry.QuartzCreteSlab);
        ModRegistry.registerBlock("block_quartz_crete_smooth", ModRegistry.SmoothQuartzCrete);
        ModRegistry.registerBlock("block_quartz_crete_smooth_wall", ModRegistry.SmoothQuartzCreteWall);
        ModRegistry.registerBlock("block_quartz_crete_smooth_stairs", ModRegistry.SmoothQuartzCreteStairs);
        ModRegistry.registerBlock("block_quartz_crete_smooth_slab", ModRegistry.SmoothQuartzCreteSlab);
    }

    private static void registerItems() {
        ModRegistry.registerItem("item_logo", ModRegistry.LogoItem);
        ModRegistry.registerItem("item_pile_of_bricks", ModRegistry.ItemPileOfBricks);
        ModRegistry.registerItem("item_pallet_of_bricks", ModRegistry.ItemPalletOfBricks);
        ModRegistry.registerItem("item_bundle_of_timber", ModRegistry.ItemBundleOfTimber);
        ModRegistry.registerItem("item_heap_of_timber", ModRegistry.ItemHeapOfTimber);
        ModRegistry.registerItem("item_ton_of_timber", ModRegistry.ItemTonOfTimber);
        ModRegistry.registerItem("item_string_of_lanterns", ModRegistry.StringOfLanterns);
        ModRegistry.registerItem("item_coil_of_lanterns", ModRegistry.CoilOfLanterns);
        ModRegistry.registerItem("item_compressed_chest", ModRegistry.CompressedChest);
        ModRegistry.registerItem("item_upgrade", ModRegistry.Upgrade);

        ModRegistry.registerItem("item_swift_blade_wood", ModRegistry.SwiftBladeWood);
        ModRegistry.registerItem("item_swift_blade_stone", ModRegistry.SwiftBladeStone);
        ModRegistry.registerItem("item_swift_blade_iron", ModRegistry.SwiftBladeIron);
        ModRegistry.registerItem("item_swift_blade_diamond", ModRegistry.SwiftBladeDiamond);
        ModRegistry.registerItem("item_swift_blade_gold", ModRegistry.SwiftBladeGold);
        ModRegistry.registerItem("item_swift_blade_copper", ModRegistry.SwiftBladeCopper);
        ModRegistry.registerItem("item_swift_blade_osmium", ModRegistry.SwiftBladeOsmium);
        ModRegistry.registerItem("item_swift_blade_bronze", ModRegistry.SwiftBladeBronze);
        ModRegistry.registerItem("item_swift_blade_steel", ModRegistry.SwiftBladeSteel);
        ModRegistry.registerItem("item_swift_blade_obsidian", ModRegistry.SwiftBladeObsidian);
        ModRegistry.registerItem("item_swift_blade_netherite", ModRegistry.SwiftBladeNetherite);

        ModRegistry.registerItem("item_sickle_wood", ModRegistry.SickleWood);
        ModRegistry.registerItem("item_sickle_stone", ModRegistry.SickleStone);
        ModRegistry.registerItem("item_sickle_gold", ModRegistry.SickleGold);
        ModRegistry.registerItem("item_sickle_iron", ModRegistry.SickleIron);
        ModRegistry.registerItem("item_sickle_diamond", ModRegistry.SickleDiamond);
        ModRegistry.registerItem("item_sickle_netherite", ModRegistry.SickleNetherite);

        ModRegistry.registerItem("item_wooden_crate", ModRegistry.ItemEmptyCrate);
        ModRegistry.registerItem("item_clutch_of_eggs", ModRegistry.ClutchOfEggs);
        ModRegistry.registerItem("item_carton_of_eggs", ModRegistry.ItemCartonOfEggs);
        ModRegistry.registerItem("item_bunch_of_potatoes", ModRegistry.BunchOfPotatoes);
        ModRegistry.registerItem("item_crate_of_potatoes", ModRegistry.ItemCrateOfPotatoes);
        ModRegistry.registerItem("item_bunch_of_carrots", ModRegistry.BunchOfCarrots);
        ModRegistry.registerItem("item_crate_of_carrots", ModRegistry.ItemCrateOfCarrots);
        ModRegistry.registerItem("item_bunch_of_beets", ModRegistry.BunchOfBeets);
        ModRegistry.registerItem("item_crate_of_beets", ModRegistry.ItemCrateOfBeets);
    }

    private static void registerBluePrints() {
        ModRegistry.registerItem("item_house", ModRegistry.House);
        ModRegistry.registerItem("item_instant_bridge", ModRegistry.InstantBridge);
        ModRegistry.registerItem("item_house_improved", ModRegistry.HouseImproved);
        ModRegistry.registerItem("item_house_advanced", ModRegistry.HouseAdvanced);
        ModRegistry.registerItem("item_bulldozer", ModRegistry.Bulldozer);
        ModRegistry.registerItem("item_creative_bulldozer", ModRegistry.CreativeBulldozer);

        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MachineryTower.getItemTextureLocation().getPath(), ModRegistry.MachineryTower);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.DefenseBunker.getItemTextureLocation().getPath(), ModRegistry.DefenseBunker);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MineshaftEntrance.getItemTextureLocation().getPath(), ModRegistry.MineshaftEntrance);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.EnderGateway.getItemTextureLocation().getPath(), ModRegistry.EnderGateway);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.AquaBase.getItemTextureLocation().getPath(), ModRegistry.AquaBase);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.GrassyPlain.getItemTextureLocation().getPath(), ModRegistry.GrassyPlain);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MagicTemple.getItemTextureLocation().getPath(), ModRegistry.MagicTemple);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WatchTower.getItemTextureLocation().getPath(), ModRegistry.WatchTower);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WelcomeCenter.getItemTextureLocation().getPath(), ModRegistry.WelcomeCenter);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Jail.getItemTextureLocation().getPath(), ModRegistry.Jail);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Saloon.getItemTextureLocation().getPath(), ModRegistry.Saloon);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.SkiLodge.getItemTextureLocation().getPath(), ModRegistry.SkiLodge);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WindMill.getItemTextureLocation().getPath(), ModRegistry.WindMill);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.TownHall.getItemTextureLocation().getPath(), ModRegistry.TownHall);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.NetherGate.getItemTextureLocation().getPath(), ModRegistry.NetherGate);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.AquaBaseImproved.getItemTextureLocation().getPath(), ModRegistry.AquaBaseImproved);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Warehouse.getItemTextureLocation().getPath(), ModRegistry.Warehouse);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WarehouseImproved.getItemTextureLocation().getPath(), ModRegistry.WareHouseImproved);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.VillagerHouses.getItemTextureLocation().getPath(), ModRegistry.VillagerHouses);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildings.getItemTextureLocation().getPath(), ModRegistry.ModernBuildings);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildingsImproved.getItemTextureLocation().getPath(), ModRegistry.ModernBuildingsImproved);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildingsAdvanced.getItemTextureLocation().getPath(), ModRegistry.ModernBuildingsAdvanced);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Farm.getItemTextureLocation().getPath(), ModRegistry.Farm);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.FarmImproved.getItemTextureLocation().getPath(), ModRegistry.FarmImproved);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.FarmAdvanced.getItemTextureLocation().getPath(), ModRegistry.FarmAdvanced);
    }

    private static void registerItemBlocks() {
        ModRegistry.registerItem(BlockCompressedStone.EnumType.COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.CompressedStoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.DoubleCompressedStoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistry.TripleCompressedStoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.COMPRESSED_DIRT.getUnlocalizedName(), ModRegistry.CompressedDirtItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT.getUnlocalizedName(), ModRegistry.DoubleCompressedDirtItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistry.CompressedGlowstoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistry.DoubleCompressedGlowstoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistry.CompressedQuartzCreteItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistry.DoubleCompressedQuartzCreteItem);
        ModRegistry.registerItem(BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.toString(), ModRegistry.CompressedObsidianItem);
        ModRegistry.registerItem(BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.toString(), ModRegistry.DoubleCompressedObsidianItem);
        ModRegistry.registerItem("block_glass_slab", ModRegistry.GlassSlabItem);
        ModRegistry.registerItem("block_glass_stairs", ModRegistry.GlassStairsItem);
        ModRegistry.registerItem("block_paper_lantern", ModRegistry.PaperLanternItem);
        ModRegistry.registerItem("block_phasic", ModRegistry.PhasicItem);
        ModRegistry.registerItem("block_boundary", ModRegistry.BoundaryItem);
        ModRegistry.registerItem("block_grass_slab", ModRegistry.GrassSlabItem);
        ModRegistry.registerItem("block_grass_stairs", ModRegistry.GrassStairsItem);
        ModRegistry.registerItem(BlockCustomWall.EnumType.GRASS.getUnlocalizedName(), ModRegistry.GrassWallItem);
        ModRegistry.registerItem(BlockCustomWall.EnumType.DIRT.getUnlocalizedName(), ModRegistry.DirtWallItem);
        ModRegistry.registerItem("block_dirt_stairs", ModRegistry.DirtStairsItem);
        ModRegistry.registerItem("block_dirt_slab", ModRegistry.DirtSlabItem);

        if (Prefab.isDebug) {
            ModRegistry.StructureScannerItem = new BlockItem(ModRegistry.StructureScanner, new Item.Properties());
            ModRegistry.registerItem("block_structure_scanner", ModRegistry.StructureScannerItem);
        }

        ModRegistry.registerItem("block_light_switch", ModRegistry.LightSwitchItem);
        ModRegistry.registerItem("block_dark_lamp", ModRegistry.DarkLampItem);

        ModRegistry.registerItem("block_quartz_crete", ModRegistry.QuartzCreteItem);
        ModRegistry.registerItem("block_quartz_crete_wall", ModRegistry.QuartzCreteWallItem);
        ModRegistry.registerItem("block_quartz_crete_bricks", ModRegistry.QuartzCreteBricksItem);
        ModRegistry.registerItem("block_quartz_crete_chiseled", ModRegistry.ChiseledQuartzCreteItem);
        ModRegistry.registerItem("block_quartz_crete_pillar", ModRegistry.QuartzCretePillarItem);
        ModRegistry.registerItem("block_quartz_crete_stairs", ModRegistry.QuartzCreteStairsItem);
        ModRegistry.registerItem("block_quartz_crete_slab", ModRegistry.QuartzCreteSlabItem);
        ModRegistry.registerItem("block_quartz_crete_smooth", ModRegistry.SmoothQuartzCreteItem);
        ModRegistry.registerItem("block_quartz_crete_smooth_wall", ModRegistry.SmoothQuartzCreteWallItem);
        ModRegistry.registerItem("block_quartz_crete_smooth_stairs", ModRegistry.SmoothQuartzCreteStairsItem);
        ModRegistry.registerItem("block_quartz_crete_smooth_slab", ModRegistry.SmoothQuartzCreteSlabItem);
    }

    /**
     * This is where the mod messages are registered.
     */
    private static void RegisterClientToServerMessageHandlers() {

        ModRegistry.registerStructureBuilderMessageHandler();

        ModRegistry.registerStructureScannerMessageHandler();

        ModRegistry.registerStructureScannerActionMessageHandler();
    }

    private static void RegisterRecipeSerializers() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Prefab.MODID, "condition_crafting_shaped"), ModRegistry.ConditionedShapedRecipeSeriaizer);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Prefab.MODID, "condition_crafting_shapeless"), ModRegistry.ConditionedShapelessRecipeSeriaizer);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Prefab.MODID, "condition_smelting"), ModRegistry.ConditionedSmeltingRecipeSeriaizer);
    }

    private static void registerBlock(String registryName, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Prefab.MODID, registryName), block);
    }

    private static void registerItem(String registryName, Item item) {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Prefab.MODID, registryName), item);
    }

    private static void registerStructureBuilderMessageHandler() {
        ServerPlayNetworking.registerGlobalReceiver(ModRegistry.StructureBuild,
                (server, player, handler, buf, responseSender) -> {
                    // Can only access the "attachedData" on the "network thread" which is here.
                    StructureTagMessage message = StructureTagMessage.decode(buf);
                    StructureTagMessage.EnumStructureConfiguration structureConfig = message.getStructureConfig();

                    server.execute(() -> {
                        // This is now on the "main" server thread and things can be done in the world!
                        StructureConfiguration configuration = structureConfig.structureConfig.ReadFromCompoundTag(message.getMessageTag());

                        configuration.BuildStructure(player, player.serverLevel());
                    });
                }
        );
    }

    private static void registerStructureScannerMessageHandler() {
        ServerPlayNetworking.registerGlobalReceiver(ModRegistry.StructureScannerSync, (server, player, handler, buf, responseSender) -> {
            CompoundTag compound = buf.readNbt();
            StructureScannerConfig config = (new StructureScannerConfig()).ReadFromCompoundNBT(compound);

            server.execute(() -> {
                BlockEntity blockEntity = player.level().getBlockEntity(config.blockPos);

                if (blockEntity instanceof StructureScannerBlockEntity) {
                    StructureScannerBlockEntity actualEntity = (StructureScannerBlockEntity) blockEntity;
                    actualEntity.setConfig(config);
                }
            });
        });
    }

    private static void registerStructureScannerActionMessageHandler() {
        ServerPlayNetworking.registerGlobalReceiver(ModRegistry.StructureScannerAction, (server, player, handler, buf, responseSender) -> {
            CompoundTag compound = buf.readNbt();
            StructureScannerConfig config = (new StructureScannerConfig()).ReadFromCompoundNBT(compound);

            server.execute(() -> {
                StructureScannerBlockEntity.ScanShape(config, player, player.serverLevel());
            });
        });
    }

    public static boolean always(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    public static boolean never(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    public enum CustomItemTier implements Tier {
        COPPER("Copper", Tiers.STONE.getLevel(), Tiers.STONE.getUses(), Tiers.STONE.getSpeed(),
                Tiers.STONE.getAttackDamageBonus(), Tiers.STONE.getEnchantmentValue(), () -> {
            return Ingredient
                    .of(Utils.getItemStacksWithTag(new ResourceLocation("c", "copper_ingots")).stream());
        }),
        OSMIUM("Osmium", Tiers.IRON.getLevel(), 500, Tiers.IRON.getSpeed(),
                Tiers.IRON.getAttackDamageBonus() + .5f, Tiers.IRON.getEnchantmentValue(), () -> {
            return Ingredient
                    .of(Utils.getItemStacksWithTag(new ResourceLocation("c", "osmium_ingots")).stream());
        }),
        BRONZE("Bronze", Tiers.IRON.getLevel(), Tiers.IRON.getUses(), Tiers.IRON.getSpeed(),
                Tiers.IRON.getAttackDamageBonus(), Tiers.IRON.getEnchantmentValue(), () -> {
            return Ingredient
                    .of(Utils.getItemStacksWithTag(new ResourceLocation("c", "bronze_ingots")).stream());
        }),
        STEEL("Steel", Tiers.DIAMOND.getLevel(), (int) (Tiers.IRON.getUses() * 1.5),
                Tiers.DIAMOND.getSpeed(), Tiers.DIAMOND.getAttackDamageBonus(),
                Tiers.DIAMOND.getEnchantmentValue(), () -> {
            return Ingredient
                    .of(Utils.getItemStacksWithTag(new ResourceLocation("c", "steel_ingots")).stream());
        }),
        OBSIDIAN("Obsidian", Tiers.DIAMOND.getLevel(), (int) (Tiers.DIAMOND.getUses() * 1.5),
                Tiers.DIAMOND.getSpeed(), Tiers.DIAMOND.getAttackDamageBonus(),
                Tiers.DIAMOND.getEnchantmentValue(), () -> {
            return Ingredient.of(Item.byBlock(Blocks.OBSIDIAN));
        });

        private final String name;
        private final int harvestLevel;
        private final int maxUses;
        private final float efficiency;
        private final float attackDamage;
        private final int enchantability;
        private final LazyLoadedValue<Ingredient> repairMaterial;

        CustomItemTier(String name, int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn,
                       int enchantability, Supplier<Ingredient> repairMaterialIn) {
            this.name = name;
            this.harvestLevel = harvestLevelIn;
            this.maxUses = maxUsesIn;
            this.efficiency = efficiencyIn;
            this.attackDamage = attackDamageIn;
            this.enchantability = enchantability;
            this.repairMaterial = new LazyLoadedValue<>(repairMaterialIn);
        }

        public static CustomItemTier getByName(String name) {
            for (CustomItemTier item : CustomItemTier.values()) {
                if (item.getName().equals(name)) {
                    return item;
                }
            }

            return null;
        }

        public String getName() {
            return this.name;
        }

        public int getUses() {
            return this.maxUses;
        }

        public float getSpeed() {
            return this.efficiency;
        }

        public float getAttackDamageBonus() {
            return this.attackDamage;
        }

        public int getLevel() {
            return this.harvestLevel;
        }

        public int getEnchantmentValue() {
            return this.enchantability;
        }

        public Ingredient getRepairIngredient() {
            return this.repairMaterial.get();
        }
    }
}