package com.wuest.prefab.gui.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.gui.GuiUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public class GuiItemList extends GuiListBox {
    /**
     * Initializes a new instance of the GuiListBox class.
     *
     * @param minecraft           The minecraft instance.
     * @param width               The width of the list.
     * @param height              The height of the list.
     * @param x                   The x-position of the top-left most part of the control.
     * @param y                   The y-position of the top-left most part of the control.
     * @param itemHeight          The height of each item in the list.
     * @param bufferColor         The color to use around the top and bottom to hide partially shown items.
     * @param entryChangedHandler The handler to call whenever an entry has been selected.
     */
    public GuiItemList(Minecraft minecraft, int width, int height, int x, int y, int itemHeight, int bufferColor, IEntryChanged entryChangedHandler) {
        super(minecraft, width, height, x, y, itemHeight, bufferColor, entryChangedHandler);
    }

    @Override
    protected void renderDecorations(PoseStack poseStack, int mouseX, int mouseY) {
        PoseStack stack = new PoseStack();
        stack.pushPose();

        // This fills the area above the control with the buffer color.
        GuiComponent.fill(stack, this.x0, this.y0 - this.itemHeight, this.x1, this.y0, this.bufferColor);

        // This fills the area below the control with the buffer color.
        GuiComponent.fill(stack, this.x0, this.y1, this.x1, this.y1 + this.itemHeight, this.bufferColor);

        stack.popPose();
    }

    public GuiItemList addEntry(Item item, int neededCount, int hasCount) {
        ItemEntry entry = new ItemEntry(this, this.minecraft.getItemRenderer());
        entry.setItemEntry(item).setNeededCount(neededCount).setHasCount(hasCount);

        this.addEntry(entry);
        return this;
    }

    protected void replaceItemEntries(Collection<ItemEntry> collection) {
        this.children().clear();
        this.children().addAll(collection);
    }

    public static class ItemEntry extends ListEntry {
        private final ResourceLocation checkMark = new ResourceLocation("prefab", "textures/gui/check_mark.png");
        private final ItemRenderer itemRenderer;
        private Item entryItem;
        private int neededCount;
        private int hasCount;

        public ItemEntry(GuiItemList parent, ItemRenderer itemRenderer) {
            super(parent);
            this.itemRenderer = itemRenderer;
            this.setText("");
        }

        public Item getEntryItem() {
            return this.entryItem;
        }

        public ItemEntry setItemEntry(Item value) {
            this.entryItem = value;
            return this;
        }

        public int getNeededCount() {
            return this.neededCount;
        }

        public ItemEntry setNeededCount(int value) {
            this.neededCount = value;
            return this;
        }

        public int getHasCount() {
            return this.hasCount;
        }

        public ItemEntry setHasCount(int value) {
            this.hasCount = value;
            return this;
        }

        @Override
        public void render(PoseStack poseStack, int entryIndex, int rowTop, int rowLeft, int rowWidth, int rowHeightWithoutBuffer, int mouseX, int mouseY, boolean isHovered, float partialTicks) {
            Minecraft mc = Minecraft.getInstance();
            int textColor = 16777215;

            if (this.entryItem != null) {
                GuiUtils.drawItemBackground(rowLeft + 2, rowTop);

                this.itemRenderer.renderGuiItem(new ItemStack(this.entryItem), rowLeft + 3, rowTop + 1);

                int textWidth = this.drawText(poseStack, mc.font, rowTop, rowLeft, textColor);

                Component textComponent = this.entryItem.getDescription();

                int itemWidth = mc.font.width(textComponent.getString());
                int maxTextWith = 133 - textWidth;

                if (itemWidth > maxTextWith) {
                    textComponent = new TextComponent(mc.font.plainSubstrByWidth(textComponent.getString(), maxTextWith));
                }

                mc.font.draw(poseStack, textComponent, rowLeft + textWidth + 20, rowTop + 6, textColor);
            }
        }

        private int drawText(PoseStack poseStack, Font font, int rowTop, int rowLeft, int textColor) {
            TextComponent textComponent = new TextComponent(String.valueOf(this.neededCount));

            int textRowLeft = rowLeft + 21;
            int neededWidth = font.width(textComponent.getString()) + 6;
            int totalWidth = neededWidth;
            font.draw(poseStack, textComponent, textRowLeft + 2, rowTop + 6, textColor);

            if (this.neededCount > this.hasCount) {
                // Draw red text to show that there is still items needed.
                int amountNeeded = this.neededCount - this.hasCount;
                TextComponent amountNeededText = new TextComponent(String.format("-%s", amountNeeded));
                amountNeededText.setStyle(Style.EMPTY.withColor(ChatFormatting.RED));

                font.draw(poseStack, amountNeededText, textRowLeft + neededWidth, rowTop + 6, textColor);
                totalWidth += font.width(amountNeededText.getString()) + 4;
            } else {
                GuiUtils.bindAndDrawTexture(this.checkMark, poseStack, textRowLeft + neededWidth, rowTop + 4, 0, 12, 12, 12, 12);
                totalWidth += 18;
            }

            return totalWidth;
        }
    }
}
