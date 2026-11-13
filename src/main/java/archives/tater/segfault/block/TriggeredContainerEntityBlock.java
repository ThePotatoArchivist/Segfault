package archives.tater.segfault.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;

import org.jspecify.annotations.Nullable;

public abstract class TriggeredContainerEntityBlock extends ContainerEntityBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    private static final int TRIGGER_DURATION = 4;

    public TriggeredContainerEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        boolean shouldTrigger = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        boolean isTriggered = state.getValue(TRIGGERED);

        if (shouldTrigger && !isTriggered) {
            level.scheduleTick(pos, this, TRIGGER_DURATION);
            level.setBlock(pos, state.setValue(TRIGGERED, true), UPDATE_CLIENTS);
        } else if (!shouldTrigger && isTriggered)
            level.setBlock(pos, state.setValue(TRIGGERED, false), UPDATE_CLIENTS);
    }

    @Override
    protected abstract void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }
}
