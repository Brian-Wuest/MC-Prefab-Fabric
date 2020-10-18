package com.wuest.prefab.structures.gui;

import com.wuest.prefab.Tuple;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.structures.config.MonsterMasherConfiguration;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import com.wuest.prefab.structures.predefined.StructureMonsterMasher;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

/**
 * @author WuestMan
 */
public class GuiMonsterMasher extends GuiStructure {
	private static final Identifier houseTopDown = new Identifier("prefab", "textures/gui/monster_masher_top_down.png");
	protected MonsterMasherConfiguration configuration;
	private ExtendedButton btnGlassColor;

	public GuiMonsterMasher() {
		super("Monster Masher");
		this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.MonsterMasher;
		this.modifiedInitialXAxis = 210;
		this.modifiedInitialYAxis = 83;
	}

	@Override
	public void Initialize() {
		this.configuration = ClientEventHandler.playerConfig.getClientConfig("Monster Masher", MonsterMasherConfiguration.class);
		this.configuration.pos = this.pos;

		// Get the upper left hand corner of the GUI box.
		Tuple<Integer, Integer> adjustedXYValue = this.getAdjustedXYValue();
		int grayBoxX = adjustedXYValue.getFirst();
		int grayBoxY = adjustedXYValue.getSecond();

		// Create the buttons.
		this.btnGlassColor = this.createAndAddButton(grayBoxX + 10, grayBoxY + 20, 90, 20, GuiLangKeys.translateDye(this.configuration.dyeColor));

		this.btnVisualize = this.createAndAddButton(grayBoxX + 10, grayBoxY + 90, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_PREVIEW));

		// Create the done and cancel buttons.
		this.btnBuild = this.createAndAddButton(grayBoxX + 10, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_BUILD));

		this.btnCancel = this.createAndAddButton(grayBoxX + 147, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_CANCEL));
	}

	@Override
	protected void preButtonRender(MatrixStack matrixStack, int x, int y) {
		super.preButtonRender(matrixStack, x, y);

		this.bindTexture(houseTopDown);
		GuiStructure.drawModalRectWithCustomSizedTexture(x + 250, y, 1, 108, 156, 108, 156);
	}

	@Override
	protected void postButtonRender(MatrixStack matrixStack,int x, int y) {
		this.drawString(matrixStack,  GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_GLASS), x + 10, y + 10, this.textColor);

		// Draw the text here.
		this.drawSplitString(GuiLangKeys.translateString(GuiLangKeys.GUI_BLOCK_CLICKED), x + 147, y + 10, 100, this.textColor);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	@Override
	public void buttonClicked(AbstractButtonWidget button) {
		this.performCancelOrBuildOrHouseFacing(this.configuration, button);

		if (button == this.btnGlassColor) {
			this.configuration.dyeColor = DyeColor.byId(this.configuration.dyeColor.getId() + 1);
			this.btnGlassColor.setMessage(new LiteralText( GuiLangKeys.translateDye(this.configuration.dyeColor)));
		} else if (button == this.btnVisualize) {
			StructureMonsterMasher structure = StructureMonsterMasher.CreateInstance(StructureMonsterMasher.ASSETLOCATION, StructureMonsterMasher.class);
			StructureRenderHandler.setStructure(structure, Direction.NORTH, this.configuration);
			this.closeScreen();
		}
	}
}
