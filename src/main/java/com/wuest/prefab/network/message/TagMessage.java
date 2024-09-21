package com.wuest.prefab.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author WuestMan
 */
@SuppressWarnings("WeakerAccess")
public class TagMessage {
    protected CompoundTag tagMessage;

    public TagMessage() {
    }

    public TagMessage(FriendlyByteBuf friendlyByteBuf) {
        this.tagMessage = friendlyByteBuf.readNbt();
    }

    public TagMessage(CompoundTag tagMessage) {
        this.tagMessage = tagMessage;
    }

    public static <T extends TagMessage> T decode(FriendlyByteBuf buf, Class<T> clazz) {
        T message = null;

        try {
            message = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        assert message != null;
        message.tagMessage = buf.readNbt();
        return message;
    }

    public static <T extends TagMessage> void encode(T message, FriendlyByteBuf buf) {
        buf.writeNbt(message.tagMessage);
    }

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(this.tagMessage);
    }

    public CompoundTag getMessageTag() {
        return this.tagMessage;
    }

    public void setMessageTag(CompoundTag value) {
        this.tagMessage = value;
    }
}
