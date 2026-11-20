package archives.tater.segfault.client.render.debug;

import archives.tater.segfault.allocation.DisplayRegion;
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
        debugValues.forEachChunk(SegfaultModDebug.ALLOCATION, (_, maybeRegion) -> {
            if (!(maybeRegion.orElse(null) instanceof DisplayRegion(var region, var free))) return;

            var begin = toVec3(region.start());
            var end = toVec3(region.end());
            Gizmos.rect(begin, end, Direction.UP, GizmoStyle.strokeAndFill(
                    free ? 0xffff0000 : 0xff00ffff,
                    3,
                    free ? 0x3fff0000 : 0x3f00ffff
            ));
        });
    }
}
