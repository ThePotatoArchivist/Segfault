package archives.tater.segfault.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;

import org.jspecify.annotations.Nullable;

public abstract class PoweredContainerEntityBlock extends ContainerEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public PoweredContainerEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) return;
        var signal = level.hasNeighborSignal(pos);
        if (signal != state.getValue(POWERED))
            level.setBlock(pos, updateShapeForPower(state.setValue(POWERED, signal), level, pos), UPDATE_CLIENTS);
    }

    protected BlockState updateShapeForPower(BlockState state, Level level, BlockPos pos) {
        return state;
    }
}
