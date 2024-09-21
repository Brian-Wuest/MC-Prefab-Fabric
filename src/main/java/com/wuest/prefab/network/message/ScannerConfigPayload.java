package com.wuest.prefab.network.message;

import com.wuest.prefab.Prefab;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ScannerConfigPayload implements CustomPacketPayload {
    private final ScannerInfo scannerInfo;

    public static final CustomPacketPayload.Type<ScannerConfigPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(
            new ResourceLocation(Prefab.MODID, "structure_scanner_config"));

    public static final StreamCodec<FriendlyByteBuf, ScannerConfigPayload> STREAM_CODEC = CustomPacketPayload.codec(
            ScannerConfigPayload::write,
            ScannerConfigPayload::new);

    public ScannerConfigPayload(ScannerInfo scannerInfo) {
        this.scannerInfo = scannerInfo;
    }

    public ScannerConfigPayload(FriendlyByteBuf friendlyByteBuf) {
        this(new ScannerInfo(friendlyByteBuf));
    }

    public void write(FriendlyByteBuf buf) {
        this.scannerInfo.write(buf);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }

    public ScannerInfo scannerInfo() {
        return this.scannerInfo;
    }
}
