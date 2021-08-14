package com.wuest.prefab.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wuest.prefab.Utils;
import com.wuest.prefab.gui.GuiTabScreen;
import com.wuest.prefab.gui.GuiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

/**
 * @author WuestMan
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
public class GuiTabTray extends ClickableWidget {
    private static final Identifier backgroundTextures = new Identifier("prefab", "textures/gui/default_background.png");
    private ArrayList<GuiTab> tabs;

    public GuiTabTray() {
        super(0, 0, 50, 35, Utils.createTextComponent("Tab Tray"));
        this.Initialize();
    }

    protected void Initialize() {
        this.tabs = new ArrayList<>();
        this.height = 35;
        this.width = 50;
    }

    public GuiTab AddTab(GuiTab tab) {
        // The first tab is always selected.
        if (this.tabs.size() == 0) {
            tab.InternalSetSelected(true);
        }

        this.tabs.add(tab);
        return tab;
    }

    public void RemoveTab(GuiTab tab) {
        this.tabs.remove(tab);
    }

    public int TabCount() {
        return this.tabs.size();
    }

    public GuiTab GetSelectedTab() {
        if (this.tabs.size() > 0) {
            for (GuiTab tab : this.tabs) {
                if (tab.getIsSelected()) {
                    return tab;
                }
            }
        }

        return null;
    }

    public void SetSelectedTab(GuiTab tab) {
        GuiTab firstTab = null;

        for (GuiTab guiTab : this.tabs) {
            if (firstTab == null) {
                firstTab = guiTab;
            }

            guiTab.InternalSetSelected(false);
        }

        if (tab != null) {
            tab.InternalSetSelected(true);
        } else {
            assert firstTab != null;
            firstTab.InternalSetSelected(true);
        }
    }

    public boolean DoesTabNameExist(String name) {
        for (GuiTab tab : this.tabs) {
            if (tab.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public void DrawTabs(MinecraftClient mc, MatrixStack matrixStack, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, backgroundTextures);
        GuiUtils.drawTexture(matrixStack, this.x, this.y, 0, this.width, this.height, this.width, 35);

        for (GuiTab tab : this.tabs) {
            tab.drawTab(mc, matrixStack, mouseX, mouseY);
        }
    }

    public ArrayList<GuiTab> GetTabs() {
        return this.tabs;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}