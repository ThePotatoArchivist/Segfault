package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;

public record GridPos(int x, int z) {
    public static final Codec<GridPos> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("x").forGetter(GridPos::x),
            Codec.INT.fieldOf("z").forGetter(GridPos::z)
    ).apply(instance, GridPos::new));

    public static final StreamCodec<ByteBuf, GridPos> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, GridPos::x,
            ByteBufCodecs.VAR_INT, GridPos::z,
            GridPos::new
    );
}
