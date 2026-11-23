package archives.tater.segfault.block;

import archives.tater.segfault.block.entity.ConstructorBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

public class ConstructorBlock extends TriggeredContainerEntityBlock {

    public ConstructorBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TRIGGERED, false));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof ConstructorBlockEntity constructor)
            constructor.tryAllocate(level.getServer());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new ConstructorBlockEntity(worldPosition, blockState);
    }
}
