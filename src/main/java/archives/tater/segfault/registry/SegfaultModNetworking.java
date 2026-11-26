package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.menu.StructDesignerMenu;
import archives.tater.segfault.network.SetStructNamePayload;
import archives.tater.segfault.network.TeleportSoundPayload;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface SegfaultModNetworking {
    CustomPacketPayload.Type<TeleportSoundPayload> TELEPORT_SOUND = register(PayloadTypeRegistry.clientboundPlay(), "teleport_sound", TeleportSoundPayload.CODEC);
    CustomPacketPayload.Type<SetStructNamePayload> SET_STRUCT_NAME = register(PayloadTypeRegistry.serverboundPlay(), "set_struct_name", SetStructNamePayload.CODEC);

    private static <T extends CustomPacketPayload, B extends FriendlyByteBuf> CustomPacketPayload.Type<T> register(PayloadTypeRegistry<B> registry, String path, StreamCodec<? super B, T> codec) {
        var type = new CustomPacketPayload.Type<T>(Segfault.id(path));
        registry.register(type, codec);
        return type;
    }

    static void init() {
        ServerPlayNetworking.registerGlobalReceiver(SET_STRUCT_NAME, (payload, context) -> {
            if (!(context.player().containerMenu instanceof StructDesignerMenu structDesignerMenu)) {
                Segfault.LOGGER.warn("Player did not have struct designer menu open");
                return;
            }

            structDesignerMenu.setStructName(payload.structName());
        });
    }
}
