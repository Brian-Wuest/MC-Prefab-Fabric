package com.wuest.prefab.structures.gui;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.structures.config.ChickenCoopConfiguration;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import com.wuest.prefab.structures.predefined.StructureChickenCoop;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

/**
 * @author WuestMan
 */
public class GuiChickenCoop extends GuiStructure {
	private static final Identifier structureTopDown = new Identifier("prefab", "textures/gui/chicken_coop_top_down.png");
	protected ChickenCoopConfiguration configuration;

	public GuiChickenCoop() {
		super("Chicken Coop");
		this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.ChickenCoop;
		this.modifiedInitialXAxis = 213;
		this.modifiedInitialYAxis = 83;
	}

	@Override
	protected void preButtonRender(MatrixStack matrixStack, int x, int y) {
		super.preButtonRender(matrixStack, x, y);

		this.bindTexture(structureTopDown);
		GuiStructure.drawModalRectWithCustomSizedTexture(x + 250, y, 1, 171, 87, 171, 87);
	}

	@Override
	protected void postButtonRender(MatrixStack matrixStack,int x, int y) {
		this.drawSplitString(GuiLangKeys.translateString(GuiLangKeys.GUI_BLOCK_CLICKED), x + 147, y + 10, 95, this.textColor);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	@Override
	public void buttonClicked(AbstractButtonWidget button) {
		this.performCancelOrBuildOrHouseFacing(this.configuration, button);

		if (button == this.btnVisualize) {
			StructureChickenCoop structure = StructureChickenCoop.CreateInstance(StructureChickenCoop.ASSETLOCATION, StructureChickenCoop.class);
			StructureRenderHandler.setStructure(structure, Direction.NORTH, this.configuration);
			this.closeScreen();
		}
	}

	@Override
	protected void Initialize() {
		this.configuration = ClientModRegistry.playerConfig.getClientConfig("Chicken Coop", ChickenCoopConfiguration.class);
		this.configuration.pos = this.pos;

		// Get the upper left hand corner of the GUI box.
		Tuple<Integer, Integer> adjustedXYValue = this.getAdjustedXYValue();
		int grayBoxX = adjustedXYValue.getFirst();
		int grayBoxY = adjustedXYValue.getSecond();

		// Create the buttons.
		this.btnVisualize = this.createAndAddButton(grayBoxX + 10, grayBoxY + 90, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_PREVIEW));

		// Create the done and cancel buttons.
		this.btnBuild = this.createAndAddButton(grayBoxX + 10, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_BUILD));

		this.btnCancel = this.createAndAddButton(grayBoxX + 147, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_CANCEL));
	}

}
