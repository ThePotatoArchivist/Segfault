package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;

import java.util.Iterator;

public record GridRegion(int startX, int startY, int endX, int endY) implements Iterable<GridPos> {

    public static final MapCodec<GridRegion> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("start_x").forGetter(GridRegion::startX),
            Codec.INT.fieldOf("start_y").forGetter(GridRegion::startY),
            Codec.INT.fieldOf("end_x").forGetter(GridRegion::endX),
            Codec.INT.fieldOf("end_y").forGetter(GridRegion::endY)
    ).apply(instance, GridRegion::new));

    public static final Codec<GridRegion> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<ByteBuf, GridRegion> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, GridRegion::startX,
            ByteBufCodecs.VAR_INT, GridRegion::startY,
            ByteBufCodecs.VAR_INT, GridRegion::endX,
            ByteBufCodecs.VAR_INT, GridRegion::endY,
            GridRegion::new
    );

    public GridRegion {
        if (!isValid(startX, startY, endX, endY))
            throw new IllegalArgumentException("Invalid region: (" + startX + ", " + startY + "), (" + endX + ", " + endY + ")");
    }

    public GridRegion(GridPos start, GridPos end) {
        this(start.x(), start.y(), end.x(), end.y());
    }

    public GridRegion(GridPos start, int sizeX, int sizeY) {
        this(start.x(), start.y(), start.x() + sizeX, start.y() + sizeY);
    }

    public GridPos start() {
        return new GridPos(startX, startY);
    }

    public GridPos end() {
        return new GridPos(endX, endY);
    }

    public int sizeX() {
        return endX - startX;
    }

    public int sizeY() {
        return endY - startY;
    }

    public boolean contains(GridPos pos) {
        return startX <= pos.x() && pos.x() < endX &&
                startY <= pos.y() && pos.y() < endY;
    }

    public boolean fits(int sizeX, int sizeZ) {
        return sizeX <= sizeX() && sizeZ <= sizeY();
    }

    public boolean intersects(GridRegion other) {
        return this.equals(other) ||
                intersects(this.startX, this.endX, other.startX, other.endX) &&
                intersects(this.startY, this.endY, other.startY, other.endY);
    }

    private static boolean intersects(int min1, int max1, int min2, int max2) {
        return max1 > min2 && max2 > min1;
    }

    private static boolean isValid(int startX, int startY, int endX, int endY) {
        return startX < endX && startY < endY;
    }

    @Override
    public Iterator<GridPos> iterator() {
        return new Iterator<>() {
            int x = startX;
            int y = startY;

            @Override
            public boolean hasNext() {
                return x < endX || y < endY;
            }

            @Override
            public GridPos next() {
                var next = new GridPos(x, y);
                if (++x >= endX) {
                    y++;
                    x = startX;
                }
                return next;
            }
        };
    }
}
