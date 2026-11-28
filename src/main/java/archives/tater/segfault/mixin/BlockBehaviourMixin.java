package archives.tater.segfault.mixin;

import archives.tater.segfault.data.PlayerPlacedBlocks;
import archives.tater.segfault.registry.SegfaultModDimensions;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    @WrapOperation(
            method = "onExplosionHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;dropFromExplosion(Lnet/minecraft/world/level/Explosion;)Z")
    )
    private boolean preventDrop(Block instance, Explosion explosion, Operation<Boolean> original, BlockState state, ServerLevel level, BlockPos pos) {
        return (level.dimension() != SegfaultModDimensions.HEAP || PlayerPlacedBlocks.shouldDrop(level, pos, state)) && original.call(instance, explosion);
    }
}
