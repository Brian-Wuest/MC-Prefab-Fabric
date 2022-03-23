package com.wuest.prefab.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.blocks.BlockStructureScanner;
import com.wuest.prefab.config.block_entities.DraftingTableConfiguration;
import com.wuest.prefab.gui.GuiBase;
import com.wuest.prefab.gui.controls.GuiListBox;
import com.wuest.prefab.gui.controls.GuiTextBox;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import com.wuest.prefab.gui.controls.GuiListBox.ListEntry;

import java.awt.*;

public class GuiDraftingTable extends GuiBase {
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

        this.modifiedInitialXAxis = 170;
        this.modifiedInitialYAxis = 100;

        Tuple<Integer, Integer> adjustedXYValues = this.getAdjustedXYValue();
        int adjustedX = adjustedXYValues.first;
        int adjustedY = adjustedXYValues.second;

        // Starting position.
        int bufferColor = new Color(198, 198, 198).getRGB();
        this.structureList = new GuiListBox(this.minecraft, 120, 100, adjustedX + 10, adjustedY + 25, 16, bufferColor, this::structureListEntryChanged);
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
        this.drawControlBackground(matrixStack, x, y, 350, 250);
    }

    @Override
    protected void postButtonRender(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        // Draw Text here.
        this.drawString(matrixStack, this.entryText, x + 10, y + 15, this.textColor);
    }

    @Override
    public void buttonClicked(AbstractButton button) {

    }

    public void structureListEntryChanged(ListEntry newEntry) {
        this.entryText = newEntry.getText();
    }
}
