package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

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
        if (!isValid(start, end))
            throw new IllegalArgumentException("Invalid region: " + start + ", " + end);
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

    public boolean isEmpty() {
        return sizeX() <= 0 || sizeZ() <= 0;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public boolean intersects(GridRegion other) {
        return this.equals(other) ||
                intersects(this.start.x(), this.end.x(), other.start.x(), other.end.x()) &&
                intersects(this.start.z(), this.end.z(), other.start.z(), other.end.z());
    }

    private static boolean intersects(int min1, int max1, int min2, int max2) {
        return max1 > min2 && max2 > min1;
    }

    private static boolean isValid(GridPos start, GridPos end) {
        return start.x() > end.x() && start.z() > end.z();
    }

    public static @Nullable GridRegion ofOrNull(GridPos start, GridPos end) {
        return isValid(start, end) ? new GridRegion(start, end) : null;
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
