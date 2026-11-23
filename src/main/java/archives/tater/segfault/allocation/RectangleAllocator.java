package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.VisibleForDebug;

import org.jspecify.annotations.Nullable;

import java.util.ListIterator;
import java.util.Optional;

import static archives.tater.segfault.allocation.GridNode.*;
import static archives.tater.segfault.allocation.GridRegion.region;
import static archives.tater.segfault.util.SegfaultUtil.withAppended;

/// Based on the [Guillotiere](https://github.com/nical/guillotiere) algorithm
public final class RectangleAllocator {

    public static final Codec<RectangleAllocator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GridNode.CODEC.optionalFieldOf("allocations").forGetter(o -> Optional.ofNullable(o.allocations))
    ).apply(instance, allocations -> new RectangleAllocator(allocations.orElse(null))));

    private @Nullable GridNode allocations;

    public RectangleAllocator() {
        allocations = null;
    }

    public RectangleAllocator(@Nullable GridNode allocations) {
        this.allocations = allocations;
    }

    public @Nullable GridRegion getAllocation(GridPos pos) {
        var node = getNode(pos);
        return node == null || node.used ? null : node.region;
    }

    private GridNode.@Nullable Leaf getNode(GridPos pos) {
        GridNode node = allocations;

        nodes: while (true) switch (node) {
            case GridNode.Leaf leaf -> { return leaf; }
            case GridNode.Split split -> {
                for (var child : split.children) {
                    if (child.region.contains(pos)) {
                        node = child;
                        continue nodes;
                    }
                }
                return null;
            }
            case null, default -> { return null; }
        }
    }

    public @Nullable GridRegion getAllocationByOrigin(GridPos pos) {
        var allocation = getAllocation(pos);
        return allocation != null && allocation.start().equals(pos) ? allocation : null;
    }

    @VisibleForDebug
    public @Nullable DisplayRegion getRegionByOrigin(GridPos pos) {
        var node = getNode(pos);
        return node != null &&
                node.region.start().equals(pos) ? new DisplayRegion(node.region, !node.used) : null;
    }

    public GridRegion allocate(int sizeX, int sizeY) {
        if (allocations == null) return setRoot(sizeX, sizeY);

        var free = findFree(allocations, sizeX, sizeY);
        if (free == null)
            return expandForNew(sizeX, sizeY);

        if (free.parent == null) return setRoot(sizeX, sizeY);

        return subdivide(free, free.parent, sizeX, sizeY);
    }

    private GridRegion setRoot(int sizeX, int sizeY) {
        allocations = used(region(new GridPos(0, 0), sizeX, sizeY));
        return allocations.region;
    }

    private GridRegion expandForNew(int sizeX, int sizeY) {
        if (allocations == null) throw new AssertionError("Expected allocations to be nonnull");
        var region = allocations.region;
        var fitsX = sizeX <= region.sizeX();
        var fitsY = sizeY <= region.sizeY();
        var expandVertical = fitsX == fitsY ? sizeX > sizeY : fitsY;
        var paraNewSize = expandVertical ? sizeY : sizeX;
        var perpNewSize = expandVertical ? sizeX : sizeY;
        var perpCurSize = expandVertical ? region.sizeX() : region.sizeY();

        // case 1: fits perfectly
        if (perpNewSize == perpCurSize) {
            // A|B
            var regions = region.expand(paraNewSize, expandVertical);

            allocations = padOrExpand(allocations, regions.first(), expandVertical, used(regions.second()));

            return regions.second();
        }

        // case 2: too small
        if (perpNewSize < perpCurSize) {
            // A|BC
            var outerRegions = region.expand(paraNewSize, expandVertical);
            var innerRegions = outerRegions.second().split(perpNewSize, !expandVertical);

            allocations = padOrExpand(allocations, outerRegions.first(), expandVertical, split(
                    outerRegions.second(), !expandVertical,
                    used(innerRegions.first()),
                    free(innerRegions.second())
            ));

            return innerRegions.first();
        }

        // case 3: too large
        // AB|C
        var innerRegions = region.expand(perpNewSize - perpCurSize, !expandVertical);
        var outerRegions = innerRegions.first().expand(paraNewSize, expandVertical);

        allocations = split(outerRegions.first(), expandVertical,
                padOrExpand(allocations, innerRegions.first(), !expandVertical, free(innerRegions.second())),
                used(outerRegions.second())
        );
        return outerRegions.second();
    }

    private GridNode padOrExpand(GridNode original, GridRegion region, boolean vertical, GridNode added) {
        return original instanceof Split split && split.vertical == vertical
                ? split(region, vertical, withAppended(split.children, added))
                : split(region, vertical, original, added);
    }

    private GridRegion subdivide(GridNode.Leaf node, GridNode.Split parent, int sizeX, int sizeY) {
        var region = node.region;
        var parentVertical = parent.vertical;

        var paraSize = parentVertical ? sizeY : sizeX;
        var perpSize = parentVertical ? sizeX : sizeY;

        var remainderX = region.sizeX() - sizeX;
        var remainderY = region.sizeY() - sizeY;
        var paraRemainder = parentVertical ? remainderY : remainderX;
        var perpRemainder = parentVertical ? remainderX : remainderY;

        // case 1: Fits perfectly
        if (paraRemainder == 0 && perpRemainder == 0) {
            // just mark as used
            node.used = true;
            return region;
        }

        // case 2: Fits only in the parent's direction
        if (paraRemainder == 0) {
            // replace with a perpendicular split
            var regions = region.split(perpSize, !parentVertical);
            parent.replace(node, split(region, !parentVertical, used(regions.first()), free(regions.second())));
            return regions.first();
        }

        // case 3: Fits only perpendicular to parent's direction
        if (perpRemainder == 0) {
            // replace with two
            var regions = region.split(paraSize, parentVertical);
            parent.replace(node, used(regions.first()), free(regions.second()));
            return regions.first();
        }

        // case 4: Fits neither dimension
        // replace with AB|C
        var outerRegions = region.split(paraSize, parentVertical);
        var innerRegions = outerRegions.first().split(perpSize, !parentVertical);
        parent.replace(node, split(outerRegions.first(), !parentVertical, used(innerRegions.first()), free(innerRegions.second())), free(outerRegions.second()));
        return innerRegions.first();
    }

    private GridNode.@Nullable Leaf findFree(GridNode node, int sizeX, int sizeY) {
        if (!node.region.fits(sizeX, sizeY)) return null;
        switch (node) {
            case GridNode.Leaf leaf -> { return leaf.used ? null : leaf; }
            case GridNode.Split split -> {
                for (var child : split.children) {
                    var result = findFree(child, sizeX, sizeY);
                    if (result != null) return result;
                }
                return null;
            }
            default -> throw new IllegalStateException("Unexpected value: " + node);
        }
    }

    public boolean deallocate(GridPos pos) {
        var node = getNode(pos);
        if (node == null || !node.used) return false;

        node.used = false;
        if (node.parent != null)
            simplifyRecursive(node.parent);

        return true;
    }

    private void simplifyRecursive(GridNode.Split node) {
        var current = node;
        while (true) {
            simplify(current);
            if (current.children.size() > 1) return;
            if (current.parent == null) {
                allocations = null;
                return;
            }

            current.parent.replace(current, free(current.region));
            current = current.parent;
        }
    }

    private static void simplify(GridNode.Split node) {
        GridPos start = null;
        GridPos end = null;
        int freeCount = 0;

        var iterator = node.children.listIterator();
        while (iterator.hasNext()) {
            var child = iterator.next();
            if (child instanceof GridNode.Leaf leaf && !leaf.used) {
                if (freeCount == 0)
                    start = leaf.region.start();
                end = leaf.region.end();
                freeCount++;
            } else {
                if (freeCount >= 2) {
                    iterator.previous();
                    trySimplify(node, iterator, freeCount, start, end);
                    iterator.next();
                }
                start = null;
                end = null;
                freeCount = 0;
            }
        }
        trySimplify(node, iterator, freeCount, start, end);
    }

    private static void trySimplify(Split parent, ListIterator<GridNode> iterator, int freeCount, @Nullable GridPos start, @Nullable GridPos end) {
        if (start == null || end == null || freeCount < 2) return;
        for (int i = 0; i < freeCount; i++) {
            iterator.previous();
            iterator.remove();
        }
        var free = free(region(start, end));
        iterator.add(free);
        free.parent = parent;
    }

    public void clear() {
        allocations = null;
    }
}