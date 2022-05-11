package com.wuest.prefab.structures.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.structures.custom.base.CustomStructureInfo;
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

    public GuiCustomStructure() {
        super("Custom Structure");
    }

    @Override
    protected void Initialize() {
        // TODO: Create Stuff Here
        super.Initialize();
    }

    @Override
    public Component getNarrationMessage() {
        return new TranslatableComponent(this.selectedStructure.displayName);
    }

    @Override
    protected void preButtonRender(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        // Enable/disable button options here.
    }

    @Override
    protected void postButtonRender(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        // Write out text here for labels.
    }

    @Override
    public void buttonClicked(AbstractButton button) {
        this.performCancelOrBuildOrHouseFacing(button);

        if (button == this.btnVisualize) {
            this.performPreview();
        }
    }
}
