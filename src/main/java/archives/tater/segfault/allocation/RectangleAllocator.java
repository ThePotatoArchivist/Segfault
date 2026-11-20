package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.VisibleForDebug;

import org.jspecify.annotations.Nullable;

import java.util.ListIterator;
import java.util.Optional;

import static archives.tater.segfault.allocation.GridNode.*;
import static archives.tater.segfault.allocation.GridRegion.region;

/// Based on the [Guillotiere](https://github.com/nical/guillotiere) algorithm
public final class RectangleAllocator {

    public static final Codec<RectangleAllocator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GridNode.CODEC.optionalFieldOf("allocations").forGetter(o -> Optional.ofNullable(o.allocations))
    ).apply(instance, allocations -> new RectangleAllocator(allocations.orElse(null))));

    private @Nullable GridNode allocations;

    public RectangleAllocator() {
        allocations = null;

        // TODO remove
        var region = region(new GridPos(0, 0), 8, 8);
        allocations = split(region, false, free(region));
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
        // TODO
        throw new AssertionError("TODO");
//        if (allocations == null) throw new AssertionError("Expected allocations to be nonnull");
//        var region = allocations.region;
//        var fitsX = sizeX <= region.sizeX();
//        var fitsY = sizeY <= region.sizeY();
//        var expandVertical = fitsX == fitsY ? sizeX > sizeY : fitsY;
//        var paraSize = expandVertical ? sizeY : sizeX;
//        var perpSize = expandVertical ? sizeX : sizeY;
//        var newRegion = expandVertical
//                ? region(new GridPos(0, 0), max(region.sizeX(), sizeX), region.sizeY() + sizeY)
//                : region(new GridPos(0, 0), region.sizeX() + sizeX, max(region.sizeY(), sizeY));
//
//        var start = expandVertical ? new GridPos(0, region.endY()) : new GridPos(region.endX(), 0);
//        var end = expandVertical ? new GridPos(region.sizeX(), region.endY() + sizeY) : new GridPos(region.endX() + sizeX, region.sizeY());
//        var appendedRegion = region(start, end);
//
//        if (!fitsX && !fitsY) {
//            throw new AssertionError("TODO");
//        } else if (allocations instanceof Split split && expandVertical == split.vertical) {
//            allocations = split(newRegion, expandVertical, withAppended(split.children, perpSize == 0
//                    ? used(allocatedRegion)
//                    : split(region(start, expandVertical ? sizeX : region.sizeX(), expandVertical ? sizeY : region.sizeY()), !expandVertical,
//                            used(allocatedRegion),
//                    free()
//                    )
//            ));
//
//        } else {
//
//        }
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
        switch (node) {
            case GridNode.Leaf leaf -> { return !leaf.used && leaf.region.fits(sizeX, sizeY) ? leaf : null; }
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

    private static void simplifyRecursive(GridNode.Split node) {
        var current = node;
        while (true) {
            simplify(current);
            if (current.parent == null || current.children.size() > 1) break;

            current.parent.replace(current, free(current.region));
            current = current.parent;
        }
    }

    private static void simplify(GridNode.Split node) {
        GridPos start = null;
        int freeCount = 0;

        var iterator = node.children.listIterator();
        GridNode child = null;
        while (iterator.hasNext()) {
            child = iterator.next();
            if (child instanceof GridNode.Leaf leaf && !leaf.used) {
                if (freeCount == 0)
                    start = leaf.region.start();
                freeCount++;
            } else {
                trySimplify(iterator, freeCount, start, child.region.end());
                start = null;
                freeCount = 0;
            }
        }
        if (child != null)
            trySimplify(iterator, freeCount, start, child.region.end());
    }

    private static void trySimplify(ListIterator<GridNode> iterator, int freeCount, @Nullable GridPos start, GridPos end) {
        if (start == null || freeCount < 2) return;
        for (int i = 0; i < freeCount; i++) {
            iterator.remove();
            iterator.previous();
        }
        iterator.add(free(region(start, end)));
    }
}