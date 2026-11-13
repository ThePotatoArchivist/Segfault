package archives.tater.segfault.block;

import archives.tater.segfault.block.entity.DereferencerBlockEntity;
import archives.tater.segfault.registry.SegfaultModComponents;
import archives.tater.segfault.registry.SegfaultModDimensions;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

public class DereferencerBlock extends PoweredContainerEntityBlock implements Portal {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public DereferencerBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(POWERED, false)
                .setValue(ACTIVE, false));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState onState, Entity entity) {
        if (!(level instanceof ServerLevel serverLevel) || !onState.getValue(POWERED) || entity.is(ConventionalEntityTypeTags.TELEPORTING_NOT_SUPPORTED)) return;

        var portalDestination = getPortalDestination(serverLevel, entity, pos);
        if (portalDestination != null)
            entity.teleport(portalDestination);
    }

    @Override
    public @Nullable TeleportTransition getPortalDestination(ServerLevel currentLevel, Entity entity, BlockPos portalEntryPos) {
        var memory = currentLevel.getServer().getLevel(SegfaultModDimensions.MEMORY);
        if (memory == null) return null;

        if (!(currentLevel.getBlockEntity(portalEntryPos) instanceof DereferencerBlockEntity dereferencer)) return null;

        var pointer = dereferencer.getTheItem().get(SegfaultModComponents.POINTER);
        if (pointer == null) return null;

        return new TeleportTransition(memory, Vec3.atBottomCenterOf(pointer.pos()), Vec3.ZERO, 0, 0, Relative.ROTATION, TeleportTransition.DO_NOTHING);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Override
    protected BlockState updateShapeForPower(BlockState state, Level level, BlockPos pos) {
        return state.setValue(ACTIVE, isActive(state, level, pos));
    }

    private static boolean isActive(BlockState state, Level level, BlockPos pos) {
        return !level.isClientSide() && state.getValue(POWERED) && level.getBlockEntity(pos) instanceof DereferencerBlockEntity dereferencer && !dereferencer.getTheItem().isEmpty();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new DereferencerBlockEntity(worldPosition, blockState);
    }
}
