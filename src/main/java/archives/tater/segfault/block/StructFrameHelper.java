package archives.tater.segfault.block;

import archives.tater.segfault.block.StructFrameBlock.Shape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import org.jspecify.annotations.Nullable;

import static it.unimi.dsi.fastutil.objects.Reference2ObjectMap.entry;

public class StructFrameHelper {
    private StructFrameHelper() {}

    private static final Reference2ObjectMap<Direction.Axis, Pair<Direction.Axis, Direction.Axis>> AXES_GROUPINGS = Reference2ObjectMap.ofEntries(
            entry(Direction.Axis.X, Pair.of(Direction.Axis.Y, Direction.Axis.Z)),
            entry(Direction.Axis.Y, Pair.of(Direction.Axis.X, Direction.Axis.Z)),
            entry(Direction.Axis.Z, Pair.of(Direction.Axis.X, Direction.Axis.Y))
    );

    public static @Nullable BoundingBox findRegion(BlockGetter level, BlockPos pos) {
        var mutable = pos.mutable();
        var box = findRegionCandidate(level, mutable);
        return box != null && regionValid(level, box, mutable) ? box : null;
    }

    private static @Nullable BoundingBox findRegionCandidate(BlockGetter level, BlockPos.MutableBlockPos mutable) {
        if (!findStart(mutable, level)) return null;

        var start = mutable.immutable();

        for (var axis : Direction.Axis.VALUES) {
            mutable.move(axis.getPositive());
            var posState = level.getBlockState(mutable);
            mutable.move(axis.getNegative(), 2);
            var negState = level.getBlockState(mutable);
            mutable.move(axis.getPositive());

            var posMatch = isFrame(posState);
            var negMatch = isFrame(negState);

            if (posMatch == negMatch) return null;

            var foundAxis = (posMatch ? posState : negState).getValue(StructFrameBlock.SHAPE).axis;
            if (foundAxis != axis) return null;

            if (!findVertex(mutable, level, posMatch ? axis.getPositive() : axis.getNegative())) return null;
        }

        var end = mutable.immutable();

        return BoundingBox.fromCorners(start, end);
    }

    private static boolean findStart(BlockPos.MutableBlockPos pos, BlockGetter level) {
        var shape = getFrameShape(level, pos);
        if (shape == Shape.NONE) return true;
        if (shape == null || shape.axis == null) return false;

        return findVertex(pos, level, shape.axis.getNegative());
    }

    private static boolean findVertex(BlockPos.MutableBlockPos pos, BlockGetter level, Direction direction) {
        while (true) {
            pos.move(direction);

            var shape = getFrameShape(level, pos);
            if (shape == null) return false;

            if (shape == Shape.NONE) return true;

            if (shape.axis != direction.getAxis()) return false;
        }
    }

    public static boolean regionValid(BlockGetter level, BoundingBox box, BlockPos.MutableBlockPos mutable) {
        return verticesValid(level, box, mutable) &&
                edgeSetValid(level, mutable, box, Direction.Axis.X) &&
                edgeSetValid(level, mutable, box, Direction.Axis.Y) &&
                edgeSetValid(level, mutable, box, Direction.Axis.Z);
    }

    private static boolean verticesValid(BlockGetter level, BoundingBox box, BlockPos.MutableBlockPos mutable) {
        return isVertex(level, getCorner(mutable, box, false, false, false)) &&
                isVertex(level, getCorner(mutable, box, false, false, true)) &&
                isVertex(level, getCorner(mutable, box, false, true, false)) &&
                isVertex(level, getCorner(mutable, box, false, true, true)) &&
                isVertex(level, getCorner(mutable, box, true, false, false)) &&
                isVertex(level, getCorner(mutable, box, true, false, true)) &&
                isVertex(level, getCorner(mutable, box, true, true, false)) &&
                isVertex(level, getCorner(mutable, box, true, true, true));
    }

    private static BlockPos.MutableBlockPos getCorner(BlockPos.MutableBlockPos mutable, BoundingBox box, boolean xMax, boolean yMax, boolean zMax) {
        mutable.set(
                xMax ? box.maxX() : box.minX(),
                yMax ? box.maxY() : box.minY(),
                zMax ? box.maxZ() : box.minZ()
        );
        return mutable;
    }

    private static boolean edgeSetValid(BlockGetter level, BlockPos.MutableBlockPos mutable, BoundingBox box, Direction.Axis axis) {
        var span = getSpan(box, axis);
        return edgeValid(level, getCorner(mutable, box, axis, false, false), axis, span) &&
                edgeValid(level, getCorner(mutable, box, axis, false, true), axis, span) &&
                edgeValid(level, getCorner(mutable, box, axis, true, false), axis, span) &&
                edgeValid(level, getCorner(mutable, box, axis, true, true), axis, span);
    }

    private static BlockPos.MutableBlockPos getCorner(BlockPos.MutableBlockPos mutable, BoundingBox box, Direction.Axis axis, boolean firstMax, boolean secondMax) {
        var otherAxes = AXES_GROUPINGS.get(axis);
        var first = otherAxes.first();
        var second = otherAxes.second();
        set(mutable, axis, getBound(box, axis, false));
        set(mutable, first, getBound(box, first, firstMax));
        set(mutable, second, getBound(box, second, secondMax));
        return mutable;
    }

    private static boolean edgeValid(BlockGetter level, BlockPos.MutableBlockPos mutable, Direction.Axis axis, int size) {
        mutable.move(axis.getNegative(), 1);
        if (isFrame(level.getBlockState(mutable))) return false;
        mutable.move(axis.getPositive(), 2);

        for (int i = 0; i < size - 2; i++) {
            var shape = getFrameShape(level, mutable);
            if (shape == null || shape.axis != axis) return false;
            mutable.move(axis.getPositive());
        }

        mutable.move(axis.getPositive());
        return !isFrame(level.getBlockState(mutable));
    }

    private static boolean isVertex(BlockGetter level, BlockPos pos) {
        return getFrameShape(level, pos) == Shape.NONE;
    }

    private static @Nullable Shape getFrameShape(BlockGetter level, BlockPos mutable) {
        var state = level.getBlockState(mutable);
        return isFrame(state) ? state.getValue(StructFrameBlock.SHAPE) : null;
    }

    private static boolean isFrame(BlockState state) {
        return state.hasProperty(StructFrameBlock.SHAPE);
    }

    private static int getSpan(BoundingBox box, Direction.Axis axis) {
        return switch (axis) {
            case X -> box.getXSpan();
            case Y -> box.getYSpan();
            case Z -> box.getZSpan();
        };
    }

    private static int getBound(BoundingBox box, Direction.Axis axis, boolean max) {
        return switch (axis) {
            case X -> max ? box.maxX() : box.minX();
            case Y -> max ? box.maxY() : box.minY();
            case Z -> max ? box.maxZ() : box.minZ();
        };
    }

    private static void set(BlockPos.MutableBlockPos mutable, Direction.Axis axis, int value) {
        switch (axis) {
            case X -> mutable.setX(value);
            case Y -> mutable.setY(value);
            case Z -> mutable.setZ(value);
        }
    }
}
