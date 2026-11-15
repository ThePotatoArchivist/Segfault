package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;

import java.util.Iterator;

public record GridRegion(GridPos start, GridPos end) implements Iterable<GridPos> {

    public static final Codec<GridRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GridPos.CODEC.fieldOf("start").forGetter(GridRegion::start),
            GridPos.CODEC.fieldOf("end").forGetter(GridRegion::end)
    ).apply(instance, GridRegion::new));

    public static final StreamCodec<ByteBuf, GridRegion> STREAM_CODEC = StreamCodec.composite(
            GridPos.STREAM_CODEC, GridRegion::start,
            GridPos.STREAM_CODEC, GridRegion::end,
            GridRegion::new
    );

    public GridRegion {
        if (end.x() < start.x() || end.z() < start.z())
            throw new IllegalArgumentException("End of allocation may not be less than start");
    }

    public GridRegion(GridPos start, int chunksX, int chunksZ) {
        this(start, new GridPos(start.x() + chunksX, start.z() + chunksZ));
    }

    public int sizeX() {
        return end.x() - start.x();
    }

    public int sizeZ() {
        return end.z() - start.z();
    }

    public boolean intersects(GridRegion other) {
        return this.equals(other) ||
                intersects(this.start.x(), this.end.x(), other.start.x(), other.end.x()) &&
                intersects(this.start.z(), this.end.z(), other.start.z(), other.end.z());
    }

    private static boolean intersects(int min1, int max1, int min2, int max2) {
        return max1 > min2 && max2 > min1;
    }

    @Override
    public Iterator<GridPos> iterator() {
        return new Iterator<>() {
            int x = start.x();
            int z = start.z();

            @Override
            public boolean hasNext() {
                return x + 1 < end.x() || z + 1 < end.z();
            }

            @Override
            public GridPos next() {
                x++;
                if (x >= end.z()) {
                    z++;
                    x = start.x();
                }
                return new GridPos(x, z);
            }
        };
    }
}
