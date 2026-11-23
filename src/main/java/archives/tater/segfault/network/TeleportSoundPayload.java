package archives.tater.segfault.network;

import archives.tater.segfault.registry.SegfaultModNetworking;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import io.netty.buffer.ByteBuf;

public class TeleportSoundPayload implements CustomPacketPayload {
    public static final TeleportSoundPayload INSTANCE = new TeleportSoundPayload();
    public static final StreamCodec<ByteBuf, TeleportSoundPayload> CODEC = StreamCodec.unit(INSTANCE);

    private TeleportSoundPayload() {}

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return SegfaultModNetworking.TELEPORT_SOUND;
    }
}
