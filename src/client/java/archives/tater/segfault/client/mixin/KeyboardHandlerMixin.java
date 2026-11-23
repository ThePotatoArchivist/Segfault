package archives.tater.segfault.client.mixin;

import archives.tater.segfault.client.SegfaultClient;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract void debugFeedbackComponent(Component component);

    @ModifyReturnValue(
            method = "handleDebugKeys",
            at = @At("TAIL")
    )
    private boolean toggleChunkGrid(boolean original, KeyEvent event) {
        if (SegfaultClient.keyDebugChunkGrid.matches(event) && minecraft.player != null && !this.minecraft.player.isReducedDebugInfo()) {
            var displayChunkGrid = minecraft.debugEntries.toggleStatus(SegfaultClient.DEBUG_CHUNK_GRID);
            debugFeedbackComponent(displayChunkGrid ? SegfaultClient.DEBUG_CHUNK_GRID_ON : SegfaultClient.DEBUG_CHUNK_GRID_OFF);
            return true;
        }
        return original;
    }
}
