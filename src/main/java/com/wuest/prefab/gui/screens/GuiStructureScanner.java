package com.wuest.prefab.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.Utils;
import com.wuest.prefab.blocks.BlockStructureScanner;
import com.wuest.prefab.config.block_entities.StructureScannerConfig;
import com.wuest.prefab.gui.GuiBase;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.gui.controls.GuiCheckBox;
import com.wuest.prefab.gui.controls.GuiTextBox;
import com.wuest.prefab.gui.controls.TextureButton;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.awt.*;

public class GuiStructureScanner extends GuiBase {
    private final ResourceLocation downTexture = new ResourceLocation("prefab", "textures/gui/down.png");
    private final ResourceLocation leftTexture = new ResourceLocation("prefab", "textures/gui/left.png");
    private final ResourceLocation rightTexture = new ResourceLocation("prefab", "textures/gui/right.png");
    private final ResourceLocation upTexture = new ResourceLocation("prefab", "textures/gui/up.png");
    private final BlockPos blockPos;
    private final Level world;
    private StructureScannerConfig config;

    private TextureButton btnStartingPositionMoveLeft;
    private TextureButton btnStartingPositionMoveRight;
    private TextureButton btnStartingPositionMoveDown;
    private TextureButton btnStartingPositionMoveUp;
    private TextureButton btnWidthGrow;
    private TextureButton btnWidthShrink;
    private TextureButton btnDepthGrow;
    private TextureButton btnDepthShrink;
    private TextureButton btnHeightGrow;
    private TextureButton btnHeightShrink;
    private GuiTextBox txtZipName;
    private ExtendedButton btnScan;
    private ExtendedButton btnSet;
    private ExtendedButton btnReSet;

    private GuiCheckBox hasGlassColorOptions;
    private GuiCheckBox hasBedColorOptions;

    public GuiStructureScanner(BlockPos blockPos, Level world, StructureScannerConfig config) {
        super("Structure Scanner");

        // Always assume that this block is 1 above ground level of the structure.
        this.blockPos = blockPos.relative(Direction.DOWN);
        this.world = world;
        this.config = config;
        this.config.blockPos = this.blockPos;
    }

    @Override
    protected void Initialize() {
        super.Initialize();

        this.config.direction = this.world.getBlockState(this.blockPos.relative(Direction.UP)).getValue(BlockStructureScanner.FACING);

        Tuple<Integer, Integer> adjustedXYValues = this.getAdjustedXYValue();
        int adjustedX = adjustedXYValues.first;
        int adjustedY = adjustedXYValues.second;

        // Starting position.
        this.btnStartingPositionMoveDown = this.createAndAddTextureButton(adjustedX + 33, adjustedY + 100, 25, 20, 10, 10);
        this.btnStartingPositionMoveDown.setDefaultTexture(this.downTexture);

        this.btnStartingPositionMoveLeft = this.createAndAddTextureButton(adjustedX + 20, adjustedY + 75, 25, 20, 10, 10);
        this.btnStartingPositionMoveLeft.setDefaultTexture(this.leftTexture);

        this.btnStartingPositionMoveRight = this.createAndAddTextureButton(adjustedX + 47, adjustedY + 75, 25, 20, 10, 10);
        this.btnStartingPositionMoveRight.setDefaultTexture(this.rightTexture);

        this.btnStartingPositionMoveUp = this.createAndAddTextureButton(adjustedX + 33, adjustedY + 50, 25, 20, 10, 10);
        this.btnStartingPositionMoveUp.setDefaultTexture(this.upTexture);

        // Depth
        this.btnDepthShrink = this.createAndAddTextureButton(adjustedX + 120, adjustedY + 30, 25, 20, 10, 10);
        this.btnDepthShrink.setDefaultTexture(this.downTexture);

        this.btnDepthGrow = this.createAndAddTextureButton(adjustedX + 147, adjustedY + 30, 25, 20, 10, 10);
        this.btnDepthGrow.setDefaultTexture(this.upTexture);

        // Width
        this.btnWidthShrink = this.createAndAddTextureButton(adjustedX + 200, adjustedY + 30, 25, 20, 10, 10);
        this.btnWidthShrink.setDefaultTexture(this.downTexture);

        this.btnWidthGrow = this.createAndAddTextureButton(adjustedX + 227, adjustedY + 30, 25, 20, 10, 10);
        this.btnWidthGrow.setDefaultTexture(this.upTexture);

        // Height
        this.btnHeightShrink = this.createAndAddTextureButton(adjustedX + 270, adjustedY + 30, 25, 20, 10, 10);
        this.btnHeightShrink.setDefaultTexture(this.downTexture);

        this.btnHeightGrow = this.createAndAddTextureButton(adjustedX + 297, adjustedY + 30, 25, 20, 10, 10);
        this.btnHeightGrow.setDefaultTexture(this.upTexture);

        // Zip Text Field
        this.txtZipName = new GuiTextBox(this.getMinecraft().font, adjustedX + 120, adjustedY + 110, 150, 20, new TextComponent(""));

        if (this.config.structureZipName == null || this.config.structureZipName.trim().equals("")) {
            this.txtZipName.setValue(GuiLangKeys.translateString(GuiLangKeys.STRUCTURE_NAME_HERE));
        } else {
            this.txtZipName.setValue(this.config.structureZipName);
        }

        this.txtZipName.setMaxLength(128);
        this.txtZipName.setBordered(true);
        this.txtZipName.backgroundColor = Color.WHITE.getRGB();
        this.txtZipName.setTextColor(Color.DARK_GRAY.getRGB());
        this.txtZipName.drawsTextShadow = false;
        this.addRenderableWidget(this.txtZipName);

        this.hasBedColorOptions = this.createAndAddCheckBox(adjustedX + 120, adjustedY + 65, "Has Bed Color Options", false, null);
        this.hasGlassColorOptions = this.createAndAddCheckBox(adjustedX + 220, adjustedY + 65, "Has Glass Color Options", false, null);

        this.btnSet = this.createAndAddButton(adjustedX + 20, adjustedY + 150, 90, 20, GuiLangKeys.translateString(GuiLangKeys.SET_AND_CLOSE), null);
        this.btnReSet = this.createAndAddButton(adjustedX + 125, adjustedY + 150, 90, 20, GuiLangKeys.translateString(GuiLangKeys.RESET), null);
        this.btnScan = this.createAndAddCustomButton(adjustedX + 230, adjustedY + 150, 90, 20, GuiLangKeys.translateString(GuiLangKeys.SCAN));
    }

