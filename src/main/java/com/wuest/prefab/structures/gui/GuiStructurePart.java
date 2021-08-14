package com.wuest.prefab.structures.gui;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.gui.controls.GuiSlider;
import com.wuest.prefab.structures.base.EnumStairsMaterial;
import com.wuest.prefab.structures.base.EnumStructureMaterial;
import com.wuest.prefab.structures.config.StructurePartConfiguration;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import com.wuest.prefab.structures.predefined.StructurePart;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Direction;


/**
 * This class is used as the gui for structure parts.
 *
 * @author WuestMan
 */
@SuppressWarnings({"ConstantConditions", "SpellCheckingInspection"})
public class GuiStructurePart extends GuiStructure {
	protected StructurePartConfiguration configuration;
	private GuiSlider sldrStairWidth;
	private GuiSlider sldrStairHeight;
	private GuiSlider sldrGeneralWidth;
	private GuiSlider sldrGeneralHeight;
	private ExtendedButton btnPartStyle;
	private ExtendedButton btnMaterialType;
	private ExtendedButton btnStairsMaterialType;

	public GuiStructurePart() {
		super("Structure Part");
		this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.Parts;
		this.modifiedInitialXAxis = 213;
		this.modifiedInitialYAxis = 83;
	}

	@Override
	protected void Initialize() {
		this.configuration = ClientModRegistry.playerConfig.getClientConfig("Parts", StructurePartConfiguration.class);
		this.configuration.pos = this.pos;

		// Get the upper left hand corner of the GUI box.
		Tuple<Integer, Integer> adjustedValue = this.getAdjustedXYValue();
		int grayBoxX = adjustedValue.getFirst();
		int grayBoxY = adjustedValue.getSecond();

		// Create the done and cancel buttons.
		this.btnBuild = this.createAndAddButton(grayBoxX + 10, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_BUILD));

		this.btnCancel = this.createAndAddButton(grayBoxX + 147, grayBoxY + 136, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_CANCEL));

		this.btnVisualize = this.createAndAddButton(grayBoxX + 10, grayBoxY + 90, 90, 20, GuiLangKeys.translateString(GuiLangKeys.GUI_BUTTON_PREVIEW));

		this.sldrStairHeight = this.createAndAddSlider(grayBoxX + 147, grayBoxY + 100, 90, 20, "", "", 1, 9, this.configuration.stairHeight, false, true, this::buttonClicked);

		this.sldrStairWidth = this.createAndAddSlider(grayBoxX + 147, grayBoxY + 60, 90, 20, "", "", 1, 9, this.configuration.stairWidth, false, true, this::buttonClicked);

		this.sldrGeneralHeight = this.createAndAddSlider(grayBoxX + 147, grayBoxY + 100, 90, 20, "", "", 3, 9, this.configuration.generalHeight, false, true, this::buttonClicked);

		this.sldrGeneralWidth = this.createAndAddSlider(grayBoxX + 147, grayBoxY + 60, 90, 20, "", "", 3, 9, this.configuration.generalWidth, false, true, this::buttonClicked);

		this.btnPartStyle = this.createAndAddButton(grayBoxX + 10, grayBoxY + 20, 90, 20, GuiLangKeys.translateString(this.configuration.style.translateKey));

		this.btnMaterialType = this.createAndAddButton(grayBoxX + 147, grayBoxY + 20, 90, 20, this.configuration.partMaterial.getTranslatedName());

		this.btnStairsMaterialType = this.createAndAddButton(grayBoxX + 147, grayBoxY + 20, 90, 20, this.configuration.stairsMaterial.getTranslatedName());
	}

	@Override
	protected void preButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
		super.preButtonRender(matrixStack, x, y, mouseX, mouseY, partialTicks);

		this.bindTexture(this.configuration.style.getPictureLocation());

		GuiStructure.drawModalRectWithCustomSizedTexture(x + 250, y, 1,
				this.configuration.style.imageWidth, this.configuration.style.imageHeight,
				this.configuration.style.imageWidth, this.configuration.style.imageHeight);
	}

	@Override
	protected void postButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
		this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.STYLE), x + 10, y + 10, this.textColor);
		this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.MATERIAL), x + 147, y + 10, this.textColor);

		if (this.configuration.style == StructurePartConfiguration.EnumStyle.Stairs
				|| this.configuration.style == StructurePartConfiguration.EnumStyle.Roof) {
			this.sldrStairHeight.visible = this.configuration.style != StructurePartConfiguration.EnumStyle.Roof;
			this.sldrStairWidth.visible = true;
			this.sldrGeneralHeight.visible = false;
			this.sldrGeneralWidth.visible = false;
			this.btnStairsMaterialType.visible = true;
			this.btnMaterialType.visible = false;
		} else {
			this.btnStairsMaterialType.visible = false;
			this.btnMaterialType.visible = true;
			this.sldrStairHeight.visible = false;
			this.sldrStairWidth.visible = false;
			this.sldrGeneralHeight.visible = true;
			this.sldrGeneralWidth.visible = true;
		}

		if (this.configuration.style != StructurePartConfiguration.EnumStyle.Roof) {
			if (this.configuration.style == StructurePartConfiguration.EnumStyle.Floor) {
				this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.LENGTH), x + 147, y + 90, this.textColor);
			} else {
				this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.HEIGHT), x + 147, y + 90, this.textColor);
			}
		}

		if (this.configuration.style == StructurePartConfiguration.EnumStyle.Roof) {
			this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.HEIGHT), x + 147, y + 50, this.textColor);
		} else {
			this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.WIDTH), x + 147, y + 50, this.textColor);
		}
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	@Override
	public void buttonClicked(PressableWidget button) {
		this.configuration.houseFacing = this.client.player.getHorizontalFacing().getOpposite();
		this.configuration.stairHeight = this.sldrStairHeight.getValueInt();
		this.configuration.stairWidth = this.sldrStairWidth.getValueInt();
		this.configuration.generalHeight = this.sldrGeneralHeight.getValueInt();
		this.configuration.generalWidth = this.sldrGeneralWidth.getValueInt();

		this.performCancelOrBuildOrHouseFacing(this.configuration, button);

		if (button == this.btnMaterialType) {
			this.configuration.partMaterial = EnumStructureMaterial.getMaterialByNumber(this.configuration.partMaterial.getNumber() + 1);
			this.btnMaterialType.setMessage(new LiteralText(this.configuration.partMaterial.getTranslatedName()));
		}
		if (button == this.btnStairsMaterialType) {
			this.configuration.stairsMaterial = EnumStairsMaterial.getByOrdinal(this.configuration.stairsMaterial.ordinal() + 1);
			this.btnStairsMaterialType.setMessage(new LiteralText(this.configuration.stairsMaterial.getTranslatedName()));
		} else if (button == this.btnPartStyle) {
			this.configuration.style = StructurePartConfiguration.EnumStyle.getByOrdinal(this.configuration.style.ordinal() + 1);
			this.btnPartStyle.setMessage(new LiteralText(GuiLangKeys.translateString(this.configuration.style.translateKey)));
		} else if (button == this.btnVisualize) {
			StructurePart structure = new StructurePart();
			structure.getClearSpace().getShape().setDirection(Direction.NORTH);
			structure.setupStructure(this.client.world, this.configuration, this.pos);

			StructureRenderHandler.setStructure(structure, Direction.SOUTH, this.configuration);
			this.closeScreen();
		}
	}
}
