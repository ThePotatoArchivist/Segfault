package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;

import io.netty.buffer.ByteBuf;

public record Allocation(ChunkPos start, ChunkPos end) {

    public Allocation {
        if (end.x() < start.x() || end.z() < start.z())
            throw new IllegalArgumentException("End of allocation may not be less than start");
    }

    public Allocation(ChunkPos start, int chunksX, int chunksZ) {
        this(start, new ChunkPos(start.x() + chunksX - 1, start.z() + chunksZ - 1));
    }

    public static final Codec<Allocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChunkPos.CODEC.fieldOf("start").forGetter(Allocation::start),
            ChunkPos.CODEC.fieldOf("end").forGetter(Allocation::end)
    ).apply(instance, Allocation::new));

    public static final StreamCodec<ByteBuf, Allocation> STREAM_CODEC = StreamCodec.composite(
            ChunkPos.STREAM_CODEC, Allocation::start,
            ChunkPos.STREAM_CODEC, Allocation::end,
            Allocation::new
    );
}
