package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jspecify.annotations.Nullable;

import java.util.List;

public final class RectangleAllocator {

    public static final Codec<RectangleAllocator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GridMap.REGIONS_CODEC.fieldOf("allocations").forGetter(o -> o.allocations)
    ).apply(instance, RectangleAllocator::new));

    private final GridNode allocations = GridNode.FREE;
    private int maxX = 0;
    private int maxZ = 0;

    public RectangleAllocator(List<GridRegion> allocations) {
        maxX = allocations.stream().mapToInt(allocation -> allocation.end().x()).max().orElse(0);
        maxZ = allocations.stream().mapToInt(allocation -> allocation.end().z()).max().orElse(0);
    }

    public @Nullable GridRegion getAllocation(GridPos pos) {
        int startX = 0;
        int startZ = 0;
        GridNode node = allocations;

        while (true) switch (node) {
            case GridNode.Free _ -> { return null; }
            case GridNode.Used(var region) -> { return region; }
            case GridNode.HorizontalSplit split -> {
                if (pos.x() < startX + split.sizeLeft)
                    node = split.left;
                else {
                    node = split.right;
                    startX += split.sizeLeft;
                }
            }
            case GridNode.VerticalSplit split -> {
                if (pos.z() < startZ + split.sizeUp)
                    node = split.up;
                else {
                    node = split.down;
                    startZ += split.sizeUp;
                }
            }
        }
    }

    public @Nullable GridRegion getAllocationByOrigin(GridPos pos) {
        var allocation = getAllocation(pos);
        return allocation != null && allocation.start().equals(pos) ? allocation : null;
    }

    public GridRegion allocate(int sizeX, int sizeZ) {

    }

    private @Nullable GridRegion find(int sizeX, int sizeZ) {
        int startX = 0;
        int startZ = 0;
        int endX = maxX;
        int endZ = maxZ;
        GridNode node = allocations;

        while (true) switch (node) {
            case GridNode.Free _ -> { return null; }
            case GridNode.Used(var region) -> { return null; }
            case GridNode.HorizontalSplit split -> {
                if (pos.x() < startX + split.sizeLeft) {
                    node = split.left;
                    endX = split.sizeLeft;
                } else {
                    node = split.right;
                    startX += split.sizeLeft;
                }
            }
            case GridNode.VerticalSplit split -> {
                if (pos.z() < startZ + split.sizeUp) {
                    node = split.up;
                    endZ = split.sizeUp;
                } else {
                    node = split.down;
                    startZ += split.sizeUp;
                }
            }
        }
    }

    public void deallocate(GridRegion allocation) {

    }
}