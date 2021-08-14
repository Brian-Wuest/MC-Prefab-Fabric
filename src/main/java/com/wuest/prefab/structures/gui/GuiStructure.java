package com.wuest.prefab.structures.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.Utils;
import com.wuest.prefab.gui.GuiBase;
import com.wuest.prefab.structures.config.StructureConfiguration;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Generic GUI for all structures.
 *
 * @author WuestMan
 */
public abstract class GuiStructure extends GuiBase {
	public BlockPos pos;
	protected PlayerEntity player;
	protected ButtonWidget btnCancel;
	protected ButtonWidget btnBuild;
	protected ButtonWidget btnVisualize;
	protected int textColor = Color.DARK_GRAY.getRGB();
	protected StructureTagMessage.EnumStructureConfiguration structureConfiguration;
	private Direction structureFacing;

	public GuiStructure(String title) {
		super(title);
	}

	/**
	 * Draws a textured rectangle Args: x, y, z, width, height, textureWidth, textureHeight
	 *
	 * @param x             The X-Axis screen coordinate.
	 * @param y             The Y-Axis screen coordinate.
	 * @param z             The Z-Axis screen coordinate.
	 * @param width         The width of the rectangle.
	 * @param height        The height of the rectangle.
	 * @param textureWidth  The width of the texture.
	 * @param textureHeight The height of the texture.
	 */
	public static void drawModalRectWithCustomSizedTexture(int x, int y, int z, int width, int height, float textureWidth, float textureHeight) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

		float u = 0;
		float v = 0;
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();

		vertexBuffer.begin(7, VertexFormats.POSITION_TEXTURE);
		vertexBuffer.vertex(x, y + height, z).texture(u * f, (v + height) * f1).next();
		vertexBuffer.vertex(x + width, y + height, z).texture((u + width) * f, (v + height) * f1).next();
		vertexBuffer.vertex(x + width, y, z).texture((u + width) * f, v * f1).next();
		vertexBuffer.vertex(x, y, z).texture(u * f, v * f1).next();

		tessellator.draw();
	}

	@Override
	public void init() {
		this.player = this.client.player;
		this.structureFacing = this.player.getHorizontalFacing().getOpposite();
		this.Initialize();
	}

	/**
	 * This method is used to initialize GUI specific items.
	 */
	protected void Initialize() {
	}

	public void checkVisualizationSetting() {
		if (!Prefab.serverConfiguration.enableStructurePreview) {
			this.btnVisualize.visible = false;
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int x, int y, float f) {
		Tuple<Integer, Integer> adjustedXYValue = this.getAdjustedXYValue();

		this.preButtonRender(matrixStack, adjustedXYValue.getFirst(), adjustedXYValue.getSecond(), x, y, f);

		this.renderButtons(matrixStack, x, y);

		this.postButtonRender(matrixStack, adjustedXYValue.getFirst(), adjustedXYValue.getSecond(), x, y, f);

		if (this.btnVisualize != null) {
			this.checkVisualizationSetting();
		}
	}

	@Override
	protected void preButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);

		this.drawControlBackground(matrixStack, x, y);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void performCancelOrBuildOrHouseFacing(StructureConfiguration configuration, PressableWidget button) {
		configuration.houseFacing = this.structureFacing;

		if (button == this.btnCancel) {
			this.closeScreen();
		} else if (button == this.btnBuild) {
			PacketByteBuf messagePacket = Utils.createStructureMessageBuffer(configuration.WriteToCompoundNBT(), this.structureConfiguration);
			ClientPlayNetworking.send(ModRegistry.StructureBuild, messagePacket);

			this.closeScreen();
		}
	}
}
