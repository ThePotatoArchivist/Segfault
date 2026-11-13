package archives.tater.segfault.block;

import archives.tater.segfault.block.entity.DereferencerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

public class DereferencerBlock extends PoweredContainerEntityBlock {
    public DereferencerBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(POWERED, false));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new DereferencerBlockEntity(worldPosition, blockState);
    }
}
