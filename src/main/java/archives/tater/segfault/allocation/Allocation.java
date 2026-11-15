package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;

import io.netty.buffer.ByteBuf;

public record Allocation(ChunkPos origin, int chunksX, int chunksZ) {
    public static final Codec<Allocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChunkPos.CODEC.fieldOf("origin").forGetter(Allocation::origin),
            ExtraCodecs.POSITIVE_INT.fieldOf("chunks_x").forGetter(Allocation::chunksX),
            ExtraCodecs.POSITIVE_INT.fieldOf("chunks_y").forGetter(Allocation::chunksZ)
    ).apply(instance, Allocation::new));

    public static final StreamCodec<ByteBuf, Allocation> STREAM_CODEC = StreamCodec.composite(
            ChunkPos.STREAM_CODEC, Allocation::origin,
            ByteBufCodecs.VAR_INT, Allocation::chunksX,
            ByteBufCodecs.VAR_INT, Allocation::chunksZ,
            Allocation::new
    );

    public ChunkPos end() {
        return new ChunkPos(origin.x() + chunksX, origin.z() + chunksZ);
    }
}
