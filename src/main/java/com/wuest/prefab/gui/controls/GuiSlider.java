package com.wuest.prefab.gui.controls;

import com.wuest.prefab.gui.GuiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class GuiSlider extends ButtonWidget {
	/** The value of this slider control. */
	public double sliderValue = 1.0F;

	public Text dispString;

	/** Is this slider control being dragged. */
	public boolean dragging = false;
	public boolean showDecimal = true;

	public double minValue = 0.0D;
	public double maxValue = 5.0D;
	public int precision = 1;

	@Nullable
	public ISlider parent = null;

	public Text suffix;

	public boolean drawString = true;

	public GuiSlider(int xPos, int yPos, int width, int height, Text prefix, Text suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, PressAction handler)
	{
		this(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, handler, null);
	}

	public GuiSlider(int xPos, int yPos, int width, int height, Text prefix, Text suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, PressAction handler, @Nullable ISlider par)
	{
		super(xPos, yPos, width, height, prefix, handler);
		minValue = minVal;
		maxValue = maxVal;
		sliderValue = (currentVal - minValue) / (maxValue - minValue);
		dispString = prefix;
		parent = par;
		suffix = suf;
		showDecimal = showDec;
		String val;

		if (showDecimal)
		{
			val = Double.toString(sliderValue * (maxValue - minValue) + minValue);
			precision = Math.min(val.substring(val.indexOf(".") + 1).length(), 4);
		}
		else
		{
			val = Integer.toString((int)Math.round(sliderValue * (maxValue - minValue) + minValue));
			precision = 0;
		}

		setMessage(new LiteralText("").append(dispString).append(val).append(suffix));

		drawString = drawStr;
		if(!drawString)
			setMessage(new LiteralText(""));
	}

	public GuiSlider(int xPos, int yPos, Text displayStr, double minVal, double maxVal, double currentVal, PressAction handler, ISlider par)
	{
		this(xPos, yPos, 150, 20, displayStr, new LiteralText(""), minVal, maxVal, currentVal, true, true, handler, par);
	}

	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
	 * this button.
	 */
	@Override
	public int getYImage(boolean par1)
	{
		return 0;
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
	 */
	@Override
	protected void renderBackground(MatrixStack mStack, MinecraftClient par1Minecraft, int par2, int par3)
	{
		if (this.visible)
		{
			if (this.dragging)
			{
				this.sliderValue = (par2 - (this.x + 4)) / (float)(this.width - 8);
				updateSlider();
			}

			GuiUtils.drawContinuousTexturedBox(WIDGETS_TEXTURE, this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 66, 8, this.height, 200, 20, 2, 3, 2, 2, this.getZOffset());
		}
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
	 * e).
	 */
	@Override
	public void onClick(double mouseX, double mouseY)
	{
		this.sliderValue = (mouseX - (this.x + 4)) / (this.width - 8);
		updateSlider();
		this.dragging = true;
	}

	public void updateSlider()
	{
		if (this.sliderValue < 0.0F)
		{
			this.sliderValue = 0.0F;
		}

		if (this.sliderValue > 1.0F)
		{
			this.sliderValue = 1.0F;
		}

		String val;

		if (showDecimal)
		{
			val = Double.toString(sliderValue * (maxValue - minValue) + minValue);

			if (val.substring(val.indexOf(".") + 1).length() > precision)
			{
				val = val.substring(0, val.indexOf(".") + precision + 1);

				if (val.endsWith("."))
				{
					val = val.substring(0, val.indexOf(".") + precision);
				}
			}
			else
			{
				while (val.substring(val.indexOf(".") + 1).length() < precision)
				{
					val = val + "0";
				}
			}
		}
		else
		{
			val = Integer.toString((int)Math.round(sliderValue * (maxValue - minValue) + minValue));
		}

		if(drawString)
		{
			setMessage(new LiteralText("").append(dispString).append(val).append(suffix));
		}

		if (parent != null)
		{
			parent.onChangeSliderValue(this);
		}
	}

	/**
	 * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
	 */
	@Override
	public void onRelease(double mouseX, double mouseY)
	{
		this.dragging = false;
	}

	public int getValueInt()
	{
		return (int)Math.round(sliderValue * (maxValue - minValue) + minValue);
	}

	public double getValue()
	{
		return sliderValue * (maxValue - minValue) + minValue;
	}

	public void setValue(double d)
	{
		this.sliderValue = (d - minValue) / (maxValue - minValue);
	}

	public static interface ISlider
	{
		void onChangeSliderValue(GuiSlider slider);
	}
}
