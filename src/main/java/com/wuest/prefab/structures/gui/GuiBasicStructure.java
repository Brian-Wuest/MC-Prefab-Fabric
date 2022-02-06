package com.wuest.prefab.structures.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.blocks.FullDyeColor;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.gui.GuiUtils;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.structures.config.BasicStructureConfiguration;
import com.wuest.prefab.structures.config.enums.BaseOption;
import com.wuest.prefab.structures.items.ItemBasicStructure;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import com.wuest.prefab.structures.predefined.StructureBasic;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

/**
 * This class is used as the gui for all basic structures.
 *
 * @author WuestMan
 */
@SuppressWarnings({"SpellCheckingInspection"})
public class GuiBasicStructure extends GuiStructure {
    public BasicStructureConfiguration configuration;
    private ExtendedButton btnBedColor = null;
    private ExtendedButton btnGlassColor = null;
    private ExtendedButton btnStructureOptions = null;
    private ArrayList<BaseOption> availableOptions;
    private boolean showConfigurationOptions;

    public GuiBasicStructure() {
        super("Basic Structure");
        this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.Basic;
        this.showConfigurationOptions = false;
    }

    @Override
    public Component getNarrationMessage() {
        return new TranslatableComponent(this.configuration.getDisplayName());
    }

    @Override
    protected void Initialize() {
        super.Initialize();

        // Get the structure configuration for this itemstack.
        ItemStack stack = ItemBasicStructure.getBasicStructureItemInHand(this.player);

        if (stack != null) {
            ItemBasicStructure item = (ItemBasicStructure) stack.getItem();
            this.configuration = ClientModRegistry.playerConfig.getClientConfig(item.structureType.getName(), BasicStructureConfiguration.class);
            this.configuration.basicStructureName = item.structureType;

            if (this.configuration.chosenOption.getClass() != item.structureType.getBaseOption().getClass()) {
                this.availableOptions = item.structureType.getBaseOption().getSpecificOptions(true);
                this.configuration.chosenOption = this.availableOptions.get(0);
            } else {
                this.availableOptions = this.configuration.chosenOption.getSpecificOptions(true);
            }
            
            this.structureImageLocation = this.configuration.chosenOption.getPictureLocation();

        }

        this.configuration.pos = this.pos;

        if (this.availableOptions.size() > 1 || this.configuration.basicStructureName.shouldShowConfigurationOptions()) {
            this.showConfigurationOptions = true;
        }

        if (!this.showConfigurationOptions) {
            this.InitializeStandardButtons();
        } else {
            this.modifiedInitialXAxis = 215;
            this.modifiedInitialYAxis = 117;
            this.imagePanelWidth = 285;

            // Get the upper left hand corner of the GUI box.
            Tuple<Integer, Integer> adjustedXYValue = this.getAdjustedXYValue();
            int grayBoxX = adjustedXYValue.getFirst();
            int grayBoxY = adjustedXYValue.getSecond();

            // Create the buttons.
            int x = grayBoxX + 8;
            int y = grayBoxY + 45;

            if (this.availableOptions.size() > 1) {
                this.btnStructureOptions = this.createAndAddButton(x, y, 100, 20, this.configuration.chosenOption.getTranslationString(), GuiLangKeys.translateString(GuiLangKeys.BUILDING_OPTIONS));
                this.btnStructureOptions.visible = true;
                y += 45;
            } else if (this.btnStructureOptions != null) {
                this.btnStructureOptions.visible = false;
            }

            this.btnBedColor = this.createAndAddDyeButton(x, y, 90, 20, this.configuration.bedColor, GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_BED_COLOR));
            this.btnBedColor.visible = false;

            this.btnGlassColor = this.createAndAddFullDyeButton(x, y, 90, 20, this.configuration.glassColor, GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_GLASS));
            this.btnGlassColor.visible = false;

