package com.wuest.prefab.gui.controls;


import com.mojang.blaze3d.systems.RenderSystem;
import com.wuest.prefab.Utils;
import com.wuest.prefab.gui.GuiUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.awt.*;

/**
 * @author WuestMan
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class GuiCheckBox extends PressableWidget {
    private static final Identifier buttonTexture = new Identifier("prefab", "textures/gui/prefab_checkbox.png");
    private static final Identifier buttonTexturePressed = new Identifier("prefab", "textures/gui/prefab_checkbox_selected.png");
    private static final Identifier buttonTextureHover = new Identifier("prefab", "textures/gui/prefab_checkbox_hover.png");
    private static final Identifier buttonTextureHoverSelected = new Identifier("prefab", "textures/gui/prefab_checkbox_hover_selected.png");

    protected int boxWidth;
    protected int boxHeight;
    protected int stringColor;
    protected boolean withShadow;
    protected MinecraftClient mineCraft;
    protected String displayString;
    protected PressAction handler;
    protected int labelWidth;
    protected boolean isChecked;

    public GuiCheckBox(int xPos, int yPos, String displayString, boolean isChecked, PressAction handler) {
        super(xPos, yPos, 11, 12, Utils.createTextComponent(displayString));

        this.boxWidth = 11;
        this.mineCraft = MinecraftClient.getInstance();
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

    public boolean isChecked()
    {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked)
    {
        this.isChecked = isChecked;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Identifier resourceLocation = GuiCheckBox.buttonTexture;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.boxWidth && mouseY < this.y + this.height;

            if (this.isChecked()) {
                resourceLocation = GuiCheckBox.buttonTexturePressed;

                if (this.hovered) {
                    resourceLocation = GuiCheckBox.buttonTextureHoverSelected;
                }
            } else if (this.hovered) {
                resourceLocation = GuiCheckBox.buttonTextureHover;
            }

            GuiUtils.bindTexture(resourceLocation);

            GuiUtils.drawTexture(matrixStack, this.x, this.y, 1, 11, 11, 11, 11);

            int color = this.stringColor;

            if (this.withShadow) {
                this.mineCraft.textRenderer.drawWithShadow(matrixStack, displayString, x + this.boxWidth + 2, y + 2, color);
            } else {
                this.mineCraft.textRenderer.drawTrimmed(Utils.createTextComponent(displayString), x + this.boxWidth + 2, y + 2, this.labelWidth, color);
            }
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, new TranslatableText("narration.checkbox.usage.focused"));
            } else {
                builder.put(NarrationPart.USAGE, new TranslatableText("narration.checkbox.usage.hovered"));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(GuiCheckBox p_onPress_1_);
    }
}
