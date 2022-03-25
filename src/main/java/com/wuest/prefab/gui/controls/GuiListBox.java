package com.wuest.prefab.gui.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.awt.*;

public class GuiListBox extends ObjectSelectionList<GuiListBox.ListEntry> {
    private final int rowWidth;
    private final int itemHeight;
    private final int bufferColor;
    private final IEntryChanged entryChangedHandler;

    /**
     * Initializes a new instance of the GuiListBox class.
     *
     * @param minecraft   The minecraft instance.
     * @param width       The width of the list.
     * @param height      The height of the list.
     * @param x           The x-position of the top-left most part of the control.
     * @param y           The y-position of the top-left most part of the control.
     * @param itemHeight  The height of each item in the list.
     * @param bufferColor The color to use around the top and bottom to hide partially shown items.
     */
    public GuiListBox(Minecraft minecraft, int width, int height, int x, int y, int itemHeight, int bufferColor, IEntryChanged entryChangedHandler) {
        super(minecraft, width, height, y, height, itemHeight);

        this.itemHeight = itemHeight;
        this.rowWidth = width;
        this.setRenderBackground(true);
        this.setRenderHeader(false, itemHeight);
        this.setRenderTopAndBottom(false);
        this.setRenderSelection(true);
        this.x0 = x;
        this.x1 = x + width;
        this.y0 = y;
        this.y1 = y + height;
        this.bufferColor = bufferColor;
        this.entryChangedHandler = entryChangedHandler;
    }

    @Override
    public int getRowWidth() {
        return this.rowWidth;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x0 + this.width - 2;
    }

    @Override
    protected void renderDecorations(PoseStack poseStack, int mouseX, int mouseY) {
        // This fills the area above the control with the buffer color.
        GuiComponent.fill(poseStack, this.x0, this.y0 - this.itemHeight, this.x1, this.y0, this.bufferColor);

        // This fills the area below the control with the buffer color.
        GuiComponent.fill(poseStack, this.x0, this.y1, this.x1, this.y1 + this.itemHeight, this.bufferColor);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        ListEntry original = this.getSelected();

        boolean result = super.keyPressed(i, j, k);

        ListEntry updated = this.getSelected();

        if (original != updated && this.entryChangedHandler != null) {
            this.entryChangedHandler.onEntryChanged(updated);
        }

        return result;
    }

    /**
     * Adds an entry to the list and returns the newly created entry.
     *
     * @return The newly created entry.
     */
    public ListEntry addEntry() {
        ListEntry entry = new ListEntry(this);

        this.addEntry(entry);

        return entry;
    }

    /**
     * Adds an entry to the list and returns the newly created entry.
     *
     * @param text The text value of the new entry.
     * @return The newly created entry.
     */
    public ListEntry addEntry(String text) {
        return this.addEntry().setText(text);
    }

    /**
     * Adds an entry to the list and returns the index of the entry.
     *
     * @param entry The entry to add.
     * @return The index of the new entry.
     */
    public int addEntry(ListEntry entry) {
        return super.addEntry(entry);
    }

    /**
     * This interface allows for outside entities to know when a selected entry has been changed.
     */
    @Environment(EnvType.CLIENT)
    public interface IEntryChanged {
        void onEntryChanged(ListEntry newEntry);
    }

    public static class ListEntry extends ObjectSelectionList.Entry<GuiListBox.ListEntry> {
        private final GuiListBox parent;
        private String text;
        private Object tag;

        public ListEntry(GuiListBox parent) {
            this.parent = parent;
        }

        /**
         * Gets the text data associated with this list entry.
         *
         * @return The text of the entry.
         */
        public String getText() {
            return this.text;
        }

        /**
         * Sets the text data for this list entry.
         *
         * @param value The value to set.
         * @return The updated ListEntry for chaining sets.
         */
        public ListEntry setText(String value) {
            this.text = value;

            return this;
        }

        /**
         * Gets the arbitrary object data for this list entry.
         *
         * @return The arbitrary object data.
         */
        public Object getTag() {
            return this.tag;
        }

        /**
         * Sets the arbitrary object data for this list entry.
         *
         * @param value The value to set.
         * @return The updated ListEntry for chaining sets.
         */
        public ListEntry setTag(Object value) {
            this.tag = value;

            return this;
        }

        @Override
        public Component getNarration() {
            return new TranslatableComponent(this.text);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            this.parent.setSelected(this);

            if (this.parent.entryChangedHandler != null) {
                this.parent.entryChangedHandler.onEntryChanged(this);
            }

            return true;
        }

        @Override
        public void render(PoseStack poseStack, int entryIndex, int rowTop, int rowLeft, int rowWidth, int rowHeightWithoutBuffer, int mouseX, int mouseY, boolean isHovered, float partialTicks) {
            Minecraft mc = Minecraft.getInstance();
            Component textComponent = new TranslatableComponent(this.text);
            int textWidth = mc.font.width(textComponent);
            int ellipsisWidth = mc.font.width("...");

            if (textWidth > rowWidth - 8 && textWidth > ellipsisWidth) {
                textComponent = Utils.createTextComponent(mc.font.substrByWidth(textComponent, rowWidth - 8 - ellipsisWidth).getString() + "...");
            }

            int textColor = 16777215;
            boolean selectedItem = this.parent.isSelectedItem(entryIndex);

            if (isHovered) {
                int backgroundColor = Color.LIGHT_GRAY.getRGB();
                GuiComponent.fill(poseStack, rowLeft, rowTop, rowLeft + rowWidth - 6, rowTop + rowHeightWithoutBuffer, backgroundColor);

                if (!selectedItem) {
                    textColor = Color.BLACK.getRGB();
                }
            }

            mc.font.draw(poseStack, textComponent, rowLeft + 2, rowTop + 2, textColor);
        }
    }
}