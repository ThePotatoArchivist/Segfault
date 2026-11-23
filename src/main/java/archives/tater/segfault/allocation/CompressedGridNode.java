package archives.tater.segfault.allocation;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static archives.tater.segfault.allocation.GridRegion.region;

/// More compact representation of a [GridNode] tree for serialization
public sealed interface CompressedGridNode {
    MapCodec<CompressedGridNode> MAP_CODEC = MapCodec.recursive("CompressedGridNode", _ -> Codec.mapEither(Leaf.CODEC, Split.CODEC).xmap(
            Either::unwrap,
            node -> switch (node) {
                case Leaf leaf -> Either.left(leaf);
                case Split split -> Either.right(split);
            }
    ));

    Codec<GridNode> TREE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CompressedGridNode.MAP_CODEC.forGetter(CompressedGridNode::compress),
            ExtraCodecs.POSITIVE_INT.fieldOf("size_x").forGetter(node -> node.region.endX()),
            ExtraCodecs.POSITIVE_INT.fieldOf("size_y").forGetter(node -> node.region.endY())
    ).apply(instance, CompressedGridNode::decompress));

    GridNode decompress(GridRegion region);

    static CompressedGridNode compress(GridNode node) {
        return switch (node) {
            case GridNode.Leaf leaf -> Leaf.compress(leaf);
            case GridNode.Split split -> Split.compress(split);
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };
    }

    static GridNode decompress(CompressedGridNode root, int sizeX, int sizeY) {
        return root.decompress(region(new GridPos(0, 0), sizeX, sizeY));
    }

    record Leaf(boolean used) implements CompressedGridNode {
        public static final MapCodec<Leaf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.fieldOf("used").forGetter(Leaf::used)
        ).apply(instance, Leaf::new));

        @Override
        public GridNode.Leaf decompress(GridRegion region) {
            return GridNode.leaf(region, used);
        }

        public static Leaf compress(GridNode.Leaf leaf) {
            return new Leaf(leaf.used);
        }
    }

    record Split(List<Entry> children, boolean vertical) implements CompressedGridNode {
        public static final MapCodec<Split> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Entry.CODEC.listOf().fieldOf("children").forGetter(Split::children),
                Codec.BOOL.fieldOf("vertical").forGetter(Split::vertical)
        ).apply(instance, Split::new));

        @Override
        public GridNode decompress(GridRegion region) {
            var children = new ArrayList<GridNode>(this.children.size());
            Entry previous = null;
            for (var entry : this.children) {
                decompressEntry(children, region, previous, entry);
                previous = entry;
            }
            decompressEntry(children, region, previous, null);

            return GridNode.split(region, vertical, children);
        }

        private void decompressEntry(ArrayList<GridNode> entries, GridRegion region, @Nullable Entry previous, @Nullable Entry current) {
            if (previous != null)
                entries.add(previous.node.decompress(vertical
                        ? region(region.startX(), region.startY() + previous.start, region.endX(), current == null ? region.endY() : region.startY() + current.start)
                        : region(region.startX() + previous.start, region.startY(), current == null ? region.endX() : region.startX() + current.start, region.endY())
                ));
        }

        public static Split compress(GridNode.Split split) {
            var vertical = split.vertical;
            var start = vertical ? split.region.startY() : split.region.startX();

            return new Split(split.children.stream().map(node -> new Entry(
                    (vertical ? node.region.startY() : node.region.startX()) - start,
                    CompressedGridNode.compress(node)
            )).toList(), vertical);
        }

        record Entry(int start, CompressedGridNode node) {
            public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("start", 0).forGetter(Entry::start),
                    CompressedGridNode.MAP_CODEC.forGetter(Entry::node)
            ).apply(instance, Entry::new));
        }
    }

}
