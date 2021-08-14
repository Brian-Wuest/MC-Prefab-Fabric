package com.wuest.prefab.network.message;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

/**
 * This message is used to sync up server saved player information to the client.
 *
 * @author WuestMan
 */
public class PlayerEntityTagMessage extends TagMessage {
	/**
	 * Initializes a new instance of the PlayerEntityTagMessage class.
	 *
	 * @param tagMessage The message to send.
	 */
	public PlayerEntityTagMessage(NbtCompound tagMessage) {
		super(tagMessage);
	}

	public PlayerEntityTagMessage() {
		super();
	}

	public static PlayerEntityTagMessage decode(PacketByteBuf buf) {
		return TagMessage.decode(buf, PlayerEntityTagMessage.class);
	}
}