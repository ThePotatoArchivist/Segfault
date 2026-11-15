package archives.tater.segfault.allocation;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.component.PointerValue;
import archives.tater.segfault.component.StructTemplate;
import archives.tater.segfault.registry.SegfaultModDimensions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.ceilDiv;

public class AllocationManager extends SavedData {

    public static final int CHUNK_SIZE = 16;

    public static final Codec<AllocationManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Allocation.CODEC.listOf().fieldOf("allocations").forGetter(o -> o.allocations)
    ).apply(instance, AllocationManager::new));

    public static final SavedDataType<AllocationManager> TYPE = new SavedDataType<>(Segfault.id("allocations"), AllocationManager::new, CODEC, DataFixTypes.LEVEL);

    private final List<Allocation> allocations = new ArrayList<>();

    private final Map<ChunkPos, Allocation> originCache = new HashMap<>();
    private final Map<ChunkPos, Allocation> regionCache = new HashMap<>();

    private AllocationManager() {}

    private AllocationManager(List<Allocation> allocations) {
        this.allocations.addAll(allocations);
        for (var allocation : allocations)
            addToCache(allocation);
    }

    public List<Allocation> getAllocations() {
        return allocations;
    }

    public @Nullable Allocation get(ChunkPos pos) {
        return originCache.get(pos);
    }

    public PointerValue allocate(MinecraftServer server, StructTemplate template) {
        var level = server.getLevel(SegfaultModDimensions.MEMORY);
        if (level == null) return PointerValue.NULL;

        var sizeX = ceilDiv(template.dimensions().getX(), CHUNK_SIZE);
        var sizeZ = ceilDiv(template.dimensions().getZ(), CHUNK_SIZE);

        var origin = new ChunkPos(1, 0);

        add(new Allocation(origin, sizeX, sizeZ));

        var state = Blocks.SMOOTH_STONE.defaultBlockState();
        for (var pos : BlockPos.betweenClosed(origin.getWorldPosition(), origin.getBlockAt(template.dimensions().getX() - 1, 0, template.dimensions().getZ() - 1)))
            level.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_SKIP_ALL_SIDEEFFECTS);

        setDirty();
        return new PointerValue(origin.getBlockAt(0, 1, 0));
    }

    public void destruct(PointerValue pointer) {
        if (pointer.isNull()) return;

        var allocation = regionCache.get(ChunkPos.containing(pointer.pos()));
        if (allocation == null) return;
        remove(allocation);

        setDirty();
    }

    private void add(Allocation allocation) {
        allocations.add(allocation);
        addToCache(allocation);
    }

    private void remove(Allocation allocation) {
        allocations.remove(allocation);
        removeFromCache(allocation);
    }

    private void addToCache(Allocation allocation) {
        originCache.put(allocation.start(), allocation);
        ChunkPos.rangeClosed(allocation.start(), allocation.end()).forEach(pos -> regionCache.put(pos, allocation));
    }

    private void removeFromCache(Allocation allocation) {
        originCache.remove(allocation.start(), allocation);
        ChunkPos.rangeClosed(allocation.start(), allocation.end()).forEach(pos -> regionCache.remove(pos, allocation));
    }

    public static AllocationManager get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

}
