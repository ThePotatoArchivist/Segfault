package archives.tater.segfault.block.entity;

import archives.tater.segfault.registry.SegfaultModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class AllocatorBlockEntity extends BaseSingleItemContainerBlockEntity {

    public static final Component DEFAULT_NAME = Component.translatable("container.segfault.allocate");

    public AllocatorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(SegfaultModBlockEntities.ALLOCATOR, worldPosition, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

}
