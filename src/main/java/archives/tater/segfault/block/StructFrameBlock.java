package archives.tater.segfault.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;

import static it.unimi.dsi.fastutil.objects.Reference2ObjectMap.entry;

public class StructFrameBlock extends Block {
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    private static final Reference2ObjectMap<Axis, Pair<Axis, Axis>> AXES_GROUPINGS = Reference2ObjectMap.ofEntries(
            entry(Axis.X, Pair.of(Axis.Y, Axis.Z)),
            entry(Axis.Y, Pair.of(Axis.X, Axis.Z)),
            entry(Axis.Z, Pair.of(Axis.X, Axis.Y))
    );

    public StructFrameBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(SHAPE, Shape.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return getState(context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return getState(level, pos);
    }

    private BlockState getState(LevelReader level, BlockPos pos) {
        var axes = new HashSet<Axis>(3);
        for (var direction : Direction.values())
            if (level.getBlockState(pos.relative(direction)).is(this))
                axes.add(direction.getAxis());
        return axes.size() == 1
                ? defaultBlockState().setValue(SHAPE, Shape.of(axes.stream().findAny().orElseThrow()))
                : defaultBlockState();
    }

    public static @Nullable BoundingBox findRegion(BlockGetter level, BlockPos pos) {
        var mutable = pos.mutable();
        var box = findRegionCandidate(level, mutable);
        return box != null && regionValid(level, box, mutable) ? box : null;
    }

    private static @Nullable BoundingBox findRegionCandidate(BlockGetter level, MutableBlockPos mutable) {
        if (!findStart(mutable, level)) return null;

        var start = mutable.immutable();

        for (var axis : Axis.VALUES) {
            mutable.move(axis.getPositive());
            var posState = level.getBlockState(mutable);
            mutable.move(axis.getNegative(), 2);
            var negState = level.getBlockState(mutable);
            mutable.move(axis.getPositive());

            var posMatch = posState.hasProperty(SHAPE);
            var negMatch = negState.hasProperty(SHAPE);

            if (posMatch == negMatch) return null;

            var foundAxis = (posMatch ? posState : negState).getValue(SHAPE).axis;
            if (foundAxis != axis) return null;

            if (!findVertex(mutable, level, posMatch ? axis.getPositive() : axis.getNegative())) return null;
        }

        var end = mutable.immutable();

        return BoundingBox.fromCorners(start, end);
    }

    private static boolean findStart(MutableBlockPos pos, BlockGetter level) {
        var shape = level.getBlockState(pos).getOptionalValue(SHAPE).orElse(null);
        if (shape == Shape.NONE) return true;
        if (shape == null || shape.axis == null) return false;

        return findVertex(pos, level, shape.axis.getNegative());
    }

    private static boolean findVertex(MutableBlockPos pos, BlockGetter level, Direction direction) {
        while (true) {
            pos.move(direction);

            var shape = level.getBlockState(pos).getOptionalValue(SHAPE).orElse(null);
            if (shape == null) return false;

            if (shape == Shape.NONE) return true;

            if (shape.axis != direction.getAxis()) return false;
        }
    }

    private static boolean regionValid(BlockGetter level, BoundingBox box, MutableBlockPos mutable) {
        return verticesValid(level, box, mutable) &&
                edgeSetValid(level, mutable, box, Axis.X) &&
                edgeSetValid(level, mutable, box, Axis.Y) &&
                edgeSetValid(level, mutable, box, Axis.Z);
    }

    private static boolean verticesValid(BlockGetter level, BoundingBox box, MutableBlockPos mutable) {
        return isVertex(level, getCorner(mutable, box, false, false, false)) &&
                isVertex(level, getCorner(mutable, box, false, false, true)) &&
                isVertex(level, getCorner(mutable, box, false, true, false)) &&
                isVertex(level, getCorner(mutable, box, false, true, true)) &&
                isVertex(level, getCorner(mutable, box, true, false, false)) &&
                isVertex(level, getCorner(mutable, box, true, false, true)) &&
                isVertex(level, getCorner(mutable, box, true, true, false)) &&
                isVertex(level, getCorner(mutable, box, true, true, true));
    }

    private static boolean isVertex(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).getOptionalValue(SHAPE).orElse(null) == Shape.NONE;
    }

    private static MutableBlockPos getCorner(MutableBlockPos mutable, BoundingBox box, boolean xMax, boolean yMax, boolean zMax) {
        mutable.set(
                xMax ? box.maxX() : box.minX(),
                yMax ? box.maxY() : box.minY(),
                zMax ? box.maxZ() : box.minZ()
        );
        return mutable;
    }

    private static boolean edgeSetValid(BlockGetter level, MutableBlockPos mutable, BoundingBox box, Axis axis) {
        var span = getSpan(box, axis);
        return  edgeValid(level, getCorner(mutable, box, axis, false, false), axis, span) &&
                edgeValid(level, getCorner(mutable, box, axis, false, true), axis, span) &&
                edgeValid(level, getCorner(mutable, box, axis, true, false), axis, span) &&
                edgeValid(level, getCorner(mutable, box, axis, true, true), axis, span);
    }

    private static MutableBlockPos getCorner(MutableBlockPos mutable, BoundingBox box, Axis axis, boolean firstMax, boolean secondMax) {
        var otherAxes = AXES_GROUPINGS.get(axis);
        var first = otherAxes.first();
        var second = otherAxes.second();
        set(mutable, axis, getBound(box, axis, false));
        set(mutable, first, getBound(box, first, firstMax));
        set(mutable, second, getBound(box, second, secondMax));
        return mutable;
    }

    private static boolean edgeValid(BlockGetter level, MutableBlockPos mutable, Axis axis, int size) {
        mutable.move(axis.getNegative(), 1);
        if (level.getBlockState(mutable).hasProperty(SHAPE)) return false;
        mutable.move(axis.getPositive(), 2);

        for (int i = 0; i < size - 2; i++) {
            var shape = level.getBlockState(mutable).getOptionalValue(SHAPE).orElse(null);
            if (shape == null || shape.axis != axis) return false;
            mutable.move(axis.getPositive());
        }

        mutable.move(axis.getPositive());
        return !level.getBlockState(mutable).hasProperty(SHAPE);
    }

    private static int getSpan(BoundingBox box, Axis axis) {
        return switch (axis) {
            case X -> box.getXSpan();
            case Y -> box.getYSpan();
            case Z -> box.getZSpan();
        };
    }

    private static int getBound(BoundingBox box, Axis axis, boolean max) {
        return switch (axis) {
            case X -> max ? box.maxX() : box.minX();
            case Y -> max ? box.maxY() : box.minY();
            case Z -> max ? box.maxZ() : box.minZ();
        };
    }

    private static void set(MutableBlockPos mutable, Axis axis, int value) {
        switch (axis) {
            case X -> mutable.setX(value);
            case Y -> mutable.setY(value);
            case Z -> mutable.setZ(value);
        }
    }

    public enum Shape implements StringRepresentable {
        X("x", Axis.X),
        Y("y", Axis.Y),
        Z("z", Axis.Z),
        NONE("none", null);

        public final String name;
        public final @Nullable Axis axis;

        Shape(String name, @Nullable Axis axis) {
            this.name = name;
            this.axis = axis;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public static Shape of(@Nullable Axis axis) {
            return switch (axis) {
                case X -> X;
                case Y -> Y;
                case Z -> Z;
                case null -> NONE;
            };
        }
    }
}
