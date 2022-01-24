package com.wuest.prefab.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wuest.prefab.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ExtendedButton extends PressableWidget {
	public float fontScale = 1;
	private final String label;
	private static final ButtonWidget.TooltipSupplier toolTipSupplier = (button, matrices, mouseX, mouseY) -> {};
	private final PressAction pressAction;
	private final boolean shouldRenderToolTip;

	public ExtendedButton(int xPos, int yPos, int width, int height, Text displayString, PressAction handler, @Nullable String label) {
		super(xPos, yPos, width, height, displayString);
		pressAction = handler;
		this.label = label;
		this.shouldRenderToolTip = true;
	}

	public ExtendedButton(int xPos, int yPos, int width, int height, Text displayString, PressAction handler) {
		super(xPos, yPos, width, height, displayString);
		pressAction = handler;
		this.label = null;
		this.shouldRenderToolTip = true;
	}

	public ExtendedButton(int xPos, int yPos, int width, int height, Text displayString, PressAction handler, @Nullable String label, boolean shouldRenderTooltip) {
		super(xPos, yPos, width, height, displayString);
		pressAction = handler;
		this.label = label;
		this.shouldRenderToolTip = shouldRenderTooltip;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible) {
			if (button == 1) {
				boolean didClickOnButton = this.clicked(mouseX, mouseY);

				if (didClickOnButton) {
					this.playDownSound(MinecraftClient.getInstance().getSoundManager());
					this.onRightClick(mouseX, mouseY);
					return true;
				}
			} else {
				return super.mouseClicked(mouseX, mouseY, button);
			}
		}
		return false;
	}

	@Override
	public void onPress() {
		pressAction.onPress(this, false);
	}

	/**
	 * Processes secondary clicks
	 */
	public void onRightPress() {
		pressAction.onPress(this, true);
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		this.onPress();
	}

	/**
	 * Processes secondary clicks
	 * @param mouseX Horizontal mouse location
	 * @param mouseY Vertical mouse location
	 */
	public void onRightClick(double mouseX, double mouseY) {
		this.onRightPress();
	}

	@Override
	protected MutableText getNarrationMessage() {
		if (label == null) {
			return super.getNarrationMessage();
		} else {
			return new LiteralText(label + ": ").append(super.getNarrationMessage());
		}
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial) {
		if (this.visible) {
			MinecraftClient mc = MinecraftClient.getInstance();
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

			int i = this.getYImage(this.isHovered());

			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			this.drawTexture(mStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
			this.drawTexture(mStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

			this.renderBackground(mStack, mc, mouseX, mouseY);

			Text buttonText = this.getMessage();
			int strWidth = mc.textRenderer.getWidth(buttonText);
			int ellipsisWidth = mc.textRenderer.getWidth("...");

			if (strWidth > width - 6 && strWidth > ellipsisWidth) {
				buttonText = Utils.createTextComponent(mc.textRenderer.trimToWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");
			}

			MatrixStack originalStack = new MatrixStack();

			originalStack.push();
			originalStack.scale(this.fontScale, this.fontScale, this.fontScale);

			int xPosition = (int) ((this.x + this.width / 2) / this.fontScale);
			int yPosition = (int) ((this.y + (this.height - (8 * this.fontScale)) / 2) / this.fontScale);

			DrawableHelper.drawCenteredText(originalStack, mc.textRenderer, buttonText, xPosition, yPosition, this.getFGColor());
			originalStack.pop();

			// Render the toolTip

			ArrayList<Text> texts = new ArrayList<>();
			texts.add(new TranslatableText("prefab.gui.button_desc.1"));
			texts.add(new TranslatableText("prefab.gui.button_desc.2"));

			if (mc.currentScreen == null) {
				return;
			}

			if (!isMouseOver(mouseX, mouseY)) {
				return;
			}

			if (!shouldRenderToolTip) {
				return;
			}

			// Make it not overlap the button
			mouseY += this.height * 1.25;

			mc.currentScreen.renderTooltip(mStack, texts, mouseX, mouseY);
		}
	}

	public int getFGColor() {
		return this.active ? 16777215 : 10526880; // White : Light Grey
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		this.appendDefaultNarrations(builder);
		toolTipSupplier.supply((text) -> {
			builder.put(NarrationPart.HINT, text);
		});
	}

	@Environment(EnvType.CLIENT)
	public interface PressAction {
		void onPress(PressableWidget button, boolean isRightClick);
	}
}
