package com.wuest.prefab.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.config.block_entities.DraftingTableConfiguration;
import com.wuest.prefab.gui.GuiBase;
import com.wuest.prefab.gui.GuiUtils;
import com.wuest.prefab.gui.controls.GuiItemList;
import com.wuest.prefab.gui.controls.GuiListBox;
import com.wuest.prefab.gui.controls.GuiListBox.ListEntry;
import com.wuest.prefab.gui.controls.TextureButton;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.awt.*;

public class GuiDraftingTable extends GuiBase {
    private final ResourceLocation backgroundTexture = new ResourceLocation("prefab", "textures/gui/drafter.png");
    private final ResourceLocation schematicDefault = new ResourceLocation("prefab", "textures/gui/schematics.png");
    private final ResourceLocation schematicSelected = new ResourceLocation("prefab", "textures/gui/schematics_selected.png");
    private final ResourceLocation schematicHover = new ResourceLocation("prefab", "textures/gui/schematics_hovered.png");
    private final ResourceLocation schematicHoverSelected = new ResourceLocation("prefab", "textures/gui/schematics_selected_hovered.png");

    private final ResourceLocation materialDefault = new ResourceLocation("prefab", "textures/gui/materials.png");
    private final ResourceLocation materialSelected = new ResourceLocation("prefab", "textures/gui/materials_selected.png");
    private final ResourceLocation materialHover = new ResourceLocation("prefab", "textures/gui/materials_hovered.png");
    private final ResourceLocation materialHoverSelected = new ResourceLocation("prefab", "textures/gui/materials_selected_hovered.png");

    private final BlockPos blockPos;
    private final Level world;
    private DraftingTableConfiguration config;
    private TextureButton schematicsButton;
    private TextureButton materialsButton;
    private GuiListBox schematicsList;
    private GuiItemList materialsList;
    private boolean showingMaterials;

    public GuiDraftingTable(BlockPos blockPos, Level world, DraftingTableConfiguration config) {
        super("Drafting Table");

        this.blockPos = blockPos;
        this.world = world;
        this.config = config;
        this.config.blockPos = this.blockPos;
        this.showingMaterials = false;
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
        this.schematicsList = new GuiListBox(this.minecraft, 157, 100, adjustedX + 7, adjustedY + 22, 16, bufferColor, this::structureListEntryChanged);
        this.schematicsList.addEntry("Some cool value");
        this.schematicsList.addEntry("Some other cool value");
        this.schematicsList.addEntry("Some other new cool value");
        this.schematicsList.addEntry("Some other old and broken value");
        this.schematicsList.addEntry("Just some text I wrote when drunk");
        this.schematicsList.addEntry("Just some other text I wrote when drunk");
        this.schematicsList.addEntry("Just some new text I wrote when drunk");
        this.schematicsList.addEntry("Just some old text I wrote when drunk");
        this.schematicsList.addEntry("Just some other text I wrote when drunk");
        this.schematicsList.addEntry("Just some super other text I wrote when drunk");

        if (!this.showingMaterials) {
            this.addRenderableWidget(this.schematicsList);
        }

        this.materialsList = new GuiItemList(this.minecraft, 157, 100, adjustedX + 7, adjustedY + 22, 22, bufferColor, null);
        this.materialsList.setVisible(this.showingMaterials);
        this.materialsList.addEntry(ModRegistry.DoubleCompressedQuartzCreteItem, 3, 2);
        this.materialsList.addEntry(ModRegistry.Pencil, 3, 3);
        this.materialsList.addEntry(Items.CHEST, 1, 2);
        this.materialsList.addEntry(Items.GLOW_ITEM_FRAME, 1, 2);
        this.materialsList.addEntry(Items.ACACIA_SLAB, 1, 2);
        this.materialsList.addEntry(Items.AZURE_BLUET, 1, 2);
        this.materialsList.addEntry(Items.BIRCH_STAIRS, 4, 2);
        this.materialsList.addEntry(Items.INFESTED_STONE_BRICKS, 1, 2);
        this.materialsList.addEntry(Items.IRON_HOE, 1, 2);
        this.materialsList.addEntry(Items.GREEN_DYE, 1, 2);
        this.materialsList.addEntry(Items.GRAY_BED, 2, 2);
        this.materialsList.addEntry(Items.IRON_CHESTPLATE, 7, 2);
        this.materialsList.addEntry(Items.IRON_HORSE_ARMOR, 1, 2);

        if (this.showingMaterials) {
            this.addRenderableWidget(this.materialsList);
        }

        this.schematicsButton = new TextureButton(adjustedX + 7, adjustedY + 130, 18, 18, this::buttonClicked);
        this.schematicsButton
                .setIsToggleButton()
                .setDefaultTexture(this.schematicDefault)
                .setHoverTexture(this.schematicHover)
                .setSelectedTexture(this.schematicSelected)
                .setSelectedHoverTexture(this.schematicHoverSelected)
                .setIsSelected(!this.showingMaterials);
        this.addRenderableWidget(this.schematicsButton);

        this.materialsButton = new TextureButton(adjustedX + 30, adjustedY + 130, 18, 18, this::buttonClicked);
        this.materialsButton
                .setIsToggleButton()
                .setDefaultTexture(this.materialDefault)
                .setHoverTexture(this.materialHover)
                .setSelectedTexture(this.materialSelected)
                .setSelectedHoverTexture(this.materialHoverSelected)
                .setIsSelected(this.showingMaterials);
        this.addRenderableWidget(this.materialsButton);
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
        GuiUtils.drawItemBackground(x + 151, y + 130);

        // Draw Text here.
        this.drawString(matrixStack, "Available Structures", x + 10, y + 10, this.textColor);
    }

    @Override
    public void buttonClicked(AbstractButton button) {
        if (button == this.schematicsButton) {
            this.materialsButton.setIsSelected(false);
            this.showingMaterials = false;
            this.schematicsList.setVisible(true);
            this.materialsList.setVisible(false);
            this.removeWidget(this.materialsList);
            this.removeWidget(this.materialsButton);
            this.removeWidget(this.schematicsButton);
            this.addRenderableWidget(this.schematicsList);
            this.addRenderableWidget(this.materialsButton);
            this.addRenderableWidget(this.schematicsButton);
        } else if (button == this.materialsButton) {
            this.schematicsButton.setIsSelected(false);
            this.showingMaterials = true;
            this.schematicsList.setVisible(false);
            this.materialsList.setVisible(true);
            this.removeWidget(this.schematicsList);
            this.removeWidget(this.materialsButton);
            this.removeWidget(this.schematicsButton);
            this.addRenderableWidget(this.materialsList);
            this.addRenderableWidget(this.materialsButton);
            this.addRenderableWidget(this.schematicsButton);
        }
    }

    public void structureListEntryChanged(ListEntry newEntry) {
    }
}
