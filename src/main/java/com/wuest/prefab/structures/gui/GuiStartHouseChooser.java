package com.wuest.prefab.structures.gui;

import com.wuest.prefab.*;

import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.gui.GuiTabScreen;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.gui.controls.GuiCheckBox;
import com.wuest.prefab.gui.controls.GuiTab;
import com.wuest.prefab.structures.config.HouseConfiguration;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import com.wuest.prefab.structures.predefined.StructureAlternateStart;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;


import java.awt.*;

/**
 * @author WuestMan
 */
public class GuiStartHouseChooser extends GuiTabScreen {
	private static final Identifier backgroundTextures = new Identifier("prefab", "textures/gui/default_background.png");
	protected ExtendedButton btnCancel;
	protected ExtendedButton btnBuild;
	protected ExtendedButton btnVisualize;

	// Tabs
	private GuiTab tabGeneral;
	private GuiTab tabConfig;
	private GuiTab tabBlockTypes;
	// General:
	private ExtendedButton btnHouseStyle;
	// Blocks/Size
	private ExtendedButton btnGlassColor;
	private ExtendedButton btnBedColor;
	// Config:
	private GuiCheckBox btnAddTorches;
	private GuiCheckBox btnAddBed;
	private GuiCheckBox btnAddCraftingTable;
	private GuiCheckBox btnAddFurnace;
	private GuiCheckBox btnAddChest;
	private GuiCheckBox btnAddChestContents;
	private GuiCheckBox btnAddMineShaft;
	private boolean allowItemsInChestAndFurnace = true;

	private HouseConfiguration houseConfiguration;

	public GuiStartHouseChooser() {
		super();
		this.Tabs.setWidth(256);
		this.modifiedInitialXAxis = 198;
		this.modifiedInitialYAxis = 83;
	}

