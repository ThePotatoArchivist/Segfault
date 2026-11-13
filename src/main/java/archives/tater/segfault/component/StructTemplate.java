package archives.tater.segfault.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;

// TODO
public record StructTemplate() {
    public static final Codec<StructTemplate> CODEC = MapCodec.unitCodec(new StructTemplate());
    public static final StreamCodec<ByteBuf, StructTemplate> STREAM_CODEC = StreamCodec.unit(new StructTemplate());
}
