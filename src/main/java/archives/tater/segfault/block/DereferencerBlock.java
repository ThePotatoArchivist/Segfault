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
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

public class DereferencerBlock extends PoweredContainerEntityBlock implements Portal {
    public DereferencerBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(POWERED, false));
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
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new DereferencerBlockEntity(worldPosition, blockState);
    }
}
