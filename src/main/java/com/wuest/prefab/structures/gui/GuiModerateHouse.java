package com.wuest.prefab.structures.gui;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.config.ModConfiguration;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.gui.GuiUtils;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.gui.controls.GuiCheckBox;
import com.wuest.prefab.structures.config.ModerateHouseConfiguration;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import com.wuest.prefab.structures.predefined.StructureModerateHouse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;

/**
 * @author WuestMan
 */
public class GuiModerateHouse extends GuiStructure {
    protected ModerateHouseConfiguration configuration;
    protected ModConfiguration serverConfiguration;
    private ExtendedButton btnHouseStyle;
    private GuiCheckBox btnAddChest;
    private GuiCheckBox btnAddChestContents;
    private GuiCheckBox btnAddMineShaft;
    private ExtendedButton btnBedColor;
    private boolean allowItemsInChestAndFurnace = true;

    public GuiModerateHouse() {
        super("Moderate House");

        this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.ModerateHouse;
    }

    @Override
    public Text getNarratedTitle() {
        return new TranslatableText(GuiLangKeys.translateString(GuiLangKeys.TITLE_MODERATE_HOUSE));
    }

    @Override
    protected void Initialize() {
        this.modifiedInitialXAxis = 215;
        this.modifiedInitialYAxis = 117;
        this.shownImageHeight = 150;
        this.shownImageWidth = 268;

        if (!MinecraftClient.getInstance().player.isCreative()) {
            this.allowItemsInChestAndFurnace = !ClientModRegistry.playerConfig.builtStarterHouse;
        } else {
            this.allowItemsInChestAndFurnace = true;
        }

        this.serverConfiguration = Prefab.serverConfiguration;
        this.configuration = ClientModRegistry.playerConfig.getClientConfig("Moderate Houses", ModerateHouseConfiguration.class);
        this.configuration.pos = this.pos;

        // Get the upper left hand corner of the GUI box.
        Tuple<Integer, Integer> adjustedXYValue = this.getAdjustedXYValue();
        int grayBoxX = adjustedXYValue.getFirst();
        int grayBoxY = adjustedXYValue.getSecond();

        // Create the buttons.
        this.btnHouseStyle = this.createAndAddButton(grayBoxX + 8, grayBoxY + 25, 90, 20, this.configuration.houseStyle.getDisplayName(), false, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_STYLE));
        this.btnBedColor = this.createAndAddDyeButton(grayBoxX + 8, grayBoxY + 60, 90, 20, this.configuration.bedColor, GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_BED_COLOR));
        this.btnAddChest = this.createAndAddCheckBox(grayBoxX + 8, grayBoxY + 120, GuiLangKeys.STARTER_HOUSE_ADD_CHEST, this.configuration.addChests, this::leftButtonClicked);
        this.btnAddMineShaft = this.createAndAddCheckBox(grayBoxX + 8, grayBoxY + 137, GuiLangKeys.STARTER_HOUSE_BUILD_MINESHAFT, this.configuration.addChestContents, this::leftButtonClicked);
        this.btnAddChestContents = this.createAndAddCheckBox(grayBoxX + 8, grayBoxY + 154, GuiLangKeys.STARTER_HOUSE_ADD_CHEST_CONTENTS, this.configuration.addMineshaft, this::leftButtonClicked);

        // Create the standard buttons.
        this.btnVisualize = this.createAndAddCustomButton(grayBoxX + 24, grayBoxY + 177, 90, 20, GuiLangKeys.GUI_BUTTON_PREVIEW);
        this.btnBuild = this.createAndAddCustomButton(grayBoxX + 310, grayBoxY + 177, 90, 20, GuiLangKeys.GUI_BUTTON_BUILD);
        this.btnCancel = this.createAndAddButton(grayBoxX + 154, grayBoxY + 177, 90, 20, false, GuiLangKeys.GUI_BUTTON_CANCEL);
    }

    @Override
    protected void preButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        int imagePanelUpperLeft = x + 136;
        int imagePanelWidth = 285;
        int imagePanelMiddle = imagePanelWidth / 2;

        this.renderBackground(matrixStack);

        this.drawControlLeftPanel(matrixStack, x + 2, y + 10, 135, 190);
        this.drawControlRightPanel(matrixStack, imagePanelUpperLeft, y + 10, imagePanelWidth, 190);

        int middleOfImage = this.shownImageWidth / 2;
        int imageLocation = imagePanelUpperLeft + (imagePanelMiddle - middleOfImage);

        GuiUtils.bindAndDrawScaledTexture(
                this.configuration.houseStyle.getHousePicture(),
                matrixStack,
                imageLocation,
                y + 15,
                this.shownImageWidth,
                this.shownImageHeight,
                this.shownImageWidth,
                this.shownImageHeight,
                this.shownImageWidth,
                this.shownImageHeight);

        this.btnAddChest.visible = this.serverConfiguration.starterHouseOptions.addChests;
        this.btnAddChestContents.visible = this.allowItemsInChestAndFurnace && this.serverConfiguration.starterHouseOptions.addChestContents;
        this.btnAddMineShaft.visible = this.serverConfiguration.starterHouseOptions.addMineshaft;
    }

    @Override
    protected void postButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        // Draw the text here.
        this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.STARTER_HOUSE_STYLE), x + 8, y + 15, this.textColor);

        this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_BED_COLOR), x + 8, y + 50, this.textColor);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    public void leftButtonClicked(PressableWidget button) {
        this.buttonClicked(button, false);
    }

    @Override
    public void buttonClicked(PressableWidget button, boolean rightClick) {
        this.configuration.addChests = this.btnAddChest.visible && this.btnAddChest.isChecked();
        this.configuration.addChestContents = this.allowItemsInChestAndFurnace && (this.btnAddChestContents.visible && this.btnAddChestContents.isChecked());
        this.configuration.addMineshaft = this.btnAddMineShaft.visible && this.btnAddMineShaft.isChecked();

        this.performCancelOrBuildOrHouseFacing(this.configuration, button);

        if (button == this.btnHouseStyle) {
            int id;
            if (rightClick) {
                id = this.configuration.houseStyle.getValue() - 1;
                if (id < 0) {
                    id = ModerateHouseConfiguration.HouseStyle.values().length - 1;
                }
            } else {
                id = this.configuration.houseStyle.getValue() + 1;
            }

            this.configuration.houseStyle = ModerateHouseConfiguration.HouseStyle.ValueOf(id);
            GuiUtils.setButtonText(btnHouseStyle, this.configuration.houseStyle.getDisplayName());
        } else if (button == this.btnVisualize) {
            StructureModerateHouse structure = StructureModerateHouse.CreateInstance(this.configuration.houseStyle.getStructureLocation(), StructureModerateHouse.class);
            this.performPreview(structure, this.configuration);
        } else if (button == this.btnBedColor) {
            if (rightClick) {
                int id = this.configuration.bedColor.getId() - 1;
                if (id < 0) {
                    id = DyeColor.values().length - 1;
                }

                this.configuration.bedColor = DyeColor.byId(id);
            } else {
                this.configuration.bedColor = DyeColor.byId(this.configuration.bedColor.getId() + 1);
            }

            GuiUtils.setButtonText(btnBedColor, GuiLangKeys.translateDye(this.configuration.bedColor));
        }
    }
}
