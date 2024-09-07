package com.wuest.prefab.gui.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.Utils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ExtendedButton extends Button {
    public float fontScale = 1;

    public ExtendedButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler, @Nullable String label) {
        super(xPos, yPos, width, height, displayString, handler, Button.DEFAULT_NARRATION);
    }

    @Override
    public void renderString(GuiGraphics guiGraphics, Font font, int i) {
        Component buttonText = this.getMessage();
        int strWidth = font.width(buttonText);
        int ellipsisWidth = font.width("...");

        if (strWidth > width - 6 && strWidth > ellipsisWidth) {
            buttonText = Utils.createTextComponent(font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");
        }

        PoseStack originalStack = new PoseStack();

        originalStack.pushPose();
        originalStack.scale(this.fontScale, this.fontScale, this.fontScale);

        int xPosition = (int) ((this.getX() + this.width / 2) / this.fontScale);
        int yPosition = (int) ((this.getY() + (this.height - (8 * this.fontScale)) / 2) / this.fontScale);

        guiGraphics.drawCenteredString(font, buttonText, xPosition, yPosition, this.getFGColor());
        originalStack.popPose();
    }


    public int getFGColor() {
        return this.active ? 16777215 : 10526880; // White : Light Grey
    }
}
