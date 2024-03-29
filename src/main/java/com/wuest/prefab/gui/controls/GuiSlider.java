package com.wuest.prefab.gui.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.Utils;
import com.wuest.prefab.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class GuiSlider extends Button {
    /**
     * The value of this slider control.
     */
    public double sliderValue = 1.0F;

    public Component dispString;

    /**
     * Is this slider control being dragged.
     */
    public boolean dragging = false;
    public boolean showDecimal = true;

    public double minValue = 0.0D;
    public double maxValue = 5.0D;
    public int precision = 1;

    public ISlider parent = null;

    public Component suffix;

    public boolean drawString = true;

    public GuiSlider(int xPos, int yPos, int width, int height, Component prefix, Component suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, OnPress handler) {
        this(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, handler, null);
    }

    public GuiSlider(int xPos, int yPos, int width, int height, Component prefix, Component suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, OnPress handler, ISlider par) {
        super(xPos, yPos, width, height, prefix, handler, Button.DEFAULT_NARRATION);
        minValue = minVal;
        maxValue = maxVal;
        sliderValue = (currentVal - minValue) / (maxValue - minValue);
        dispString = prefix;
        parent = par;
        suffix = suf;
        showDecimal = showDec;
        String val;

        if (showDecimal) {
            val = Double.toString(sliderValue * (maxValue - minValue) + minValue);
            precision = Math.min(val.substring(val.indexOf(".") + 1).length(), 4);
        } else {
            val = Integer.toString((int) Math.round(sliderValue * (maxValue - minValue) + minValue));
            precision = 0;
        }

        setMessage(Utils.createTextComponent("").append(dispString).append(val).append(suffix));

        drawString = drawStr;
        if (!drawString)
            setMessage(Utils.createTextComponent(""));
    }

    public GuiSlider(int xPos, int yPos, Component displayStr, double minVal, double maxVal, double currentVal, OnPress handler, ISlider par) {
        this(xPos, yPos, 150, 20, displayStr, Utils.createTextComponent(""), minVal, maxVal, currentVal, true, true, handler, par);
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    @Override
    public void onClick(double mouseX, double mouseY) {
        this.sliderValue = (mouseX - (this.getX() + 4)) / (this.width - 8);
        updateSlider();
        this.dragging = true;
    }

    public void updateSlider() {
        if (this.sliderValue < 0.0F) {
            this.sliderValue = 0.0F;
        }

        if (this.sliderValue > 1.0F) {
            this.sliderValue = 1.0F;
        }

        String val;

        if (showDecimal) {
            val = Double.toString(sliderValue * (maxValue - minValue) + minValue);

            if (val.substring(val.indexOf(".") + 1).length() > precision) {
                val = val.substring(0, val.indexOf(".") + precision + 1);

                if (val.endsWith(".")) {
                    val = val.substring(0, val.indexOf(".") + precision);
                }
            } else {
                while (val.substring(val.indexOf(".") + 1).length() < precision) {
                    val = val + "0";
                }
            }
        } else {
            val = Integer.toString((int) Math.round(sliderValue * (maxValue - minValue) + minValue));
        }

        if (drawString) {
            setMessage(Utils.createTextComponent("").append(dispString).append(val).append(suffix));
        }

        if (parent != null) {
            parent.onChangeSliderValue(this);
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.dragging = false;
    }

    public int getValueInt() {
        return (int) Math.round(sliderValue * (maxValue - minValue) + minValue);
    }

    public double getValue() {
        return sliderValue * (maxValue - minValue) + minValue;
    }

    public void setValue(double d) {
        this.sliderValue = (d - minValue) / (maxValue - minValue);
    }

    public static interface ISlider {
        void onChangeSliderValue(GuiSlider slider);
    }
}
