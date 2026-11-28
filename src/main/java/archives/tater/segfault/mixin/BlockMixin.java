package archives.tater.segfault.mixin;

import archives.tater.segfault.data.PlayerPlacedBlocks;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
public class BlockMixin {
    @WrapMethod(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"
    )
    private static void preventDrop(BlockState state, Level level, BlockPos pos, Operation<Void> original) {
        if (PlayerPlacedBlocks.shouldDrop(level, pos, state))
            original.call(state, level, pos);
    }

    @WrapMethod(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V"
    )
    private static void preventDrop(BlockState state, LevelAccessor level, BlockPos pos, BlockEntity blockEntity, Operation<Void> original) {
        if (!(level instanceof Level level1) || PlayerPlacedBlocks.shouldDrop(level1, pos, state))
            original.call(state, level, pos, blockEntity);
    }

    @WrapMethod(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V"
    )
    private static void preventDrop(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity breaker, ItemStack tool, Operation<Void> original) {
        if (PlayerPlacedBlocks.shouldDrop(level, pos, state))
            original.call(state, level, pos, blockEntity, breaker, tool);
    }
}
