package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static archives.tater.segfault.util.SegfaultUtil.foldCompose;
import static java.lang.Math.max;

public class RectangleAllocator {

    public static final Codec<RectangleAllocator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GridRegion.CODEC.listOf().fieldOf("allocations").forGetter(o -> o.allocations)
    ).apply(instance, RectangleAllocator::new));

    private final List<GridRegion> allocations = new ArrayList<GridRegion>();

    private int maxX;
    private int maxZ;
    private List<GridRegion> holes;

    private final Map<GridPos, GridRegion> originCache = new HashMap<>();
    private final Map<GridPos, GridRegion> regionCache = new HashMap<>();

    public RectangleAllocator() {
        maxX = 1;
        maxZ = 1;
        holes = List.of(new GridRegion(new GridPos(0, 0), new GridPos(1, 1)));
    }

    public RectangleAllocator(List<GridRegion> allocations) {
        this.allocations.addAll(allocations);
        for (var allocation : allocations)
            addToCache(allocation);

        maxX = allocations.stream().mapToInt(allocation -> allocation.end().x()).max().orElse(1);
        maxZ = allocations.stream().mapToInt(allocation -> allocation.end().z()).max().orElse(1);

        holes = foldCompose(
                Stream.of(new GridRegion(new GridPos(0, 0), new GridPos(maxX, maxZ))),
                allocations,
                allocation -> stream -> stream.flatMap(subtractFromHole(allocation))
        ).toList();
    }

    public List<GridRegion> getAllocations() {
        return allocations;
    }

    public @Nullable GridRegion getAllocation(GridPos pos) {
        return regionCache.get(pos);
    }

    public @Nullable GridRegion getAllocationByOrigin(GridPos pos) {
        return originCache.get(pos);
    }

    public GridRegion allocate(int sizeX, int sizeZ) {
        var allocation = new GridRegion(findNext(sizeX, sizeZ), sizeX, sizeZ);

        var prevMaxX = maxX;
        var prevMaxZ = maxZ;
        maxX = max(maxX, allocation.end().x() + 1);
        maxZ = max(maxZ, allocation.end().z() + 1);

        if (prevMaxX != maxX || prevMaxZ != maxZ)
            updateHoles(stream -> stream
                    .map(expandEdgeHole(prevMaxX, maxX, prevMaxZ, maxZ))
                    .flatMap(subtractFromHole(allocation)));
        else
            updateHoles(stream -> stream.flatMap(subtractFromHole(allocation)));

        add(allocation);
        return allocation;
    }

    private GridPos findNext(int sizeX, int sizeZ) {
        for (var hole : holes)
            if (sizeX <= hole.sizeX() && sizeZ <= hole.sizeZ())
                return hole.start();

        return sizeX <= maxX && (sizeZ > maxZ || sizeX > sizeZ) ? new GridPos(0, maxZ) : new GridPos(maxX, 0);
    }

    public void deallocate(GridRegion allocation) {
        remove(allocation);
        addHole(allocation);
    }

    // hole maintenance

    private void updateHoles(UnaryOperator<Stream<GridRegion>> pipeline) {
        holes = pipeline.apply(holes.stream()).toList();
    }

    private void addHole(GridRegion hole) {
        holes.add(hole);
    }

    private static Function<GridRegion, Stream<? extends GridRegion>> subtractFromHole(GridRegion allocation) {
        return hole -> {
            if (hole.intersects(allocation)) return Stream.of(hole);

            return Stream.of(
                    new GridRegion(hole.start(), new GridPos(hole.end().x(), allocation.start().z())),
                    new GridRegion(new GridPos(hole.start().x(), allocation.end().z()), hole.end()),
                    new GridRegion(hole.start(), new GridPos(allocation.start().x(), hole.end().z())),
                    new GridRegion(new GridPos(allocation.end().x(), hole.start().z()), hole.end())
            ).filter(GridRegion::isNotEmpty);
        };
    }

    private Function<GridRegion, GridRegion> expandEdgeHole(int oldX, int newX, int oldZ, int newZ) {
        return hole -> {
            var end = expand(hole.end(), oldX, newX, oldZ, newZ);
            if (hole.end() == end) return hole;
            return new GridRegion(hole.start(), end);
        };
    }

    private GridPos expand(GridPos pos, int oldX, int newX, int oldZ, int newZ) {
        if (pos.x() == oldX) {
            if (pos.z() == oldZ)
                return new GridPos(newX, newZ);
            else
                return new GridPos(newX, pos.z());
        } else {
            if (pos.z() == oldZ)
                return new GridPos(pos.x(), newZ);
            else
                return pos;
        }
    }

    // cache updates

    private void add(GridRegion allocation) {
        allocations.add(allocation);
        addToCache(allocation);
    }

    private void remove(GridRegion allocation) {
        allocations.remove(allocation);
        removeFromCache(allocation);
    }

    private void addToCache(GridRegion allocation) {
        originCache.put(allocation.start(), allocation);
        for (GridPos pos : allocation)
            regionCache.put(pos, allocation);
    }

    private void removeFromCache(GridRegion allocation) {
        originCache.remove(allocation.start(), allocation);
        for (GridPos pos : allocation)
            regionCache.remove(pos, allocation);
    }
}