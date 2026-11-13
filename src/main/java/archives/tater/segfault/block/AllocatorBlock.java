package archives.tater.segfault.block;

import archives.tater.segfault.block.entity.AllocatorBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jspecify.annotations.Nullable;

public class AllocatorBlock extends BaseEntityBlock {
    public AllocatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;
        if (!(serverLevel.getBlockEntity(pos) instanceof AllocatorBlockEntity allocator)) return InteractionResult.SUCCESS;
        player.openMenu(allocator);
        // award stat
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new AllocatorBlockEntity(worldPosition, blockState);
    }
}
