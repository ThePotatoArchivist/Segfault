package archives.tater.segfault.allocation;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.component.PointerValue;
import archives.tater.segfault.component.StructTemplate;
import archives.tater.segfault.registry.SegfaultModDimensions;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import org.jspecify.annotations.Nullable;

import static java.lang.Math.ceilDiv;

public class AllocationManager extends SavedData {

    public static final int CHUNK_SIZE = 16;

    public static final Codec<AllocationManager> CODEC = RectangleAllocator.CODEC.xmap(AllocationManager::new, o -> o.rectangleAllocator);
    public static final SavedDataType<AllocationManager> TYPE = new SavedDataType<>(Segfault.id("allocations"), AllocationManager::new, CODEC, DataFixTypes.LEVEL);

    private final RectangleAllocator rectangleAllocator;

    public AllocationManager() {
        this(new RectangleAllocator());
    }

    public AllocationManager(RectangleAllocator rectangleAllocator) {
        this.rectangleAllocator = rectangleAllocator;
    }

    public @Nullable Allocation get(ChunkPos pos) {
        return rectangleAllocator.get(pos);
    }

    public @Nullable Allocation getWithOrigin(ChunkPos pos) {
        return rectangleAllocator.getWithOrigin(pos);
    }

    public PointerValue construct(MinecraftServer server, StructTemplate template) {
        var level = server.getLevel(SegfaultModDimensions.MEMORY);
        if (level == null) return PointerValue.NULL;

        var allocation = rectangleAllocator.allocate(
                ceilDiv(template.dimensions().getX(), CHUNK_SIZE),
                ceilDiv(template.dimensions().getZ(), CHUNK_SIZE)
        );

        var state = Blocks.SMOOTH_STONE.defaultBlockState();
        for (var pos : BlockPos.betweenClosed(allocation.start().getWorldPosition(), allocation.start().getBlockAt(template.dimensions().getX() - 1, 0, template.dimensions().getZ() - 1)))
            level.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_SKIP_ALL_SIDEEFFECTS);

        setDirty();
        return new PointerValue(allocation.start().getBlockAt(0, 1, 0));
    }

    public void destruct(PointerValue pointer) {
        if (pointer.isNull()) return;

        var allocation = rectangleAllocator.get(ChunkPos.containing(pointer.pos()));
        if (allocation == null) return;
        rectangleAllocator.deallocate(allocation);

        setDirty();
    }

    public static AllocationManager get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

}
