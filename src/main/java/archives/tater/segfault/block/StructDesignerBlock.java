package archives.tater.segfault.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

public class StructDesignerBlock extends Block {
    public StructDesignerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return null;
    }
}
