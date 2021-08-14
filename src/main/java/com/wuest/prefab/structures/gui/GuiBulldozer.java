package com.wuest.prefab.structures.gui;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.structures.config.BulldozerConfiguration;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;

/**
 * @author WuestMan
 */
public class GuiBulldozer extends GuiStructure {

	protected BulldozerConfiguration configuration;

	/**
	 * Initializes a new instance of the {@link GuiBulldozer} class.
	 */
	public GuiBulldozer() {
		super("Bulldozer");

		this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.Bulldozer;
		this.modifiedInitialXAxis = 125;
		this.modifiedInitialYAxis = 83;
	}

	@Override
	protected void Initialize() {
		this.configuration = ClientModRegistry.playerConfig.getClientConfig("Bulldozer", BulldozerConfiguration.class);
		this.configuration.pos = this.pos;

		// Get the upper left hand corner of the GUI box.
		Tuple<Integer, Integer> adjustedCorner = this.getAdjustedXYValue();
		int grayBoxX = adjustedCorner.getFirst();
		int grayBoxY = adjustedCorner.getSecond();

		// Create the done and cancel buttons.
		this.btnBuild = this.createAndAddButton(grayBoxX + 10, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_BUILD));

		this.btnCancel = this.createAndAddButton(grayBoxX + 147, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_CANCEL));
	}

	@Override
	protected void postButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
		this.drawSplitString(GuiLangKeys.translateString(GuiLangKeys.GUI_BULLDOZER_DESCRIPTION), x + 10, y + 10, 230, this.textColor);

		this.drawSplitString(GuiLangKeys.translateString(GuiLangKeys.GUI_CLEARED_AREA), x + 10, y + 40, 230, this.textColor);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	@Override
	public void buttonClicked(PressableWidget button) {
		assert this.client != null;
		this.configuration.houseFacing = this.client.player.getHorizontalFacing().getOpposite();
		this.performCancelOrBuildOrHouseFacing(this.configuration, button);
	}

}
