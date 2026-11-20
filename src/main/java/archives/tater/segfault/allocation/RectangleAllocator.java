package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.Pair;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

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
        return node == null || !node.free ? null : node.region;
    }

    private GridNode.@Nullable Leaf getNode(GridPos pos) {
        GridNode node = allocations;

        while (true) switch (node) {
            case GridNode.Leaf leaf -> { return leaf; }
            case GridNode.Split split -> {
                for (var child : split.children) {
                    if (child.region.contains(pos)) {
                        node = child;
                        break;
                    }
                    return null;
                }
            }
            case null, default -> { return null; }
        }
    }

    public @Nullable GridRegion getAllocationByOrigin(GridPos pos) {
        var allocation = getAllocation(pos);
        return allocation != null && allocation.start().equals(pos) ? allocation : null;
    }

    public GridRegion allocate(int sizeX, int sizeY) {
        if (allocations == null) {
            allocations = new GridNode.Leaf(new GridRegion(new GridPos(0, 0), sizeX, sizeY), false);
            return allocations.region;
        }

        var free = findFree(allocations, sizeX, sizeY);
        if (free == null) {

            return;
        }

        var nodes = subdivide(free, sizeX, sizeY);
        if (free.parent == null)
            allocations = new GridNode.Split(free.region, false, nodes.first(), nodes.second());
        else
            free.parent.replace(free, nodes.first(), nodes.second());
    }

    private Pair<GridNode, GridNode> subdivide(GridNode node, int sizeX, int sizeY) {
        var remainderX = node.region.sizeX() - sizeX;
        var remainderY = node.region.sizeY() - sizeY;

        var horizontalFirst = node.parent == null || node.parent.vertical;

        var region = new GridRegion(node.region.start(), sizeX, sizeY);
        var main = horizontalFirst
                ? new GridNode.Leaf(region, false);

        var remainder = new GridNode.Leaf();

        return Pair.of(main, remainder);
    }

    private @Nullable GridNode findFree(GridNode node, int sizeX, int sizeY) {
        return switch (node) {
            case GridNode.Leaf leaf -> leaf.free && leaf.region.fits(sizeX, sizeY) ? leaf : null;
            case GridNode.Split split -> {
                for (var child : split.children) {
                    var result = findFree(child, sizeX, sizeY);
                    if (result != null) yield result;
                }
                yield null;
            }
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };
    }

    public boolean deallocate(GridPos pos) {
        var node = getNode(pos);
        if (node == null || node.free) return false;

        //

        return true;
    }
}