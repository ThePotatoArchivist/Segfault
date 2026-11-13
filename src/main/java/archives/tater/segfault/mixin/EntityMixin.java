package archives.tater.segfault.mixin;

import archives.tater.segfault.registry.SegfaultModAttachments;
import archives.tater.segfault.registry.SegfaultModBlockTags;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings({"NonExtendableApiUsage"})
@Mixin(Entity.class)
public abstract class EntityMixin implements AttachmentTarget {
    @Shadow
    public abstract Level level();

    @Shadow
    public abstract BlockPos blockPosition();

    @Shadow
    public abstract BlockState getBlockStateOn();

    @Inject(
            method = "baseTick",
            at = @At("HEAD")
    )
    private void updateExited(CallbackInfo ci) {
        if (!level().isClientSide() && hasAttached(SegfaultModAttachments.EXITED_MEMORY) && !getBlockStateOn().is(SegfaultModBlockTags.PERSISTS_EXITED_STATE) && !level().getBlockState(blockPosition().below()).is(SegfaultModBlockTags.PERSISTS_EXITED_STATE))
            removeAttached(SegfaultModAttachments.EXITED_MEMORY);
    }
}
