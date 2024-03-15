package com.wuest.prefab.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ExtendedButton extends Button {
    private static final ResourceLocation WIDGETS_LOCATION =  new ResourceLocation("textures/gui/widgets.png");
    private final String label;
    public float fontScale = 1;

    public ExtendedButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler, @Nullable String label) {
        super(xPos, yPos, width, height, displayString, handler, Button.DEFAULT_NARRATION);
        this.label = label;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

            int i = !this.active ? 0 : (this.isHoveredOrFocused() ? 2 : 1);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
//            this.blit(mStack, this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height);
//            this.blit(mStack, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

            Component buttonText = this.getMessage();
            int strWidth = mc.font.width(buttonText);
            int ellipsisWidth = mc.font.width("...");

            if (strWidth > width - 6 && strWidth > ellipsisWidth) {
                buttonText = Utils.createTextComponent(mc.font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");
            }

            PoseStack originalStack = new PoseStack();

            originalStack.pushPose();
            originalStack.scale(this.fontScale, this.fontScale, this.fontScale);

            int xPosition = (int) ((this.getX() + this.width / 2) / this.fontScale);
            int yPosition = (int) ((this.getY() + (this.height - (8 * this.fontScale)) / 2) / this.fontScale);

            guiGraphics.drawCenteredString(mc.font,buttonText,xPosition,yPosition,this.getFGColor());
            originalStack.popPose();
        }
    }

    public int getFGColor() {
        return this.active ? 16777215 : 10526880; // White : Light Grey
    }
}
