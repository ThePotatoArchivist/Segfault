package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;

import io.netty.buffer.ByteBuf;

public record Allocation(ChunkPos start, ChunkPos end) {

    public static final Codec<Allocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChunkPos.CODEC.fieldOf("start").forGetter(Allocation::start),
            ChunkPos.CODEC.fieldOf("end").forGetter(Allocation::end)
    ).apply(instance, Allocation::new));

    public static final StreamCodec<ByteBuf, Allocation> STREAM_CODEC = StreamCodec.composite(
            ChunkPos.STREAM_CODEC, Allocation::start,
            ChunkPos.STREAM_CODEC, Allocation::end,
            Allocation::new
    );

    public Allocation {
        if (end.x() < start.x() || end.z() < start.z())
            throw new IllegalArgumentException("End of allocation may not be less than start");
    }

    public Allocation(ChunkPos start, int chunksX, int chunksZ) {
        this(start, new ChunkPos(start.x() + chunksX - 1, start.z() + chunksZ - 1));
    }

    public int sizeX() {
        return end.x() - start.x() + 1;
    }

    public int sizeZ() {
        return end.z() - start.z() + 1;
    }

    public boolean intersects(Allocation other) {
        return this.equals(other) ||
                intersects(this.start.x(), this.end.x(), other.start.x(), other.end.x()) &&
                intersects(this.start.z(), this.end.z(), other.start.z(), other.end.z());
    }

    private static boolean intersects(int min1, int max1, int min2, int max2) {
        return max1 > min2 && max2 > min1;
    }
}
