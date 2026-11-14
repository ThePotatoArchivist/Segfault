package archives.tater.segfault.client;

import archives.tater.segfault.registry.SegfaultModDebug;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static archives.tater.segfault.AllocationManager.CHUNK_SIZE;

public class AllocationDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    @Override
    public void emitGizmos(double camX, double camY, double camZ, DebugValueAccess debugValues, Frustum frustum, float partialTicks) {
        debugValues.forEachChunk(SegfaultModDebug.ALLOCATIONS, (_, allocations) -> {
            for (var allocation : allocations) {
                var begin = Vec3.atLowerCornerOf(allocation.origin());
                Gizmos.cuboid(new AABB(begin, begin.add(allocation.chunksX() * CHUNK_SIZE, 16, allocation.chunksZ() * CHUNK_SIZE)), GizmoStyle.stroke(0xffffffff));
            }
        });
    }
}
