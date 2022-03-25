package com.wuest.prefab.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.blocks.BlockStructureScanner;
import com.wuest.prefab.config.block_entities.DraftingTableConfiguration;
import com.wuest.prefab.gui.GuiBase;
import com.wuest.prefab.gui.GuiUtils;
import com.wuest.prefab.gui.controls.GuiListBox;
import com.wuest.prefab.gui.controls.GuiTextBox;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import com.wuest.prefab.gui.controls.GuiListBox.ListEntry;

import java.awt.*;

public class GuiDraftingTable extends GuiBase {
    private final ResourceLocation backgroundTexture = new ResourceLocation("prefab", "textures/gui/drafter.png");

    private final BlockPos blockPos;
    private final Level world;
    private DraftingTableConfiguration config;
    private GuiListBox structureList;
    private String entryText;

    public GuiDraftingTable(BlockPos blockPos, Level world, DraftingTableConfiguration config) {
        super("Drafting Table");

        this.blockPos = blockPos;
        this.world = world;
        this.config = config;
        this.config.blockPos = this.blockPos;
    }

    @Override
    protected void Initialize() {
        super.Initialize();

        this.modifiedInitialXAxis = 88;
        this.modifiedInitialYAxis = 120;

        Tuple<Integer, Integer> adjustedXYValues = this.getAdjustedXYValue();
        int adjustedX = adjustedXYValues.first;
        int adjustedY = adjustedXYValues.second;

        // Starting position.
        int bufferColor = new Color(198, 198, 198).getRGB();
        this.structureList = new GuiListBox(this.minecraft, 157, 100, adjustedX + 7, adjustedY + 19, 16, bufferColor, this::structureListEntryChanged);
        this.structureList.addEntry("Some cool value");
        this.structureList.addEntry("Some other cool value");
        this.structureList.addEntry("Some other new cool value");
        this.structureList.addEntry("Some other old and broken value");
        this.structureList.addEntry("Just some text I wrote when drunk");
        this.structureList.addEntry("Just some other text I wrote when drunk");
        this.structureList.addEntry("Just some new text I wrote when drunk");
        this.structureList.addEntry("Just some old text I wrote when drunk");
        this.addRenderableWidget(this.structureList);
    }

    @Override
    protected void preButtonRender(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        
        GuiUtils.bindAndDrawScaledTexture(
                this.backgroundTexture,
                matrixStack,
                x,
                y,
                176,
                237,
                176,
                237,
                176,
                237);
    }

    @Override
    protected void postButtonRender(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        this.drawItemBackground(x + 150, y + 130, 0, 0);

        // Draw Text here.
        this.drawString(matrixStack, this.entryText, x + 10, y + 15, this.textColor);
    }

    @Override
    public void buttonClicked(AbstractButton button) {

    }

    /**
     * Draws the background icon for an item, using a texture from stats.png with the given coords
     */
    public void drawItemBackground(int x, int z, int textureX, int textureY) {
        GuiUtils.bindTexture(GuiComponent.STATS_ICON_LOCATION);
        float f = 0.0078125F;
        float f1 = 0.0078125F;
        int i = 18;
        int j = 18;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuilder();
        vertexbuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        vertexbuffer.vertex(x, (z + 18), 0).uv(0, ((float) (textureY + 18) * 0.0078125F)).endVertex();
        vertexbuffer.vertex((x + 18), (z + 18), 0).uv(((float) (textureX + 18) * 0.0078125F), ((float) (textureY + 18) * 0.0078125F)).endVertex();
        vertexbuffer.vertex((x + 18), z, 0).uv(((float) (textureX + 18) * 0.0078125F), 0).endVertex();
        vertexbuffer.vertex(x, z, 0).uv(0, 0).endVertex();
        tessellator.end();
    }

    public void structureListEntryChanged(ListEntry newEntry) {
        this.entryText = newEntry.getText();
    }
}
