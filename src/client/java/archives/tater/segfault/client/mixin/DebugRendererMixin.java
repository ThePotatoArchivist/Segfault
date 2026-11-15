package archives.tater.segfault.client.mixin;

import archives.tater.segfault.client.SegfaultClient;
import archives.tater.segfault.client.render.debug.ChunkGridDebugRenderer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;

import java.util.List;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Shadow
    @Final
    private List<DebugRenderer.SimpleDebugRenderer> renderers;

    @Inject(
            method = "refreshRendererList",
            at = @At("TAIL")
    )
    private void addChunkGridRenderer(CallbackInfo ci) {
        if (Minecraft.getInstance().debugEntries.isCurrentlyEnabled(SegfaultClient.DEBUG_CHUNK_GRID))
            renderers.add(new ChunkGridDebugRenderer());
    }
}
