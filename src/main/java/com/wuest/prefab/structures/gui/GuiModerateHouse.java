package com.wuest.prefab.structures.gui;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.gui.GuiTabScreen;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.gui.controls.GuiCheckBox;
import com.wuest.prefab.structures.config.ModerateHouseConfiguration;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import com.wuest.prefab.structures.predefined.StructureModerateHouse;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

/**
 * @author WuestMan
 */
public class GuiModerateHouse extends GuiStructure {
	protected ModerateHouseConfiguration configuration;
	private ExtendedButton btnHouseStyle;
	private GuiCheckBox btnAddChest;
	private GuiCheckBox btnAddChestContents;
	private GuiCheckBox btnAddMineShaft;
	private ExtendedButton btnBedColor;
	private boolean allowItemsInChestAndFurnace = true;

	public GuiModerateHouse() {
		super("Moderate House");

		this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.ModerateHouse;
		this.modifiedInitialXAxis = 212;
		this.modifiedInitialYAxis = 83;
	}

	@Override
	protected void Initialize() {
		if (!MinecraftClient.getInstance().player.isCreative()) {
			this.allowItemsInChestAndFurnace = !ClientModRegistry.playerConfig.builtStarterHouse;
		}

		this.configuration = ClientModRegistry.playerConfig.getClientConfig("Moderate Houses", ModerateHouseConfiguration.class);
		this.configuration.pos = this.pos;

		// Get the upper left hand corner of the GUI box.
		int grayBoxX = this.getCenteredXAxis() - 212;
		int grayBoxY = this.getCenteredYAxis() - 83;

		// Create the buttons.
		this.btnHouseStyle = this.createAndAddButton(grayBoxX + 10, grayBoxY + 20, 90, 20, this.configuration.houseStyle.getDisplayName());

		this.btnVisualize = this.createAndAddButton(grayBoxX + 10, grayBoxY + 50, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_PREVIEW));

		// Create the done and cancel buttons.
		this.btnBuild = this.createAndAddButton(grayBoxX + 10, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_BUILD));

		this.btnCancel = this.createAndAddButton(grayBoxX + 147, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_CANCEL));

		int x = grayBoxX + 130;
		int y = grayBoxY + 20;

		this.btnBedColor = this.createAndAddButton(x, y, 90, 20, GuiLangKeys.translateDye(this.configuration.bedColor));

		y += 30;

		this.btnAddChest = this.createAndAddCheckBox(x, y, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_ADD_CHEST), this.configuration.addChests, null);
		y += 15;

		this.btnAddMineShaft = this.createAndAddCheckBox(x, y, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_BUILD_MINESHAFT), this.configuration.addChestContents, null);
		y += 15;

		this.btnAddChestContents = this.createAndAddCheckBox(x, y, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_ADD_CHEST_CONTENTS), this.configuration.addMineshaft, null);
	}

	@Override
	protected void preButtonRender(MatrixStack matrixStack, int x, int y) {
		super.preButtonRender(matrixStack, x, y);

		this.bindTexture(this.configuration.houseStyle.getHousePicture());
		GuiTabScreen.drawModalRectWithCustomSizedTexture(x + 249, y, 1,
				this.configuration.houseStyle.getImageWidth(), this.configuration.houseStyle.getImageHeight(),
				this.configuration.houseStyle.getImageWidth(), this.configuration.houseStyle.getImageHeight());
	}

	@Override
	protected void postButtonRender(MatrixStack matrixStack, int x, int y) {
		this.btnAddChest.visible = Prefab.serverConfiguration.starterHouseOptions.addChests;
		this.btnAddChestContents.visible = this.allowItemsInChestAndFurnace && Prefab.serverConfiguration.starterHouseOptions.addChestContents;
		this.btnAddMineShaft.visible = Prefab.serverConfiguration.starterHouseOptions.addMineshaft;

		// Draw the text here.
		this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_STYLE), x + 10, y + 10, this.textColor);

		this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_BED_COLOR), x + 130, y + 10, this.textColor);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	@Override
	public void buttonClicked(AbstractButtonWidget button) {
		this.configuration.addChests = this.btnAddChest.visible && this.btnAddChest.isChecked();
		this.configuration.addChestContents = this.allowItemsInChestAndFurnace && (this.btnAddChestContents.visible && this.btnAddChestContents.isChecked());
		this.configuration.addMineshaft = this.btnAddMineShaft.visible && this.btnAddMineShaft.isChecked();

		this.performCancelOrBuildOrHouseFacing(this.configuration, button);

		if (button == this.btnHouseStyle) {
			int id = this.configuration.houseStyle.getValue() + 1;
			this.configuration.houseStyle = ModerateHouseConfiguration.HouseStyle.ValueOf(id);

			this.btnHouseStyle.setMessage(new LiteralText(this.configuration.houseStyle.getDisplayName()));
		} else if (button == this.btnVisualize) {
			StructureModerateHouse structure = StructureModerateHouse.CreateInstance(this.configuration.houseStyle.getStructureLocation(), StructureModerateHouse.class);
			StructureRenderHandler.setStructure(structure, Direction.NORTH, this.configuration);
			this.closeScreen();
		} else if (button == this.btnBedColor) {
			this.configuration.bedColor = DyeColor.byId(this.configuration.bedColor.getId() + 1);
			this.btnBedColor.setMessage(new LiteralText(GuiLangKeys.translateDye(this.configuration.bedColor)));
		}
	}
}
