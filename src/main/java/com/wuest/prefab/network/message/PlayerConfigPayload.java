package com.wuest.prefab.network.message;

import com.wuest.prefab.Prefab;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PlayerConfigPayload implements CustomPacketPayload {
    private final TagMessage tagMessage;

    public static final Type<PlayerConfigPayload> PACKET_TYPE = new Type<>(
            new ResourceLocation(Prefab.MODID, "payer_config_payload"));

    public static final StreamCodec<FriendlyByteBuf, PlayerConfigPayload> STREAM_CODEC = CustomPacketPayload.codec(
            PlayerConfigPayload::write,
            PlayerConfigPayload::new);

    public PlayerConfigPayload(TagMessage tagMessage) {
        this.tagMessage = tagMessage;
    }

    public PlayerConfigPayload(FriendlyByteBuf friendlyByteBuf) {
        this(new TagMessage(friendlyByteBuf));
    }

    public void write(FriendlyByteBuf buf) {
        this.tagMessage.write(buf);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }

    public TagMessage tagMessage() {
        return this.tagMessage;
    }
}
