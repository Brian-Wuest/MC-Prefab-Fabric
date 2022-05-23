package com.wuest.prefab.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class TextureButton extends ExtendedButton {
    private ResourceLocation defaultTexture;
    private ResourceLocation hoverTexture;
    private ResourceLocation selectedTexture;
    private ResourceLocation selectedHoverTexture;
    private boolean isToggleButton;
    private boolean isSelected;

    private final boolean renderDefaultButtonBackground;

    private final int textureHeight;
    private final int textureWidth;

    public TextureButton(int xPos, int yPos, int width, int height, int textureWidth, int textureHeight, OnPress handler) {
        super(xPos, yPos, width, height, new TextComponent(""), handler, null);
        this.isToggleButton = false;
        this.isSelected = false;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.renderDefaultButtonBackground = true;
    }

    public TextureButton(int xPos, int yPos, int width, int height, OnPress handler) {
        super(xPos, yPos, width, height, new TextComponent(""), handler, null);
        this.isToggleButton = false;
        this.isSelected = false;
        this.renderDefaultButtonBackground = false;
        this.textureHeight = height;
        this.textureWidth = width;
    }

    public TextureButton setDefaultTexture(ResourceLocation value) {
        this.defaultTexture = value;
        return this;
    }

    public TextureButton setHoverTexture(ResourceLocation value) {
        this.hoverTexture = value;
        return this;
    }

    public TextureButton setSelectedTexture(ResourceLocation value) {
        this.selectedTexture = value;
        return this;
    }

    public TextureButton setSelectedHoverTexture(ResourceLocation value) {
        this.selectedHoverTexture = value;
        return this;
    }

    public TextureButton setIsToggleButton() {
        this.isToggleButton = true;
        return this;
    }

    public TextureButton setIsSelected(boolean value) {
        this.isSelected = value;
        return this;
    }

    @Override
    public void onPress() {
        if (this.isSelected && this.isToggleButton) {
            // When this is a toggle button and it's already selected; don't allow the click event to trigger.
            return;
        }

        this.onPress.onPress(this);

        if (this.isToggleButton) {
            this.isSelected = true;
        }
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void renderButton(PoseStack mStack, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            int xPosition = this.x;
            int yPosition = this.y;

            if (this.renderDefaultButtonBackground) {
                this.renderStandardBackground(mStack, mouseX, mouseY, mc);
                xPosition = this.x + (this.width / 2) - (this.textureWidth / 2);
                yPosition = this.y + (this.height / 2) - (this.textureHeight / 2);
            }

            ResourceLocation buttonTexture = this.getButtonTexture();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, buttonTexture);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

            GuiUtils.bindAndDrawScaledTexture(mStack, xPosition, yPosition, this.textureWidth, this.textureHeight, this.textureWidth, this.textureHeight, this.textureWidth, this.textureHeight);
        }
    }

    private void renderStandardBackground(PoseStack poseStack, int mouseX, int mouseY, Minecraft mc) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

        int i = this.getYImage(this.isHoveredOrFocused());

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.blit(poseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

        this.renderBg(poseStack, mc, mouseX, mouseY);
    }

    private ResourceLocation getButtonTexture() {
        if (this.isSelected && this.selectedTexture != null) {
            if (this.isHovered && this.selectedHoverTexture != null) {
                return this.selectedHoverTexture;
            }

            return this.selectedTexture;
        } else if (this.isHovered && this.hoverTexture != null) {
            return this.hoverTexture;
        }

        return this.defaultTexture;
    }
}
