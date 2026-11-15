package archives.tater.segfault.client.render.debug;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public class ChunkGridDebugRenderer implements DebugRenderer.SimpleDebugRenderer {

    public static final int GRID_SIZE = 16;
    public static final int CHUNK_SIZE = 16;
    public static final double Y = -1 / 16.0;

    @Override
    public void emitGizmos(double camX, double camY, double camZ, DebugValueAccess debugValues, Frustum frustum, float partialTicks) {
        var cameraPos = ChunkPos.containing(BlockPos.containing(new Vec3(camX, camY, camZ)));
        var xStart = cameraPos.getMinBlockX();
        var zStart = cameraPos.getMinBlockZ();
        var xMin = xStart - GRID_SIZE * CHUNK_SIZE;
        var xMax = xStart + GRID_SIZE * CHUNK_SIZE;
        var zMin = zStart - GRID_SIZE * CHUNK_SIZE;
        var zMax = zStart + GRID_SIZE * CHUNK_SIZE;

        for (int i = -GRID_SIZE; i <= GRID_SIZE; i++) {
            var x = xStart + i * CHUNK_SIZE;
            var z = zStart + i * CHUNK_SIZE;
            Gizmos.line(new Vec3(x, Y, zStart), new Vec3(x, Y, zMin), 0xffffffff, 1);
            Gizmos.line(new Vec3(x, Y, zStart), new Vec3(x, Y, zMax), 0xffffffff, 1);
            Gizmos.line(new Vec3(xStart, Y, z), new Vec3(xMin, Y, z), 0xffffffff, 1);
            Gizmos.line(new Vec3(xStart, Y, z), new Vec3(xMax, Y, z), 0xffffffff, 1);
        }

        Gizmos.line(new Vec3(0, 0, 0), new Vec3(0, 256, 0), 0xff00ff00);
        Gizmos.arrow(new Vec3(0, 0, 0), new Vec3(CHUNK_SIZE, 0, 0), 0xffff0000);
        Gizmos.arrow(new Vec3(0, 0, 0), new Vec3(0, 0, CHUNK_SIZE), 0xff0000ff);
    }
}
