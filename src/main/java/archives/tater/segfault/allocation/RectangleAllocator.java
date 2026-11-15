package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.ChunkPos;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;

// Note: uses ChunkPos so that
public class RectangleAllocator {

    public static final Codec<RectangleAllocator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Allocation.CODEC.listOf().fieldOf("allocations").forGetter(o -> o.allocations)
    ).apply(instance, RectangleAllocator::new));

    private final List<Allocation> allocations = new ArrayList<Allocation>();
    private int maxX;
    private int maxZ;
    private final Map<ChunkPos, Allocation> originCache = new HashMap<ChunkPos, Allocation>();
    private final Map<ChunkPos, Allocation> regionCache = new HashMap<ChunkPos, Allocation>();

    public RectangleAllocator() {
        maxX = 1;
        maxZ = 1;
    }

    public RectangleAllocator(List<Allocation> allocations) {
        this.allocations.addAll(allocations);
        for (var allocation : allocations)
            addToCache(allocation);

        maxX = allocations.stream().mapToInt(allocation -> allocation.end().x()).max().orElse(1);
        maxZ = allocations.stream().mapToInt(allocation -> allocation.end().z()).max().orElse(1);
    }

    public List<Allocation> getAllocations() {
        return allocations;
    }

    public @Nullable Allocation get(ChunkPos pos) {
        return regionCache.get(pos);
    }

    public @Nullable Allocation getWithOrigin(ChunkPos pos) {
        return originCache.get(pos);
    }

    public Allocation allocate(int sizeX, int sizeZ) {
        var allocation = new Allocation(findNext(sizeX, sizeZ), sizeX, sizeZ);
        maxX = max(maxX, allocation.end().x() + 1);
        maxZ = max(maxZ, allocation.end().z() + 1);
        add(allocation);
        return allocation;
    }

    private ChunkPos findNext(int sizeX, int sizeZ) {
        return sizeX <= maxX && (sizeZ > maxZ || sizeX > sizeZ) ? new ChunkPos(0, maxZ) : new ChunkPos(maxX, 0);
    }

    public void deallocate(Allocation allocation) {
        remove(allocation);
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
}