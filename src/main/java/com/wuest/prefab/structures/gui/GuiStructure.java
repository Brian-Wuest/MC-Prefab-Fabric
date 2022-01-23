package com.wuest.prefab.structures.gui;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.Utils;
import com.wuest.prefab.gui.GuiBase;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.structures.base.Structure;
import com.wuest.prefab.structures.config.StructureConfiguration;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Generic GUI for all structures.
 *
 * @author WuestMan
 */
public abstract class GuiStructure extends GuiBase {
    public BlockPos pos;
    protected PlayerEntity player;
    protected PressableWidget btnCancel;
    protected PressableWidget btnBuild;
    protected PressableWidget btnVisualize;
    public StructureTagMessage.EnumStructureConfiguration structureConfiguration;
    protected Identifier structureImageLocation;
    private Direction structureFacing;

    public GuiStructure(String title) {
        super(title);
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
        super.Initialize();
    }

    protected void InitializeStandardButtons() {
        // Get the upper left hand corner of the GUI box.
        Tuple<Integer, Integer> adjustedXYValue = this.getAdjustedXYValue();
        int grayBoxX = adjustedXYValue.getFirst();
        int grayBoxY = adjustedXYValue.getSecond();

        // Create the buttons.
        this.btnVisualize = this.createAndAddCustomButton(grayBoxX + 113, grayBoxY + 167, 90, 20, GuiLangKeys.GUI_BUTTON_PREVIEW);
        this.btnBuild = this.createAndAddCustomButton(grayBoxX + 215, grayBoxY + 167, 90, 20, GuiLangKeys.GUI_BUTTON_BUILD);
        this.btnCancel = this.createAndAddButton(grayBoxX + 10, grayBoxY + 167, 90, 20, GuiLangKeys.GUI_BUTTON_CANCEL);
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
        this.drawStandardControlBoxAndImage(matrixStack, this.structureImageLocation, x, y, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void postButtonRender(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
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

    protected void performPreview(Structure structure, StructureConfiguration structureConfiguration) {
        StructureRenderHandler.setStructure(structure, Direction.NORTH, structureConfiguration);
        this.closeScreen();
    }
}
