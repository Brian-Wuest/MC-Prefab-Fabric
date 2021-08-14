package com.wuest.prefab.network.message;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

/**
 * @author WuestMan
 */
@SuppressWarnings("WeakerAccess")
public class TagMessage {
	protected NbtCompound tagMessage;

	protected TagMessage() {
	}

	public TagMessage(NbtCompound tagMessage) {
		this.tagMessage = tagMessage;
	}

	public static <T extends TagMessage> T decode(PacketByteBuf buf, Class<T> clazz) {
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

	public static <T extends TagMessage> void encode(T message, PacketByteBuf buf) {
		buf.writeNbt(message.tagMessage);
	}

	public NbtCompound getMessageTag() {
		return this.tagMessage;
	}

	public void setMessageTag(NbtCompound value) {
		this.tagMessage = value;
	}
}
