package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;

public class RectangleAllocator {

    public static final Codec<RectangleAllocator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GridRegion.CODEC.listOf().fieldOf("allocations").forGetter(o -> o.allocations)
    ).apply(instance, RectangleAllocator::new));

    private final List<GridRegion> allocations = new ArrayList<GridRegion>();

    private int maxX;
    private int maxZ;
    private final List<GridRegion> holes = new ArrayList<>();

    private final Map<GridPos, GridRegion> originCache = new HashMap<>();
    private final Map<GridPos, GridRegion> regionCache = new HashMap<>();

    public RectangleAllocator() {
        maxX = 1;
        maxZ = 1;
    }

    public RectangleAllocator(List<GridRegion> allocations) {
        this.allocations.addAll(allocations);
        for (var allocation : allocations)
            addToCache(allocation);

        maxX = allocations.stream().mapToInt(allocation -> allocation.end().x()).max().orElse(1);
        maxZ = allocations.stream().mapToInt(allocation -> allocation.end().z()).max().orElse(1);
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

        maxX = max(maxX, allocation.end().x() + 1);
        maxZ = max(maxZ, allocation.end().z() + 1);
        holes.removeIf(allocation::intersects);

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
        holes.add(allocation);
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