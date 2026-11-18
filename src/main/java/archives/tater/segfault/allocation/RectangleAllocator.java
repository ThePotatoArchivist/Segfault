package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.VisibleForDebug;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static archives.tater.segfault.util.SegfaultUtil.foldCompose;

public final class RectangleAllocator {

    public static final Codec<RectangleAllocator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GridMap.REGIONS_CODEC.fieldOf("allocations").forGetter(o -> o.allocations)
    ).apply(instance, RectangleAllocator::new));

    private final GridMap allocations;

    private Set<GridRegion> holes = Set.of();

    public RectangleAllocator(GridMap allocations) {
        this.allocations = allocations;
        rebuildHoles();
    }

    public RectangleAllocator(List<GridRegion> allocations) {
        this(new GridMap(allocations));
    }

    public List<GridRegion> getAllocations() {
        return allocations.regions();
    }

    @VisibleForDebug
    public Set<GridRegion> getHoles() {
        return holes;
    }

    public @Nullable GridRegion getAllocation(GridPos pos) {
        return allocations.get(pos);
    }

    public @Nullable GridRegion getAllocationByOrigin(GridPos pos) {
        return allocations.getByOrigin(pos);
    }

    public GridRegion allocate(int sizeX, int sizeZ) {
        var allocation = new GridRegion(findNext(sizeX, sizeZ), sizeX, sizeZ);

        allocations.add(allocation);

        rebuildHoles();

        return allocation;
    }

    private GridPos findNext(int sizeX, int sizeZ) {
        for (var hole : holes)
            if (sizeX <= hole.sizeX() && sizeZ <= hole.sizeZ())
                return hole.start();

        return sizeX <= allocations.maxX() && (sizeZ > allocations.maxZ() || sizeX > sizeZ) ? new GridPos(0, allocations.maxZ()) : new GridPos(allocations.maxX(), 0);
    }

    public void deallocate(GridRegion allocation) {
        allocations.remove(allocation);
        rebuildHoles();
    }

    // hole maintenance

    private void rebuildHoles() {
        holes = foldCompose(
                Stream.of(new GridRegion(new GridPos(0, 0), new GridPos(allocations.maxX(), allocations.maxZ()))),
                allocations.regions(),
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
}