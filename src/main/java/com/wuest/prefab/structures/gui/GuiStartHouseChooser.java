package com.wuest.prefab.structures.gui;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.blocks.FullDyeColor;
import com.wuest.prefab.config.ModConfiguration;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.gui.GuiUtils;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.gui.controls.GuiCheckBox;
import com.wuest.prefab.structures.config.HouseConfiguration;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import com.wuest.prefab.structures.predefined.StructureAlternateStart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;

/**
 * @author WuestMan
 */
public class GuiStartHouseChooser extends GuiStructure {
    protected ModConfiguration serverConfiguration;

    // General:
    private ExtendedButton btnHouseStyle;
    // Blocks/Size
    private ExtendedButton btnGlassColor;
    private ExtendedButton btnBedColor;
    // Config:
    private GuiCheckBox btnAddChest;
    private GuiCheckBox btnAddChestContents;
    private GuiCheckBox btnAddMineShaft;
    private boolean allowItemsInChestAndFurnace = true;

    private HouseConfiguration configuration;

    public GuiStartHouseChooser() {
        super("Starter House");
        this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.StartHouse;
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
        this.configuration = ClientModRegistry.playerConfig.getClientConfig("Starter House", HouseConfiguration.class);
        this.configuration.pos = this.pos;

        // Get the upper left hand corner of the GUI box.
        Tuple<Integer, Integer> adjustedXYValue = this.getAdjustedXYValue();
        int grayBoxX = adjustedXYValue.getFirst();
        int grayBoxY = adjustedXYValue.getSecond();

        // Create the buttons.
        this.btnHouseStyle = this.createAndAddButton(grayBoxX + 8, grayBoxY + 25, 90, 20, this.configuration.houseStyle.getDisplayName(), false);
        this.btnBedColor = this.createAndAddDyeButton(grayBoxX + 8, grayBoxY + 60, 90, 20, this.configuration.bedColor);
        this.btnGlassColor = this.createAndAddFullDyeButton(grayBoxX + 8, grayBoxY + 95, 90, 20, this.configuration.glassColor);
        this.btnAddChest = this.createAndAddCheckBox(grayBoxX + 8, grayBoxY + 120, GuiLangKeys.STARTER_HOUSE_ADD_CHEST, this.configuration.addChest, this::buttonClicked);
        this.btnAddMineShaft = this.createAndAddCheckBox(grayBoxX + 8, grayBoxY + 137, GuiLangKeys.STARTER_HOUSE_BUILD_MINESHAFT, this.configuration.addChestContents, this::buttonClicked);
        this.btnAddChestContents = this.createAndAddCheckBox(grayBoxX + 8, grayBoxY + 154, GuiLangKeys.STARTER_HOUSE_ADD_CHEST_CONTENTS, this.configuration.addMineShaft, this::buttonClicked);

        // Create the standard buttons.
        this.btnVisualize = this.createAndAddCustomButton(grayBoxX + 26, grayBoxY + 177, 90, 20, GuiLangKeys.GUI_BUTTON_PREVIEW);
        this.btnBuild = this.createAndAddCustomButton(grayBoxX + 313, grayBoxY + 177, 90, 20, GuiLangKeys.GUI_BUTTON_BUILD);
        this.btnCancel = this.createAndAddButton(grayBoxX + 165, grayBoxY + 177, 90, 20, GuiLangKeys.GUI_BUTTON_CANCEL);
    }

    @Override
    protected void preButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        int imagePanelUpperLeft = x + 142;
        int imagePanelWidth = 285;
        int imagePanelMiddle = imagePanelWidth / 2;

        this.renderBackground(matrixStack);

        this.drawControlLeftPanel(matrixStack, x + 2, y + 10, 141, 190);
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

        this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_GLASS), x + 8, y + 85, this.textColor);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Override
    public void buttonClicked(PressableWidget button) {
        this.configuration.addBed = this.serverConfiguration.starterHouseOptions.addBed;
        this.configuration.addChest = this.serverConfiguration.starterHouseOptions.addChests && this.btnAddChest.isChecked();
        this.configuration.addChestContents = this.allowItemsInChestAndFurnace && (this.serverConfiguration.starterHouseOptions.addChestContents && this.btnAddChestContents.isChecked());
        this.configuration.addCraftingTable = this.serverConfiguration.starterHouseOptions.addCraftingTable;
        this.configuration.addFurnace = this.serverConfiguration.starterHouseOptions.addFurnace;
        this.configuration.addMineShaft = this.serverConfiguration.starterHouseOptions.addMineshaft && this.btnAddMineShaft.isChecked();
        this.configuration.addTorches = this.serverConfiguration.chestOptions.addTorches;
        this.configuration.houseFacing = this.getMinecraft().player.getHorizontalFacing().getOpposite();

        this.performCancelOrBuildOrHouseFacing(this.configuration, button);

        if (button == this.btnHouseStyle) {
            int id = this.configuration.houseStyle.getValue() + 1;
            this.configuration.houseStyle = HouseConfiguration.HouseStyle.ValueOf(id);
            GuiUtils.setButtonText(btnHouseStyle, this.configuration.houseStyle.getDisplayName());
        } else if (button == this.btnGlassColor) {
            this.configuration.glassColor = FullDyeColor.byId(this.configuration.glassColor.getId() + 1);
            GuiUtils.setButtonText(this.btnGlassColor, GuiLangKeys.translateFullDye(this.configuration.glassColor));
        } else if (button == this.btnBedColor) {
            this.configuration.bedColor = DyeColor.byId(this.configuration.bedColor.getId() + 1);
            GuiUtils.setButtonText(btnBedColor, GuiLangKeys.translateDye(this.configuration.bedColor));
        } else if (button == this.btnVisualize) {
            StructureAlternateStart structure = StructureAlternateStart.CreateInstance(this.configuration.houseStyle.getStructureLocation(), StructureAlternateStart.class);
            this.performPreview(structure, this.configuration);
        }
    }
}
