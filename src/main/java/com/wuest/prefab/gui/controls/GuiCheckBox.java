package com.wuest.prefab.gui.controls;


import com.mojang.blaze3d.systems.RenderSystem;
import com.wuest.prefab.Utils;
import com.wuest.prefab.gui.GuiUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

/**
 * @author WuestMan
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class GuiCheckBox extends AbstractButton {
    private static final ResourceLocation buttonTexture = new ResourceLocation("prefab", "textures/gui/prefab_checkbox.png");
    private static final ResourceLocation buttonTexturePressed = new ResourceLocation("prefab", "textures/gui/prefab_checkbox_selected.png");
    private static final ResourceLocation buttonTextureHover = new ResourceLocation("prefab", "textures/gui/prefab_checkbox_hover.png");
    private static final ResourceLocation buttonTextureHoverSelected = new ResourceLocation("prefab", "textures/gui/prefab_checkbox_hover_selected.png");

    protected int boxWidth;
    protected int boxHeight;
    protected int stringColor;
    protected boolean withShadow;
    protected Minecraft mineCraft;
    protected String displayString;
    protected IPressable handler;
    protected int labelWidth;
    protected boolean isChecked;

    public GuiCheckBox(int xPos, int yPos, String displayString, boolean isChecked, IPressable handler) {
        super(xPos, yPos, 11, 12, Utils.createTextComponent(displayString));

        this.boxWidth = 11;
        this.mineCraft = Minecraft.getInstance();
        this.displayString = displayString;
        this.stringColor = Color.DARK_GRAY.getRGB();
        this.handler = handler;
        this.withShadow = false;
        this.labelWidth = 98;
        this.isChecked = isChecked;
    }

    /**
     * Gets the string color to write.
     *
     * @return The color used when writing the string value of this checkbox.
     */
    public int getStringColor() {
        return this.stringColor;
    }

    /**
     * Sets the color used when writing the text for this checkbox.
     *
     * @param color The color used for the text.
     * @return An updated instance of this class.
     */
    public GuiCheckBox setStringColor(int color) {
        this.stringColor = color;
        return this;
    }

    @Override
    public void onPress() {
        this.isChecked = !this.isChecked;

        if (this.handler != null) {
            this.handler.onPress(this);
        }
    }

    /**
     * Gets a value indicating whether a shadow is included with the checkbox text.
     *
     * @return The value of whether shadows are included when writing the text of this checkbox.
     */
    public boolean getWithShadow() {
        return this.withShadow;
    }

    /**
     * Sets the value of whether shadows are included when writing the text of this checkbox.
     *
     * @param value The new value of the property.
     * @return An updated instance of this class
     */
    public GuiCheckBox setWithShadow(boolean value) {
        this.withShadow = value;
        return this;
    }

    public GuiCheckBox setLabelWidth(int value) {
        this.labelWidth = value;
        return this;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            ResourceLocation resourceLocation = GuiCheckBox.buttonTexture;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.boxWidth && mouseY < this.getY() + this.height;

            if (this.isChecked()) {
                resourceLocation = GuiCheckBox.buttonTexturePressed;

                if (this.isHovered) {
                    resourceLocation = GuiCheckBox.buttonTextureHoverSelected;
                }
            } else if (this.isHovered) {
                resourceLocation = GuiCheckBox.buttonTextureHover;
            }

            GuiUtils.bindTexture(resourceLocation);

            GuiUtils.drawTexture(resourceLocation, guiGraphics, this.getX(), this.getY(), 1, 11, 11, 11, 11);

            int color = this.stringColor;

            if (this.withShadow) {
                guiGraphics.drawString(this.mineCraft.font, displayString, this.getX() + this.boxWidth + 2, this.getY() + 4, color);
            } else {
                guiGraphics.drawWordWrap(this.mineCraft.font, Utils.createTextComponent(displayString), this.getX() + this.boxWidth + 2, this.getY() + 2, this.labelWidth, color);
            }
        }
    }

    @Override
    public MutableComponent createNarrationMessage() {
        Component state = isChecked ? Component.translatable("options.on") : Component.translatable("options.off");
        String msg = displayString + ": ";
        return Component.translatable("narration.checkbox", Component.literal(msg).append(state));
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
        builder.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                builder.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
            } else {
                builder.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public interface IPressable {
        void onPress(GuiCheckBox p_onPress_1_);
    }
}
