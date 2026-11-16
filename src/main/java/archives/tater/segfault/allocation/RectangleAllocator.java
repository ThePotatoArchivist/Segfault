package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.VisibleForDebug;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private Set<GridRegion> holes = Set.of();

    private final Map<GridPos, GridRegion> originCache = new HashMap<>();
    private final Map<GridPos, GridRegion> regionCache = new HashMap<>();

    public RectangleAllocator(List<GridRegion> allocations) {
        this.allocations.addAll(allocations);
        for (var allocation : allocations)
            addToCache(allocation);

        maxX = allocations.stream().mapToInt(allocation -> allocation.end().x()).max().orElse(1);
        maxZ = allocations.stream().mapToInt(allocation -> allocation.end().z()).max().orElse(1);

        rebuildHoles();
    }

    public List<GridRegion> getAllocations() {
        return allocations;
    }

    @VisibleForDebug
    public Set<GridRegion> getHoles() {
        return holes;
    }

    public @Nullable GridRegion getAllocation(GridPos pos) {
        return regionCache.get(pos);
    }

    public @Nullable GridRegion getAllocationByOrigin(GridPos pos) {
        return originCache.get(pos);
    }

    public GridRegion allocate(int sizeX, int sizeZ) {
        var allocation = new GridRegion(findNext(sizeX, sizeZ), sizeX, sizeZ);

        add(allocation);

        maxX = max(maxX, allocation.end().x());
        maxZ = max(maxZ, allocation.end().z());

        rebuildHoles();

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
        rebuildHoles();
    }

    // hole maintenance

    private void rebuildHoles() {
        holes = foldCompose(
                Stream.of(new GridRegion(new GridPos(0, 0), new GridPos(maxX, maxZ))),
                allocations,
                allocation -> stream -> stream.flatMap(subtractFromHole(allocation))
        ).collect(Collectors.toSet());
    }

    private static Function<GridRegion, Stream<? extends GridRegion>> subtractFromHole(GridRegion allocation) {
        return hole -> {
            if (!hole.intersects(allocation)) return Stream.of(hole);

            return Stream.of(
                    GridRegion.ofOrNull(hole.start(), new GridPos(hole.end().x(), allocation.start().z())),
                    GridRegion.ofOrNull(new GridPos(hole.start().x(), allocation.end().z()), hole.end()),
                    GridRegion.ofOrNull(hole.start(), new GridPos(allocation.start().x(), hole.end().z())),
                    GridRegion.ofOrNull(new GridPos(allocation.end().x(), hole.start().z()), hole.end())
            ).filter(Objects::nonNull);
        };
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