package archives.tater.segfault.client.render.debug;

import archives.tater.segfault.registry.SegfaultModDebug;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.Direction;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.Vec3;

import static archives.tater.segfault.allocation.AllocationManager.CHUNK_SIZE;

public class AllocationDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    @Override
    public void emitGizmos(double camX, double camY, double camZ, DebugValueAccess debugValues, Frustum frustum, float partialTicks) {
        debugValues.forEachChunk(SegfaultModDebug.ALLOCATION, (_, maybeAllocation) -> {
            var allocation = maybeAllocation.orElse(null);
            if (allocation == null) return;

            var begin = Vec3.atLowerCornerOf(allocation.start().getWorldPosition());
            var end = Vec3.atLowerCornerOf(allocation.end().getBlockAt(CHUNK_SIZE, 0, CHUNK_SIZE));
            Gizmos.rect(begin, end, Direction.UP, GizmoStyle.strokeAndFill(0xff00ffff, 3, 0x3f00ffff));
        });
    }
}
