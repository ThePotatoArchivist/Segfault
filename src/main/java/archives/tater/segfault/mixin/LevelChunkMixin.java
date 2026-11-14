package archives.tater.segfault.mixin;

import archives.tater.segfault.AllocationManager;
import archives.tater.segfault.registry.SegfaultModDebug;
import archives.tater.segfault.registry.SegfaultModDimensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.debug.DebugValueSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.blending.BlendingData;

import org.jspecify.annotations.Nullable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess {
    public LevelChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, PalettedContainerFactory containerFactory, long inhabitedTime, LevelChunkSection @Nullable [] sections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, containerFactory, inhabitedTime, sections, blendingData);
    }

    @Inject(
            method = "registerDebugValues",
            at = @At("TAIL")
    )
    private void registerAllocationDebug(ServerLevel level, DebugValueSource.Registration registration, CallbackInfo ci) {
        if (level.dimension() != SegfaultModDimensions.MEMORY) return;
        var allocationManager = AllocationManager.get(level.getServer());
        var pos = getPos();
        registration.register(SegfaultModDebug.ALLOCATIONS, () -> allocationManager.getAllocationsInChunk(pos));
    }
}