            // Create the standard buttons.
            this.btnVisualize = this.createAndAddCustomButton(grayBoxX + 24, grayBoxY + 177, 90, 20, GuiLangKeys.GUI_BUTTON_PREVIEW);
            this.btnBuild = this.createAndAddCustomButton(grayBoxX + 310, grayBoxY + 177, 90, 20, GuiLangKeys.GUI_BUTTON_BUILD);
            this.btnCancel = this.createAndAddButton(grayBoxX + 154, grayBoxY + 177, 90, 20, false, GuiLangKeys.GUI_BUTTON_CANCEL);
        }
    }

    @Override
    protected void preButtonRender(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        if (!this.showConfigurationOptions) {
            super.preButtonRender(matrixStack, x, y, mouseX, mouseY, partialTicks);
        } else {
            // Draw the control background.
            int imagePanelUpperLeft = x + 136;
            int imagePanelMiddle = this.imagePanelWidth / 2;

            this.renderBackground(matrixStack);

            this.drawControlLeftPanel(matrixStack, x + 2, y + 10, 185, 190);
            this.drawControlRightPanel(matrixStack, imagePanelUpperLeft, y + 10, this.imagePanelWidth, 190);

            int middleOfImage = this.shownImageWidth / 2;
            int imageLocation = imagePanelUpperLeft + (imagePanelMiddle - middleOfImage);

            // Draw the picture.
            GuiUtils.bindAndDrawScaledTexture(
                    this.structureImageLocation,
                    matrixStack,
                    imageLocation,
                    y + 15,
                    this.shownImageWidth,
                    this.shownImageHeight,
                    this.shownImageWidth,
                    this.shownImageHeight,
                    this.shownImageWidth,
                    this.shownImageHeight);
        }

        if (this.btnBedColor != null) {
            this.btnBedColor.visible = false;
        }

        if (this.btnGlassColor != null) {
            this.btnGlassColor.visible = false;
        }

        int yValue = y + 45;

        if (this.availableOptions.size() > 1) {
            yValue = yValue + 45;
        }

        if (this.configuration.chosenOption.getHasBedColor()) {
            this.btnBedColor.visible = true;
            this.btnBedColor.y = yValue;

            yValue = yValue + 45;
        }

        if (this.configuration.chosenOption.getHasGlassColor()) {
            this.btnGlassColor.visible = true;
            this.btnGlassColor.y = yValue;
        }
    }

    @Override
    protected void postButtonRender(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        if (this.showConfigurationOptions) {
            this.drawSplitString(GuiLangKeys.translateString(this.configuration.basicStructureName.getItemTranslationString()), x + 8, y + 17, 128, this.textColor);

            int yValue = y + 35;

            if (this.availableOptions.size() > 1) {
                this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.BUILDING_OPTIONS), x + 8, yValue, this.textColor);
                yValue += 45;
            }

            // Draw the text here.
            if (this.configuration.chosenOption.getHasBedColor()) {
                this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_BED_COLOR), x + 8, yValue, this.textColor);
                yValue += 45;
            }

            if (this.configuration.chosenOption.getHasGlassColor()) {
                this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.GUI_STRUCTURE_GLASS), x + 8, yValue, this.textColor);
                yValue += 45;
            }
        }
    }

    @Override
    public void buttonClicked(PressableWidget button, boolean rightClick) {
        this.performCancelOrBuildOrHouseFacing(this.configuration, button);

        if (button == this.btnVisualize) {
            StructureBasic structure = StructureBasic.CreateInstance(this.configuration.chosenOption.getAssetLocation(), StructureBasic.class);
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

            GuiUtils.setButtonText(this.btnBedColor, GuiLangKeys.translateDye(this.configuration.bedColor));
        } else if (button == this.btnGlassColor) {
            if (rightClick) {
                int id = this.configuration.glassColor.getId() - 1;

                if (id < 0) {
                    id = FullDyeColor.values().length - 1;
                }

                this.configuration.glassColor = FullDyeColor.byId(id);
            } else {
                this.configuration.glassColor = FullDyeColor.byId(this.configuration.glassColor.getId() + 1);
            }

            GuiUtils.setButtonText(this.btnGlassColor, GuiLangKeys.translateFullDye(this.configuration.glassColor));
        } else if (button == this.btnStructureOptions) {
            int selectedOptionIndex = this.availableOptions.indexOf(this.configuration.chosenOption);

            if (rightClick) {
                selectedOptionIndex--;
            } else {
                selectedOptionIndex++;
            }

            if (selectedOptionIndex >= this.availableOptions.size()) {
                selectedOptionIndex = 0;
            } else if (selectedOptionIndex < 0) {
                selectedOptionIndex = this.availableOptions.size() - 1;
            }

            this.configuration.chosenOption = this.availableOptions.get(selectedOptionIndex);
            this.structureImageLocation = this.configuration.chosenOption.getPictureLocation();
            GuiUtils.setButtonText(btnStructureOptions, GuiLangKeys.translateString(this.configuration.chosenOption.getTranslationString()));
        }
    }
}
