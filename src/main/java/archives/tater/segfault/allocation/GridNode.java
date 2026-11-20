package archives.tater.segfault.allocation;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public abstract sealed class GridNode {

    @SuppressWarnings("StaticInitializerReferencesSubClass") // fixed it
    public static final Codec<GridNode> CODEC = Codec.either(Leaf.CODEC, Split.CODEC).xmap(
            Either::unwrap,
            node -> switch (node) {
                case Leaf leaf -> Either.left(leaf);
                case Split split -> Either.right(split);
                default -> throw new IllegalStateException("Unexpected value: " + node);
            }
    );

    public final GridRegion region;
    public @Nullable Split parent = null;

    public GridNode(GridRegion region) {
        this.region = region;
    }

    private static <T extends GridNode> Products.P1<RecordCodecBuilder.Mu<T>, GridRegion> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(GridRegion.MAP_CODEC.forGetter(o -> o.region));
    }

    public static final class Leaf extends GridNode {

        public static final Codec<Leaf> CODEC = RecordCodecBuilder.create(instance -> commonFields(instance).and(
                Codec.BOOL.fieldOf("free").forGetter(o -> o.free)
        ).apply(instance, Leaf::new));

        public boolean free;

        public Leaf(GridRegion region, boolean free) {
            super(region);
            this.free = free;
        }

        public Leaf(GridRegion region) {
            this(region, true);
        }
    }

    public static final class Split extends GridNode {

        public static final Codec<Split> CODEC = RecordCodecBuilder.create(instance -> commonFields(instance).and(instance.group(
                Codec.BOOL.fieldOf("vertical").forGetter(o -> o.vertical),
                Codec.lazyInitialized(() -> GridNode.CODEC).listOf().fieldOf("children").forGetter(o -> o.children)
        )).apply(instance, Split::new));

        public final boolean vertical;
        public List<GridNode> children = new LinkedList<>();

        public Split(GridRegion region, boolean vertical) {
            super(region);
            this.vertical = vertical;
        }

        public Split(GridRegion region, boolean vertical, List<GridNode> children) {
            this(region, vertical);
            this.children.addAll(children);
            for (var child : children)
                child.parent = this;
        }

        public Split(GridRegion region, boolean vertical, GridNode... children) {
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
