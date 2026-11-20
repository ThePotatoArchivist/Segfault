package archives.tater.segfault.allocation;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.component.PointerValue;
import archives.tater.segfault.component.StructTemplate;
import archives.tater.segfault.registry.SegfaultModDimensions;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

import static java.lang.Math.ceilDiv;

public final class AllocationManager extends SavedData {

    public static final int CHUNK_SIZE = 16;

    public static final Codec<AllocationManager> CODEC = RectangleAllocator.CODEC.xmap(AllocationManager::new, o -> o.rectangleAllocator);
    public static final SavedDataType<AllocationManager> TYPE = new SavedDataType<>(Segfault.id("allocations"), AllocationManager::new, CODEC, DataFixTypes.LEVEL);

    private final RectangleAllocator rectangleAllocator;

    private AllocationManager() {
        this(new RectangleAllocator());
        rectangleAllocator.allocate(1, 1);
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

    public PointerValue construct(MinecraftServer server, StructTemplate template) {
        var level = server.getLevel(SegfaultModDimensions.MEMORY);
        if (level == null) return PointerValue.NULL;

        var allocation = rectangleAllocator.allocate(
                ceilDiv(template.dimensions().getX(), CHUNK_SIZE),
                ceilDiv(template.dimensions().getZ(), CHUNK_SIZE)
        );

        var state = Blocks.GLASS.defaultBlockState();
        var startBlock = minBlockPos(allocation.start());
        for (var pos : BlockPos.betweenClosed(startBlock, startBlock.offset(template.dimensions().getX() - 1, 0, template.dimensions().getZ() - 1)))
            level.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_SKIP_ALL_SIDEEFFECTS);

        setDirty();
        return new PointerValue(startBlock.offset(0, 1, 0));
    }

    public boolean destruct(PointerValue pointer) {
        if (pointer.isNull()) return false;

        var pos = posContaining(pointer.pos());
        if (pos.x() == 0 && pos.y() == 0) return false;
        if (!rectangleAllocator.deallocate(pos)) return false;

        setDirty();

        return true;
    }

    public static AllocationManager get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public static GridPos posContaining(BlockPos pos) {
        return new GridPos(pos.getX() / CHUNK_SIZE, pos.getZ() / CHUNK_SIZE);
    }

    public static GridPos toGridPos(ChunkPos pos) {
        return new GridPos(pos.x(), pos.z());
    }

    public static BlockPos minBlockPos(GridPos pos) {
        return new BlockPos(pos.x() * CHUNK_SIZE, 0, pos.y() * CHUNK_SIZE);
    }

    public static Vec3 toVec3(GridPos pos) {
        return new Vec3(pos.x() * CHUNK_SIZE, 0, pos.y() * CHUNK_SIZE);
    }
}
