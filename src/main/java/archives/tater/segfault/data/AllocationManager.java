package archives.tater.segfault.data;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.allocation.DisplayRegion;
import archives.tater.segfault.allocation.GridPos;
import archives.tater.segfault.allocation.GridRegion;
import archives.tater.segfault.allocation.RectangleAllocator;
import archives.tater.segfault.component.Pointer;
import archives.tater.segfault.component.StructTemplate;
import archives.tater.segfault.registry.SegfaultModAttachments;
import archives.tater.segfault.registry.SegfaultModDimensions;
import archives.tater.segfault.util.PatchedMap;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

import static java.lang.Math.ceilDiv;

public final class AllocationManager extends SavedData {

    public static final int CHUNK_SIZE = 16;

    public static final Codec<AllocationManager> CODEC = RectangleAllocator.CODEC.xmap(AllocationManager::new, o -> o.rectangleAllocator);
    public static final SavedDataType<AllocationManager> TYPE = new SavedDataType<>(Segfault.id("allocations"), AllocationManager::new, CODEC, DataFixTypes.LEVEL);

    private final RectangleAllocator rectangleAllocator;

    private AllocationManager() {
        this(new RectangleAllocator());
    }

    private AllocationManager(RectangleAllocator rectangleAllocator) {
        this.rectangleAllocator = rectangleAllocator;
    }

    public @Nullable GridRegion getAllocation(GridPos pos) {
        return rectangleAllocator.getAllocation(pos);
    }

    public @Nullable GridRegion getAllocationByOrigin(GridPos pos) {
        return rectangleAllocator.getAllocationByOrigin(pos);
    }

    @VisibleForDebug
    public @Nullable DisplayRegion getRegionByOrigin(GridPos pos) {
        return rectangleAllocator.getRegionByOrigin(pos);
    }

    public Pointer construct(MinecraftServer server, StructTemplate template) {
        var level = server.getLevel(SegfaultModDimensions.HEAP);
        if (level == null) return Pointer.NULL;

        var structureTemplate = server.getStructureTemplateManager().get(template.structureId()).orElse(null);
        if (structureTemplate == null) return Pointer.NULL;

        var size = structureTemplate.getSize();
        var allocation = rectangleAllocator.allocate(
                ceilDiv(size.getX(), CHUNK_SIZE),
                ceilDiv(size.getZ(), CHUNK_SIZE)
        );

        var startPos = minBlockPos(allocation.start());
        structureTemplate.placeInWorld(level, startPos, BlockPos.ZERO, new StructurePlaceSettings(), level.getRandom(), Block.UPDATE_CLIENTS | Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
        clearContents(level, startPos, size);

        streamChunks(allocation).forEach(chunkPos ->
                level.getChunk(chunkPos.x(), chunkPos.z()).setAttached(SegfaultModAttachments.PLAYER_PLACED_BLOCKS, PatchedMap.empty())
        );

        setDirty();
        return new Pointer(startPos.offset(1, 1, 1));
    }

    private static void clearContents(ServerLevel level, BlockPos start, Vec3i size) {
        for (var pos : BlockPos.betweenClosed(start, start.offset(size)))
            if (level.getBlockEntity(pos) instanceof Clearable clearable)
                clearable.clearContent();
    }

    public boolean destruct(MinecraftServer server, Pointer pointer) {
        if (pointer.isNull()) return false;

        var level = server.getLevel(SegfaultModDimensions.HEAP);
        if (level == null) return false;

        var pos = posContaining(pointer.pos());

        var allocation = rectangleAllocator.getAllocation(pos);
        if (allocation != null)
            streamChunks(allocation).forEach(chunkPos ->
                    level.getChunk(chunkPos.x(), chunkPos.z()).removeAttached(SegfaultModAttachments.PLAYER_PLACED_BLOCKS)
            );

        if (!rectangleAllocator.deallocate(pos)) return false;

        setDirty();

        return true;
    }

    public void reset() {
        rectangleAllocator.clear();
        setDirty();
    }

    public static AllocationManager get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public static GridPos posContaining(BlockPos pos) {
        return new GridPos(pos.getX() / CHUNK_SIZE - 1, pos.getZ() / CHUNK_SIZE - 1);
    }

    public static GridPos toGridPos(ChunkPos pos) {
        return new GridPos(pos.x() - 1, pos.z() - 1);
    }

    public static BlockPos minBlockPos(GridPos pos) {
        return new BlockPos((pos.x() + 1) * CHUNK_SIZE, 0, (pos.y() + 1) * CHUNK_SIZE);
    }

    public static ChunkPos toChunkPos(GridPos pos) {
        return new ChunkPos(pos.x() + 1, pos.y() + 1);
    }

    public static Vec3 toVec3(GridPos pos) {
        return new Vec3((pos.x() + 1) * CHUNK_SIZE, 0, (pos.y() + 1) * CHUNK_SIZE);
    }

    private static Stream<ChunkPos> streamChunks(GridRegion allocation) {
        return ChunkPos.rangeClosed(toChunkPos(allocation.start()), toChunkPos(allocation.end()));
    }
}
