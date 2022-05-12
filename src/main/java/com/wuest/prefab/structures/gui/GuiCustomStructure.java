package com.wuest.prefab.structures.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.structures.config.CustomStructureConfiguration;
import com.wuest.prefab.structures.custom.base.CustomStructureInfo;
import com.wuest.prefab.structures.items.ItemBlueprint;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * TODO:
 *  1. Show Name of Custom Structure
 *  2. Show Preview Button
 *  3. Show Cancel Button
 *  4. Show Build Button
 *
 */
public class GuiCustomStructure extends GuiStructure {
    protected CustomStructureInfo selectedStructure;
    protected CustomStructureConfiguration specificConfiguration;
    protected boolean hasColorOptions;

    private ExtendedButton btnBedColor = null;
    private ExtendedButton btnGlassColor = null;

    public GuiCustomStructure() {
        super("Custom Structure");
        this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.Custom;
    }

    @Override
    protected void Initialize() {
        // TODO: Create Stuff Here
        super.Initialize();

        this.selectedStructure = ItemBlueprint.getCustomStructureInHand(this.player);

        this.configuration = this.specificConfiguration = new CustomStructureConfiguration();
        this.configuration.pos = this.pos;
        this.configuration.houseFacing = this.structureFacing;
        this.specificConfiguration.customStructureName = this.selectedStructure.displayName;

        if (this.selectedStructure.hasBedColorOptions || this.selectedStructure.hasGlassColorOptions) {
            this.hasColorOptions = true;
        }

        if (!this.hasColorOptions) {
            this.InitializeStandardButtons();
        }
    }

    @Override
    public Component getNarrationMessage() {
        return new TranslatableComponent(this.selectedStructure.displayName);
    }

    @Override
    protected void preButtonRender(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        // Enable/disable button options here.
        if (!this.hasColorOptions) {
            super.preButtonRender(matrixStack, x, y, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void postButtonRender(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        // Write out text here for labels.
        this.drawSplitString(GuiLangKeys.translateString(this.specificConfiguration.customStructureName), x + 8, y + 10, 128, this.textColor);
    }

    @Override
    public void buttonClicked(AbstractButton button) {
        this.performCancelOrBuildOrHouseFacing(button);

        if (button == this.btnVisualize) {
            this.performPreview();
        }
    }
}
