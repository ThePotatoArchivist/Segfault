package archives.tater.segfault.client.render.debug;

import archives.tater.segfault.registry.SegfaultModDebug;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.Direction;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;

import static archives.tater.segfault.allocation.AllocationManager.toVec3;

public class AllocationDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    @Override
    public void emitGizmos(double camX, double camY, double camZ, DebugValueAccess debugValues, Frustum frustum, float partialTicks) {
        debugValues.forEachChunk(SegfaultModDebug.ALLOCATION, (_, maybeAllocation) -> {
            var allocation = maybeAllocation.orElse(null);
            if (allocation == null) return;

            var begin = toVec3(allocation.start());
            var end = toVec3(allocation.end());
            Gizmos.rect(begin, end, Direction.UP, GizmoStyle.strokeAndFill(0xff00ffff, 3, 0x3f00ffff));
        });
    }
}
