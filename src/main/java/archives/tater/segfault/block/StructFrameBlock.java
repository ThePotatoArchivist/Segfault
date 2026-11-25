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

import org.jspecify.annotations.Nullable;

import java.util.HashSet;

public class StructFrameBlock extends Block {
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

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
        if (!findStart(mutable, level)) return null;

        var start = mutable.immutable();

        for (var axis : Axis.VALUES) {
            mutable.move(axis.getPositive());
            var positive = level.getBlockState(mutable);
            mutable.move(axis.getNegative(), 2);
            var negative = level.getBlockState(mutable);
            mutable.move(axis.getPositive());

            var positiveBlock = positive.hasProperty(SHAPE);
            var negativeBlock = negative.hasProperty(SHAPE);

            if (positiveBlock == negativeBlock) return null;

            var foundAxis = (positiveBlock ? positive : negative).getValue(SHAPE).axis;
            if (foundAxis != axis) return null;

            if (!findVertex(mutable, level, positiveBlock ? axis.getPositive() : axis.getNegative())) return null;
        }

        var end = mutable.immutable();

        return BoundingBox.fromCorners(start, end);
    }

    private static boolean findStart(MutableBlockPos pos, BlockGetter level) {
        var shape = level.getBlockState(pos).getOptionalValue(SHAPE).orElse(null);
        if (shape == null || shape.axis == null) return false;

        return findVertex(pos, level, shape.axis.getNegative());
    }

    private static boolean findVertex(MutableBlockPos pos, BlockGetter level, Direction direction) {
        while (true) {
            pos.move(direction);

            var shape = level.getBlockState(pos).getOptionalValue(SHAPE).orElse(null);
            if (shape == null) return false;

            var axis = shape.axis;
            if (axis == null) return true;

            if (axis != direction.getAxis()) return false;
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
