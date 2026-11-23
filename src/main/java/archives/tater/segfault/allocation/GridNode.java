package archives.tater.segfault.allocation;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public abstract sealed class GridNode {

    public final GridRegion region;
    public @Nullable Split parent = null;

    public GridNode(GridRegion region) {
        this.region = region;
    }

    private static <T extends GridNode> Products.P1<RecordCodecBuilder.Mu<T>, GridRegion> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(GridRegion.MAP_CODEC.forGetter(o -> o.region));
    }

    public static Split split(GridRegion region, boolean vertical, GridNode... children) {
        return new Split(region, vertical, children);
    }

    public static Split split(GridRegion region, boolean vertical, List<GridNode> children) {
        return new Split(region, vertical, children);
    }

    public static Leaf free(GridRegion region) {
        return leaf(region, false);
    }

    public static Leaf used(GridRegion region) {
        return leaf(region, true);
    }

    public static Leaf leaf(GridRegion region, boolean used) {
        return new Leaf(region, used);
    }

    public static final class Leaf extends GridNode {

        public boolean used;

        private Leaf(GridRegion region, boolean used) {
            super(region);
            this.used = used;
        }
    }

    public static final class Split extends GridNode {

        public final boolean vertical;
        public List<GridNode> children = new LinkedList<>();

        private Split(GridRegion region, boolean vertical) {
            super(region);
            this.vertical = vertical;
        }

        private Split(GridRegion region, boolean vertical, List<GridNode> children) {
            this(region, vertical);
            this.children.addAll(children);
            for (var child : children)
                child.parent = this;
        }

        private Split(GridRegion region, boolean vertical, GridNode... children) {
            this(region, vertical);
            for (var child : children) {
                this.children.add(child);
                child.parent = this;
            }
        }

        public void replace(GridNode search, GridNode... with) {
            for (var iterator = children.listIterator(); iterator.hasNext();) {
                var node = iterator.next();
                if (node != search) continue;

                iterator.remove();
                for (var child : with) {
                    iterator.add(child);
                    child.parent = this;
                }

                break;
            }
        }

//        public void replaceMultiple(GridNode with, GridNode... search) {
//            var matchCount = 0;
//            for (var iterator = children.listIterator(); iterator.hasNext();) {
//                var node = iterator.next();
//                if (node != search[matchCount]) continue;
//                if (++matchCount < search.length) continue;
//
//                while (matchCount-- > 0) {
//                    iterator.remove();
//                    iterator.previous();
//                }
//                iterator.add(with);
//
//                break;
//            }
//        }

    }
}
