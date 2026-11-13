package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.component.PointerValue;
import archives.tater.segfault.component.StoredPointer;
import archives.tater.segfault.component.StructTemplate;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface SegfaultModComponents {
    DataComponentType<PointerValue> POINTER = register("pointer", PointerValue.CODEC, PointerValue.STREAM_CODEC);
    DataComponentType<StoredPointer> STORED_POINTER = register("stored_pointer", StoredPointer.CODEC, StoredPointer.STREAM_CODEC);
    DataComponentType<StructTemplate> STRUCT_TEMPLATE = register("struct_template", StructTemplate.CODEC, StructTemplate.STREAM_CODEC);

    private static <T> DataComponentType<T> register(String path, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return register(path, DataComponentType.<T>builder()
                .persistent(codec)
                .networkSynchronized(streamCodec));
    }

    private static <T> DataComponentType<T> register(String path, DataComponentType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Segfault.id(path), builder.build());
    }

    static void init() {

    }
}
