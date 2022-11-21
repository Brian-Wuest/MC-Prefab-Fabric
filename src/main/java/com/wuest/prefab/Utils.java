package com.wuest.prefab;

import com.wuest.prefab.network.message.TagMessage;
import com.wuest.prefab.structures.messages.StructureTagMessage;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.StringUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

public class Utils {
    public static String[] WrapString(String value) {
        return Utils.WrapString(value, 50);
    }

    public static String[] WrapString(String value, int width) {
        String result = WordUtils.wrap(value, width);
        String[] results = result.split("\n");

        String[] returnValue = new String[results.length];

        for (int i = 0; i < results.length; i++) {
            returnValue[i] = results[i].trim();
        }

        return returnValue;
    }

    public static ArrayList<Component> WrapStringToLiterals(String value) {
        return Utils.WrapStringToLiterals(value, 50);
    }

    /**
     * This is a wrapper method to make sure that when minecraft changes the name of the StringTextComponent again it's a single place update.
     *
     * @param value The text to create the object from.
     * @return A StringTextComponent object.
     */
    public static MutableComponent createTextComponent(String value) {
        return Component.literal(value);
    }

    public static ArrayList<Component> WrapStringToLiterals(String value, int width) {
        String[] values = Utils.WrapString(value, width);
        ArrayList<Component> returnValue = new ArrayList<>();

        for (String stringValue : values) {
            returnValue.add(Utils.createTextComponent(stringValue));
        }

        return returnValue;
    }

    public static FriendlyByteBuf createMessageBuffer(CompoundTag tag) {
        TagMessage message = new TagMessage(tag);
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        TagMessage.encode(message, byteBuf);

        return byteBuf;
    }

    public static FriendlyByteBuf createStructureMessageBuffer(CompoundTag tag, StructureTagMessage.EnumStructureConfiguration structureConfiguration) {
        StructureTagMessage message = new StructureTagMessage(tag, structureConfiguration);
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        StructureTagMessage.encode(message, byteBuf);

        return byteBuf;
    }

    public static Direction getDirectionByName(String name) {
        if (!StringUtil.isNullOrEmpty(name)) {
            for (Direction direction : Direction.values()) {
                if (direction.toString().equalsIgnoreCase(name)) {
                    return direction;
                }
            }
        }

        return Direction.NORTH;
    }

    /**
     * Gets a collection of all blocks with the associated tag.
     *
     * @param resourceLocation The resource location to check.
     * @return A collection of found blocks.
     */
    public static ArrayList<Block> getBlocksWithTagLocation(ResourceLocation resourceLocation) {
        TagKey<Block> tags = TagKey.create(BuiltInRegistries.BLOCK_REGISTRY, resourceLocation);
        ArrayList<Block> blocks = new ArrayList<>();

        for (Holder<Block> blockHolder : BuiltInRegistries.BLOCK.getTagOrEmpty(tags)) {
            blocks.add(blockHolder.value());
        }

        return blocks;
    }

    /**
     * Gets a collection of all blocks with the associated tag key.
     *
     * @param tagKey The tagkey to look for.
     * @return A collection containing the blocks.
     */
    public static ArrayList<Block> getBlocksWithTagKey(TagKey<Block> tagKey) {
        ArrayList<Block> blocks = new ArrayList<>();

        for (Holder<Block> blockHolder : BuiltInRegistries.BLOCK.getTagOrEmpty(tagKey)) {
            blocks.add(blockHolder.value());
        }

        return blocks;
    }

    /**
     * Determines if a particular block has a tag.
     *
     * @param block    The block to check.
     * @param location The resource location of the tag to check for.
     * @return True if the tag was found; otherwise false.
     */
    public static boolean doesBlockHaveTag(Block block, ResourceLocation location) {
        ResourceLocation blockKey = BuiltInRegistries.BLOCK.getKey(block);
        TagKey<Block> tags = TagKey.create(Registry.BLOCK_REGISTRY, location);

        for (Holder<Block> blockHolder : BuiltInRegistries.BLOCK.getTagOrEmpty(tags)) {
            if (blockHolder.is(blockKey)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the current block state has a tag.
     *
     * @param blockState The block state to check.
     * @param location   The resource location of the tag to check for.
     * @return True if the tag exists on the block state; otherwise false.
     */
    public static boolean doesBlockStateHaveTag(BlockState blockState, ResourceLocation location) {
        for (TagKey<Block> tagKey : blockState.getTags().toList()) {
            if (tagKey.location().toString().equalsIgnoreCase(location.toString())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets a collection of all item stacks with the associated tag.
     *
     * @param resourceLocation The resource location to check.
     * @return A collection of found blocks.
     */
    public static ArrayList<ItemStack> getItemStacksWithTag(ResourceLocation resourceLocation) {
        TagKey<Item> tags = TagKey.create(Registry.ITEM_REGISTRY, resourceLocation);
        ArrayList<ItemStack> itemStacks = new ArrayList<>();

        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tags)) {
            itemStacks.add(new ItemStack(holder.value()));
        }

        return itemStacks;
    }

    /**
     * Determines if the current item has a tag.
     *
     * @param item     The item to check.
     * @param location The resource location of the tag to check for.
     * @return True if the tag exists on the item; otherwise false.
     */
    public static boolean doesItemHaveTag(Item item, ResourceLocation location) {
        ResourceLocation blockKey = BuiltInRegistries.ITEM.getKey(item);
        TagKey<Item> tags = TagKey.create(BuiltInRegistries.ITEM_REGISTRY, location);

        for (Holder<Item> blockHolder : BuiltInRegistries.ITEM.getTagOrEmpty(tags)) {
            if (blockHolder.is(blockKey)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the current item stack has a tag.
     *
     * @param itemStack The item stack to check.
     * @param location  The resource location of the tag to check for.
     * @return True if the tag exists on the item stack; otherwise false.
     */
    public static boolean doesItemStackHaveTag(ItemStack itemStack, ResourceLocation location) {
        for (TagKey<Item> tagKey : itemStack.getTags().toList()) {
            if (tagKey.location().toString().equalsIgnoreCase(location.toString())) {
                return true;
            }
        }

        return false;
    }
}
