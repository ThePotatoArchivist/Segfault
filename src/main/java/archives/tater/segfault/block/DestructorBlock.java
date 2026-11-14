package archives.tater.segfault.block;

import archives.tater.segfault.block.entity.DestructorBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

public class DestructorBlock extends TriggeredContainerEntityBlock {
    public DestructorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof DestructorBlockEntity destructor)
            destructor.tryDestruct(level.getServer());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new DestructorBlockEntity(worldPosition, blockState);
    }
}