	@Override
	public void init() {
		super.init();

		assert this.client != null;
		if (!this.client.player.isCreative()) {
			this.allowItemsInChestAndFurnace = !ClientModRegistry.playerConfig.builtStarterHouse;
		}

		this.Initialize();
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	@Override
	public void render(MatrixStack matrixStack, int x, int y, float f) {
		Tuple<Integer, Integer> adjustedValueCoords = this.getAdjustedXYValue();
		int grayBoxX = adjustedValueCoords.getFirst();
		int grayBoxY = adjustedValueCoords.getSecond();

		this.Tabs.x = adjustedValueCoords.getFirst();
		this.Tabs.y = adjustedValueCoords.getSecond() - 21;

		this.renderBackground(matrixStack);

		// Draw the control background.
		assert this.client != null;
		this.bindTexture(backgroundTextures);
		this.drawTexture(matrixStack,  grayBoxX, grayBoxY, 0, 0, 256, 256);

		for (AbstractButtonWidget button : this.buttons) {
			// Make all buttons invisible.
			if (button != this.btnCancel && button != this.btnBuild) {
				button.visible = false;
			}
		}

		this.btnAddTorches.visible = false;
		this.btnAddBed.visible = false;
		this.btnAddChest.visible = false;
		this.btnAddChestContents.visible = false;
		this.btnAddCraftingTable.visible = false;
		this.btnAddFurnace.visible = false;
		this.btnAddMineShaft.visible = false;
		this.btnBedColor.visible = false;

		// Update visibility on controls based on the selected tab.
		if (this.getSelectedTab() == this.tabGeneral) {
			this.btnHouseStyle.visible = true;
			this.btnVisualize.visible = true;
		} else if (this.getSelectedTab() == this.tabConfig) {
			this.btnAddTorches.visible = Prefab.serverConfiguration.chestOptions.addTorches;
			this.btnAddBed.visible = Prefab.serverConfiguration.starterHouseOptions.addBed;
			this.btnAddChest.visible = Prefab.serverConfiguration.starterHouseOptions.addChests;
			this.btnAddChestContents.visible = this.allowItemsInChestAndFurnace && Prefab.serverConfiguration.starterHouseOptions.addChestContents;
			this.btnAddCraftingTable.visible = Prefab.serverConfiguration.starterHouseOptions.addCraftingTable;
			this.btnAddFurnace.visible = Prefab.serverConfiguration.starterHouseOptions.addFurnace;
			this.btnAddMineShaft.visible = Prefab.serverConfiguration.starterHouseOptions.addMineshaft;

		} else if (this.getSelectedTab() == this.tabBlockTypes) {
			this.btnGlassColor.visible = this.houseConfiguration.houseStyle != HouseConfiguration.HouseStyle.SNOWY
					&& this.houseConfiguration.houseStyle != HouseConfiguration.HouseStyle.DESERT;

			this.btnBedColor.visible = true;
		}

		// Draw the buttons, labels and tabs.
		super.render(matrixStack,  x, y, f);

		// Draw the text here.
		int color = Color.DARK_GRAY.getRGB();

		// Draw the appropriate text based on the selected tab.
		if (this.getSelectedTab() == this.tabGeneral) {
			this.drawString(matrixStack,  GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_STYLE), grayBoxX + 10, grayBoxY + 10, color);
			this.drawSplitString(this.houseConfiguration.houseStyle.getHouseNotes(), grayBoxX + 147, grayBoxY + 10, 95, color);

			this.bindTexture(this.houseConfiguration.houseStyle.getHousePicture());
			GuiTabScreen.drawModalRectWithCustomSizedTexture(grayBoxX + 250, grayBoxY, 1,
					this.houseConfiguration.houseStyle.getImageWidth(), this.houseConfiguration.houseStyle.getImageHeight(),
					this.houseConfiguration.houseStyle.getImageWidth(), this.houseConfiguration.houseStyle.getImageHeight());
		} else if (this.getSelectedTab() == this.tabBlockTypes) {
			if (!(this.houseConfiguration.houseStyle == HouseConfiguration.HouseStyle.SNOWY
					|| this.houseConfiguration.houseStyle == HouseConfiguration.HouseStyle.DESERT)) {
				// Column 1:
				this.drawString(matrixStack,  GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_GLASS), grayBoxX + 10, grayBoxY + 10, color);
			}

			// Column 2:
			this.drawString(matrixStack,  GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_BED_COLOR), grayBoxX + 147, grayBoxY + 10, color);
		}

		if (!Prefab.serverConfiguration.enableStructurePreview) {
			this.btnVisualize.visible = false;
		}
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	@Override
	public void buttonClicked(AbstractButtonWidget button) {
		if (button == this.btnCancel || button == this.btnVisualize
				|| button == this.btnBuild) {
			this.houseConfiguration.addBed = Prefab.serverConfiguration.starterHouseOptions.addBed && this.btnAddBed.isChecked();
			this.houseConfiguration.addChest = Prefab.serverConfiguration.starterHouseOptions.addChests && this.btnAddChest.isChecked();
			this.houseConfiguration.addChestContents = this.allowItemsInChestAndFurnace && (Prefab.serverConfiguration.starterHouseOptions.addChestContents && this.btnAddChestContents.isChecked());
			this.houseConfiguration.addCraftingTable = Prefab.serverConfiguration.starterHouseOptions.addCraftingTable && this.btnAddCraftingTable.isChecked();
			this.houseConfiguration.addFurnace = Prefab.serverConfiguration.starterHouseOptions.addFurnace && this.btnAddFurnace.isChecked();
			this.houseConfiguration.addMineShaft = Prefab.serverConfiguration.starterHouseOptions.addMineshaft && this.btnAddMineShaft.isChecked();
			this.houseConfiguration.addTorches = Prefab.serverConfiguration.chestOptions.addTorches && this.btnAddTorches.isChecked();
			assert this.client != null;
			this.houseConfiguration.houseFacing = this.client.player.getHorizontalFacing().getOpposite();
		}

		if (button == this.btnCancel) {
			this.closeScreen();
		} else if (button == this.btnBuild) {
			PacketByteBuf messagePacket = Utils.createStructureMessageBuffer(this.houseConfiguration.WriteToCompoundNBT(), StructureTagMessage.EnumStructureConfiguration.StartHouse);
			ClientPlayNetworking.send(ModRegistry.StructureBuild, messagePacket);

			this.closeScreen();
		} else if (button == this.btnHouseStyle) {
			int id = this.houseConfiguration.houseStyle.getValue() + 1;
			this.houseConfiguration.houseStyle = HouseConfiguration.HouseStyle.ValueOf(id);

			// Skip the loft if it's not enabled.
			if (this.houseConfiguration.houseStyle == HouseConfiguration.HouseStyle.LOFT
					&& !Prefab.serverConfiguration.enableLoftHouse) {
				id = this.houseConfiguration.houseStyle.getValue() + 1;
				this.houseConfiguration.houseStyle = HouseConfiguration.HouseStyle.ValueOf(id);
			}

			this.btnHouseStyle.setMessage(new LiteralText( this.houseConfiguration.houseStyle.getDisplayName()));

			// Set the default glass colors for this style.
			if (this.houseConfiguration.houseStyle == HouseConfiguration.HouseStyle.HOBBIT) {
				this.houseConfiguration.glassColor = DyeColor.GREEN;
				this.btnGlassColor.setMessage(new LiteralText(GuiLangKeys.translateDye(DyeColor.GREEN)));
			} else if (this.houseConfiguration.houseStyle == HouseConfiguration.HouseStyle.LOFT) {
				this.houseConfiguration.glassColor = DyeColor.BLACK;
				this.btnGlassColor.setMessage(new LiteralText(GuiLangKeys.translateDye(DyeColor.BLACK)));
			} else if (this.houseConfiguration.houseStyle == HouseConfiguration.HouseStyle.BASIC) {
				this.houseConfiguration.glassColor = DyeColor.LIGHT_GRAY;
				this.btnGlassColor.setMessage(new LiteralText(GuiLangKeys.translateDye(DyeColor.LIGHT_GRAY)));
			} else if (this.houseConfiguration.houseStyle == HouseConfiguration.HouseStyle.DESERT2) {
				this.houseConfiguration.glassColor = DyeColor.RED;
				this.btnGlassColor.setMessage(new LiteralText(GuiLangKeys.translateDye(DyeColor.RED)));
			} else {
				this.houseConfiguration.glassColor = DyeColor.CYAN;
				this.btnGlassColor.setMessage(new LiteralText(GuiLangKeys.translateDye(DyeColor.CYAN)));
			}

			this.tabBlockTypes.visible = true;

		} else if (button == this.btnGlassColor) {
			this.houseConfiguration.glassColor = DyeColor.byId(this.houseConfiguration.glassColor.getId() + 1);
			this.btnGlassColor.setMessage(new LiteralText(GuiLangKeys.translateDye(this.houseConfiguration.glassColor)));
		} else if (button == this.btnBedColor) {
			this.houseConfiguration.bedColor = DyeColor.byId(this.houseConfiguration.bedColor.getId() + 1);
			this.btnBedColor.setMessage(new LiteralText(GuiLangKeys.translateDye(this.houseConfiguration.bedColor)));
		} else if (button == this.btnVisualize) {
			StructureAlternateStart structure = StructureAlternateStart.CreateInstance(this.houseConfiguration.houseStyle.getStructureLocation(), StructureAlternateStart.class);

			StructureRenderHandler.setStructure(structure, Direction.NORTH, this.houseConfiguration);
			this.closeScreen();
		}
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	@Override
	public boolean isPauseScreen() {
		return true;
	}

	@Override
	protected void Initialize() {
		// Get the upper left hand corner of the GUI box.
		Tuple<Integer, Integer> adjustedXYValue = this.getAdjustedXYValue();
		int grayBoxX = adjustedXYValue.getFirst();
		int grayBoxY = adjustedXYValue.getSecond();
		int color = Color.DARK_GRAY.getRGB();
		this.houseConfiguration = ClientModRegistry.playerConfig.getClientConfig("Starter House", HouseConfiguration.class);
		this.houseConfiguration.pos = this.pos;

		// Create the Controls.
		// Column 1:
		this.btnHouseStyle = this.createAndAddButton(grayBoxX + 10, grayBoxY + 20, 90, 20, this.houseConfiguration.houseStyle.getDisplayName());

		this.btnVisualize = this.createAndAddButton(grayBoxX + 10, grayBoxY + 60, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_PREVIEW));

		int x = grayBoxX + 10;
		int y = grayBoxY + 10;
		int secondColumnY = y;
		int secondColumnX = x + 137;

		this.btnAddFurnace = this.createAndAddCheckBox(secondColumnX, secondColumnY, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_ADD_FURNACE), this.houseConfiguration.addFurnace, null);
		this.btnAddFurnace.visible = false;

		if (Prefab.serverConfiguration.starterHouseOptions.addFurnace) {
			secondColumnY += 15;
		}

		this.btnAddBed = this.createAndAddCheckBox(secondColumnX, secondColumnY, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_ADD_BED), this.houseConfiguration.addBed, null);
		this.btnAddBed.visible = false;

		if (Prefab.serverConfiguration.starterHouseOptions.addBed) {
			secondColumnY += 15;
		}

		this.btnAddCraftingTable = this.createAndAddCheckBox(x, y, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_ADD_CRAFTING_TABLE), this.houseConfiguration.addCraftingTable, null);
		this.btnAddCraftingTable.visible = false;

		if (Prefab.serverConfiguration.starterHouseOptions.addCraftingTable) {
			y += 15;
		}

		this.btnAddTorches = this.createAndAddCheckBox(x, y, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_ADD_TORCHES), this.houseConfiguration.addTorches, null);
		this.btnAddTorches.visible = false;

		if (Prefab.serverConfiguration.chestOptions.addTorches) {
			y += 15;
		}

		this.btnAddChest = this.createAndAddCheckBox(x, y, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_ADD_CHEST), this.houseConfiguration.addChest, null);
		this.btnAddChest.visible = false;

		if (Prefab.serverConfiguration.starterHouseOptions.addChests) {
			y += 15;
		}

		this.btnAddMineShaft = this.createAndAddCheckBox(x, y, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_BUILD_MINESHAFT), this.houseConfiguration.addMineShaft, null);
		this.btnAddMineShaft.visible = false;

		if (Prefab.serverConfiguration.starterHouseOptions.addMineshaft) {
			y += 15;
		}

		this.btnAddChestContents = this.createAndAddCheckBox(x, y, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_ADD_CHEST_CONTENTS), this.houseConfiguration.addChestContents, null);
		this.btnAddChestContents.visible = false;

		if (this.allowItemsInChestAndFurnace) {
			y += 15;
		}

		x = grayBoxX + 10;
		y = grayBoxY + 20;

		this.btnGlassColor = this.createAndAddButton(x, y, 90, 20, GuiLangKeys.translateDye(this.houseConfiguration.glassColor));

		// Column 2:
		x = secondColumnX;
		this.btnBedColor = this.createAndAddButton(x, y, 90, 20, GuiLangKeys.translateDye(this.houseConfiguration.bedColor));

		// Column 3:

		// Tabs:
		this.tabGeneral = new GuiTab(this.Tabs, GuiLangKeys.translateString(GuiLangKeys.STARTER_TAB_GENERAL), grayBoxX + 3, grayBoxY - 20);
		this.Tabs.AddTab(this.tabGeneral);

		this.tabConfig = new GuiTab(this.Tabs, GuiLangKeys.translateString(GuiLangKeys.STARTER_TAB_CONFIG), grayBoxX + 54, grayBoxY - 20);
		this.Tabs.AddTab(this.tabConfig);

		this.tabBlockTypes = new GuiTab(this.Tabs, GuiLangKeys.translateString(GuiLangKeys.STARTER_TAB_BLOCK), grayBoxX + 105, grayBoxY - 20);
		this.tabBlockTypes.setWidth(70);
		this.Tabs.AddTab(this.tabBlockTypes);

		// Create the done and cancel buttons.
		this.btnBuild = this.createAndAddButton(grayBoxX + 10, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_BUILD));

		this.btnCancel = this.createAndAddButton(grayBoxX + 147, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_CANCEL));
	}
}
