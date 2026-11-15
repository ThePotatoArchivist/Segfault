package archives.tater.segfault;

import archives.tater.segfault.component.PointerValue;
import archives.tater.segfault.component.StructTemplate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.*;

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

    public PointerValue allocate(StructTemplate template) {
        var origin = new ChunkPos(1, 0);
        add(new Allocation(
                origin,
                ceilDiv(template.dimensions().getX(), CHUNK_SIZE),
                ceilDiv(template.dimensions().getZ(), CHUNK_SIZE)
        ));

        setDirty();
        // TODO
        return new PointerValue(origin.getWorldPosition());
    }

    public void destruct(PointerValue pointer) {
        if (pointer.isNull()) return;

        var allocation = regionCache.get(ChunkPos.containing(pointer.pos()));
        if (allocation == null) return;
        remove(allocation);

        setDirty();
        // TODO
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
        originCache.put(allocation.origin, allocation);
        ChunkPos.rangeClosed(allocation.origin, allocation.end()).forEach(pos -> regionCache.put(pos, allocation));
    }

    private void removeFromCache(Allocation allocation) {
        originCache.remove(allocation.origin, allocation);
        ChunkPos.rangeClosed(allocation.origin, allocation.end()).forEach(pos -> regionCache.remove(pos, allocation));
    }

    public static AllocationManager get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public record Allocation(ChunkPos origin, int chunksX, int chunksZ) {
        public static final Codec<Allocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ChunkPos.CODEC.fieldOf("origin").forGetter(Allocation::origin),
                ExtraCodecs.POSITIVE_INT.fieldOf("chunks_x").forGetter(Allocation::chunksX),
                ExtraCodecs.POSITIVE_INT.fieldOf("chunks_y").forGetter(Allocation::chunksZ)
        ).apply(instance, Allocation::new));

        public static final StreamCodec<ByteBuf, Allocation> STREAM_CODEC = StreamCodec.composite(
                ChunkPos.STREAM_CODEC, Allocation::origin,
                ByteBufCodecs.VAR_INT, Allocation::chunksX,
                ByteBufCodecs.VAR_INT, Allocation::chunksZ,
                Allocation::new
        );

        public ChunkPos end() {
            return new ChunkPos(origin.x() + chunksX, origin.z() + chunksZ);
        }
    }
}
