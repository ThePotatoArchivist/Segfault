package archives.tater.segfault.block;

import archives.tater.segfault.menu.StructDesignerMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;

import org.jspecify.annotations.Nullable;

import java.util.Optional;

import static archives.tater.segfault.util.SegfaultUtil.simpleExtendedMenuProvider;

public class StructDesignerBlock extends Block {

    public static final Component CONTAINER_TITLE = Component.translatable("container.segfault.struct_designer");

    public StructDesignerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        if (!level.isClientSide())
            player.openMenu(state.getMenuProvider(level, pos));

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        var box = findFrame(level, pos);

        return simpleExtendedMenuProvider(
            (containerId, inventory, _) ->
                    new StructDesignerMenu(containerId, inventory, box, ContainerLevelAccess.create(level, pos)),
            CONTAINER_TITLE,
            Optional.ofNullable(box)
        );
    }

    private static @Nullable BoundingBox findFrame(BlockGetter level, BlockPos pos) {
        BoundingBox box = null;
        for (var direction : Direction.values()) {
            var offsetPos = pos.relative(direction);
            if (level.getBlockState(offsetPos).hasProperty(StructFrameBlock.SHAPE)) {
                if (box == null)
                    box = StructFrameHelper.findRegion(level, offsetPos);
                else if (!StructFrameHelper.isEdge(box, offsetPos))
                    return null;
            }
        }
        return box;
    }
}
