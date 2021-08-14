package com.wuest.prefab.gui.controls;

import com.wuest.prefab.gui.GuiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ExtendedButton extends ButtonWidget {
	public ExtendedButton(int xPos, int yPos, int width, int height, Text displayString, PressAction handler) {
		super(xPos, yPos, width, height, displayString, handler);
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial) {
		if (this.visible) {
			MinecraftClient mc = MinecraftClient.getInstance();
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int k = this.getYImage(this.isHovered());
			GuiUtils.drawContinuousTexturedBox(WIDGETS_TEXTURE, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.getZOffset());
			this.renderBackground(mStack, mc, mouseX, mouseY);

			Text buttonText = this.getMessage();
			int strWidth = mc.textRenderer.getWidth(buttonText);
			int ellipsisWidth = mc.textRenderer.getWidth("...");

			if (strWidth > width - 6 && strWidth > ellipsisWidth)
				buttonText = new LiteralText(mc.textRenderer.trimToWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");

			this.drawCenteredText(mStack, mc.textRenderer, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, this.getFGColor());
		}
	}

	public int getFGColor() {
		return this.active ? 16777215 : 10526880; // White : Light Grey
	}
}