    @Override
    protected void preButtonRender(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        this.drawControlBackground(matrixStack, x, y + 15, 350, 250);
    }

    @Override
    protected void postButtonRender(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.STARTING_POSITION), x + 15, y + 20, this.textColor);
        this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.LEFT) + " " + this.config.blocksToTheLeft + " " + GuiLangKeys.translateString(GuiLangKeys.DOWN) + " " + -this.config.blocksDown, x + 15, y + 35, this.textColor);
        this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.DEPTH) + " " + this.config.blocksLong, x + 120, y + 20, this.textColor);
        this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.WIDTH)+ " " + this.config.blocksWide, x + 200, y + 20, this.textColor);
        this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.HEIGHT) + " " + this.config.blocksTall, x + 270, y + 20, this.textColor);

        this.drawString(matrixStack, GuiLangKeys.translateString(GuiLangKeys.NAME), x + 120, y + 100, this.textColor);
    }

    @Override
    public void buttonClicked(AbstractButton button) {
        this.config.structureZipName = this.txtZipName.getValue();

        this.btnScan.active = !this.config.structureZipName.trim().equals("");

        this.config.structureZipName = this.config.structureZipName.toLowerCase().trim().replace(' ', '_');

        if (button == this.btnScan && this.btnScan.active) {
            this.sendScanPacket();
            this.closeScreen();
        } else if (button == this.btnSet) {
            // Look through the list of scanners to see if it's already there, if so don't do anything.
            // Otherwise, add it to the list of scanners.
            boolean foundExistingConfig = false;

            for (StructureScannerConfig config : ClientModRegistry.structureScanners) {
                if (config.blockPos.getX() == this.config.blockPos.getX()
                        && config.blockPos.getZ() == this.config.blockPos.getZ()
                        && config.blockPos.getY() == this.config.blockPos.getY()) {
                    foundExistingConfig = true;
                    break;
                }
            }

            if (!foundExistingConfig) {
                ClientModRegistry.structureScanners.add(this.config);
            }

            // Always make sure to send the update packet when setting.
            // This way the block configuration is saved even when just typing in the structure name.
            this.sendUpdatePacket();

            this.closeScreen();
        } else if(button == this.btnReSet) {
            this.config.blocksDown = 0;
            this.config.blocksLong = 0;
            this.config.blocksTall = 0;
            this.config.blocksWide = 0;
            this.config.blocksParallel = 0;
            this.config.blocksToTheLeft = 0;
            this.txtZipName.setValue(GuiLangKeys.translateString(GuiLangKeys.STRUCTURE_NAME_HERE));
            this.config.structureZipName = GuiLangKeys.translateString(GuiLangKeys.STRUCTURE_NAME_HERE);

            this.sendUpdatePacket();
        } else {
            if (button == this.btnStartingPositionMoveLeft) {
                this.config.blocksToTheLeft = this.config.blocksToTheLeft + 1;
            }

            if (button == this.btnStartingPositionMoveRight) {
                this.config.blocksToTheLeft = this.config.blocksToTheLeft - 1;
            }

            if (button == this.btnStartingPositionMoveDown) {
                this.config.blocksDown = this.config.blocksDown - 1;
            }

            if (button == this.btnStartingPositionMoveUp) {
                this.config.blocksDown = this.config.blocksDown + 1;
            }

            if (button == this.btnWidthGrow) {
                this.config.blocksWide += 1;
            }

            if (button == this.btnWidthShrink) {
                this.config.blocksWide -= 1;
            }

            if (button == this.btnDepthGrow) {
                this.config.blocksLong += 1;
            }

            if (button == this.btnDepthShrink) {
                this.config.blocksLong -= 1;
            }

            if (button == this.btnHeightGrow) {
                this.config.blocksTall += 1;
            }

            if (button == this.btnHeightShrink) {
                this.config.blocksTall -= 1;
            }

            this.sendUpdatePacket();
        }
    }

    private void sendUpdatePacket() {
        FriendlyByteBuf messagePacket = Utils.createMessageBuffer(this.config.GetCompoundNBT());
        ClientPlayNetworking.send(ModRegistry.StructureScannerSync, messagePacket);
    }

    private void sendScanPacket() {
        FriendlyByteBuf messagePacket = Utils.createMessageBuffer(this.config.GetCompoundNBT());
        ClientPlayNetworking.send(ModRegistry.StructureScannerAction, messagePacket);
    }
}
