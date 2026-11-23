package archives.tater.segfault.allocation;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

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
            int offset = vertical ? region.startY() : region.startX();
            for (var entry : this.children) {
                children.add(entry.node.decompress(vertical
                        ? region(region.startX(), offset, region.endX(), offset + entry.size)
                        : region(offset, region.startY(), offset + entry.size, region.endY())
                ));
                offset += entry.size;
            }

            return GridNode.split(region, vertical, children);
        }

        public static Split compress(GridNode.Split split) {
            var vertical = split.vertical;

            return new Split(split.children.stream().map(node -> new Entry(
                    vertical ? node.region.sizeY() : node.region.sizeX(),
                    CompressedGridNode.compress(node)
            )).toList(), vertical);
        }

        record Entry(int size, CompressedGridNode node) {
            public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("size").forGetter(Entry::size),
                    CompressedGridNode.MAP_CODEC.forGetter(Entry::node)
            ).apply(instance, Entry::new));
        }
    }

}
