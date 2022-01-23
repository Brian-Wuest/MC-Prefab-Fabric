package com.wuest.prefab.gui.screens;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.Utils;
import com.wuest.prefab.blocks.BlockStructureScanner;
import com.wuest.prefab.config.StructureScannerConfig;
import com.wuest.prefab.gui.GuiBase;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.gui.controls.GuiTextBox;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;

public class GuiStructureScanner extends GuiBase {
    private final BlockPos blockPos;
    private final World world;
    private StructureScannerConfig config;

    private ExtendedButton btnStartingPositionMoveLeft;
    private ExtendedButton btnStartingPositionMoveRight;
    private ExtendedButton btnStartingPositionMoveDown;
    private ExtendedButton btnStartingPositionMoveUp;
    private ExtendedButton btnWidthGrow;
    private ExtendedButton btnWidthShrink;
    private ExtendedButton btnLengthGrow;
    private ExtendedButton btnLengthShrink;
    private ExtendedButton btnHeightGrow;
    private ExtendedButton btnHeightShrink;
    private GuiTextBox txtZipName;
    private ExtendedButton btnScan;
    private ExtendedButton btnSet;

    public GuiStructureScanner(BlockPos blockPos, World world, StructureScannerConfig config) {
        super("Structure Scanner");

        this.blockPos = blockPos;
        this.world = world;
        this.config = config;
        this.config.blockPos = this.blockPos;
    }

    @Override
    protected void Initialize() {
        super.Initialize();

        this.config.direction = this.world.getBlockState(this.blockPos).get(BlockStructureScanner.FACING);

        Tuple<Integer, Integer> adjustedXYValues = this.getAdjustedXYValue();
        int adjustedX = adjustedXYValues.first;
        int adjustedY = adjustedXYValues.second;

        // Starting position.
        this.btnStartingPositionMoveDown = this.createAndAddButton(adjustedX + 33, adjustedY + 50, 25, 20, "▲", null);

        this.btnStartingPositionMoveLeft = this.createAndAddButton(adjustedX + 20, adjustedY + 75, 25, 20, "◄", null);
        this.btnStartingPositionMoveLeft.fontScale = 2.0f;
        this.btnStartingPositionMoveRight = this.createAndAddButton(adjustedX + 47, adjustedY + 75, 25, 20, "►", null);
        this.btnStartingPositionMoveRight.fontScale = 2.0f;

        this.btnStartingPositionMoveUp = this.createAndAddButton(adjustedX + 33, adjustedY + 100, 25, 20, "▼", null);

        // Length
        this.btnLengthShrink = this.createAndAddButton(adjustedX + 120, adjustedY + 30, 25, 20, "▼", null);
        this.btnLengthGrow = this.createAndAddButton(adjustedX + 147, adjustedY + 30, 25, 20, "▲", null);

        // Width
        this.btnWidthShrink = this.createAndAddButton(adjustedX + 200, adjustedY + 30, 25, 20, "▼", null);
        this.btnWidthGrow = this.createAndAddButton(adjustedX + 227, adjustedY + 30, 25, 20, "▲", null);

        // Height
        this.btnHeightShrink = this.createAndAddButton(adjustedX + 270, adjustedY + 30, 25, 20, "▼", null);
        this.btnHeightGrow = this.createAndAddButton(adjustedX + 297, adjustedY + 30, 25, 20, "▲", null);

        // Zip Text Field
        this.txtZipName = new GuiTextBox(this.getMinecraft().textRenderer, adjustedX + 120, adjustedY + 75, 150, 20, new LiteralText(""));

        if (this.config.structureZipName == null || this.config.structureZipName.trim().equals("")) {
            this.txtZipName.setText("Structure Name Here");
        } else {
            this.txtZipName.setText(this.config.structureZipName);
        }

        this.txtZipName.setMaxLength(128);
        this.txtZipName.setDrawsBackground(true);
        this.txtZipName.backgroundColor = Color.WHITE.getRGB();
        this.txtZipName.setEditableColor(Color.DARK_GRAY.getRGB());
        this.txtZipName.drawsTextShadow = false;
        this.addSelectableChild(this.txtZipName);

        this.btnSet = this.createAndAddButton(adjustedX + 25, adjustedY + 140, 90, 20, "Set And Close", null);
        this.btnScan = this.createAndAddCustomButton(adjustedX + 200, adjustedY + 140, 90, 20, "Scan");
    }

    @Override
    protected void preButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        this.drawControlBackground(matrixStack, x, y + 15, 350, 250);
    }

    @Override
    protected void postButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        this.drawString(matrixStack, "Starting Position", x + 15, y + 20, this.textColor);
        this.drawString(matrixStack, "Left: " + this.config.blocksToTheLeft + " Down: " + -this.config.blocksDown, x + 15, y + 35, this.textColor);
        this.drawString(matrixStack, "Length: " + this.config.blocksLong, x + 120, y + 20, this.textColor);
        this.drawString(matrixStack, "Width: " + this.config.blocksWide, x + 200, y + 20, this.textColor);
        this.drawString(matrixStack, "Height: " + this.config.blocksTall, x + 270, y + 20, this.textColor);

        this.drawString(matrixStack, "Name", x + 120, y + 60, this.textColor);
    }

    @Override
    public void buttonClicked(PressableWidget button, boolean rightClick) {
        this.config.structureZipName = this.txtZipName.getText();

        if (this.config.structureZipName.trim().equals("")) {
            this.config.structureZipName = "Structure Name Here";
        }

        this.config.structureZipName = this.config.structureZipName.toLowerCase().trim().replace(' ', '_');

        if (button == this.btnScan) {
            this.sendScanPacket();
            this.closeScreen();
        } else if (button == this.btnSet) {
            // Look through the list of scanners to see if it's already there, if so don't do anything.
            // Otherwise add it to the list of scanners.
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

            this.closeScreen();
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

            if (button == this.btnLengthGrow) {
                this.config.blocksLong += 1;
            }

            if (button == this.btnLengthShrink) {
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
        PacketByteBuf messagePacket = Utils.createMessageBuffer(this.config.GetCompoundNBT());
        ClientPlayNetworking.send(ModRegistry.StructureScannerSync, messagePacket);
    }

    private void sendScanPacket() {
        PacketByteBuf messagePacket = Utils.createMessageBuffer(this.config.GetCompoundNBT());
        ClientPlayNetworking.send(ModRegistry.StructureScannerAction, messagePacket);
    }
}
