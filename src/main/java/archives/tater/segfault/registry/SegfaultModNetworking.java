package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.network.TeleportSoundPayload;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface SegfaultModNetworking {
    CustomPacketPayload.Type<TeleportSoundPayload> TELEPORT_SOUND = register(PayloadTypeRegistry.clientboundPlay(), "teleport_sound", TeleportSoundPayload.CODEC);

    private static <T extends CustomPacketPayload, B extends FriendlyByteBuf> CustomPacketPayload.Type<T> register(PayloadTypeRegistry<B> registry, String path, StreamCodec<? super B, T> codec) {
        var type = new CustomPacketPayload.Type<T>(Segfault.id(path));
        registry.register(type, codec);
        return type;
    }

    static void init() {

    }
}
