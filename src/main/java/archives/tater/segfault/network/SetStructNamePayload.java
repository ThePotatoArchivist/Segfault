package archives.tater.segfault.network;

import archives.tater.segfault.registry.SegfaultModNetworking;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import io.netty.buffer.ByteBuf;

public record SetStructNamePayload(String structName) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, SetStructNamePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SetStructNamePayload::structName,
            SetStructNamePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return SegfaultModNetworking.SET_STRUCT_NAME;
    }
}
