package archives.tater.segfault.mixin;

import archives.tater.segfault.component.PointerValue;
import archives.tater.segfault.registry.SegfaultModDimensions;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @WrapMethod(
            method = "onBelowWorld"
    )
    private void exit(Operation<Void> original) {
        if (level().dimension() == SegfaultModDimensions.MEMORY) {
            if (!(level() instanceof ServerLevel serverLevel)) return;

            var destination = PointerValue.getExitDestination(serverLevel, this);
            if (destination != null) {
                resetFallDistance();
                teleport(destination);
                return;
            }
        }
        original.call();
    }
}
