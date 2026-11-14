package archives.tater.segfault;

import archives.tater.segfault.component.PointerValue;
import archives.tater.segfault.component.StructTemplate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceilDiv;

public class AllocationManager extends SavedData {

    public static final int CHUNK_SIZE = 16;

    public static final Codec<AllocationManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Allocation.CODEC.listOf().fieldOf("allocations").forGetter(o -> o.allocations)
    ).apply(instance, AllocationManager::new));

    public static final SavedDataType<AllocationManager> TYPE = new SavedDataType<>(Segfault.id("allocations"), AllocationManager::new, CODEC, DataFixTypes.LEVEL);

    private final List<Allocation> allocations = new ArrayList<>();

    private AllocationManager() {}

    private AllocationManager(List<Allocation> allocations) {
        this.allocations.addAll(allocations);
    }

    public List<Allocation> getAllocations() {
        return allocations;
    }

    public List<Allocation> getAllocationsInChunk(ChunkPos pos) {
        return allocations.stream().filter(allocation -> pos.contains(allocation.origin)).toList();
    }

    public PointerValue allocate(StructTemplate template) {
        var origin = new BlockPos(8, 0, 8);
        allocations.add(new Allocation(
                origin,
                ceilDiv(template.dimensions().getX(), CHUNK_SIZE),
                ceilDiv(template.dimensions().getZ(), CHUNK_SIZE)
        ));

        setDirty();
        // TODO
        return new PointerValue(origin);
    }

    public void destruct(PointerValue pointer) {
        if (pointer.isNull()) return;

        var pos = pointer.pos();
        allocations.removeIf(allocation -> allocation.origin.equals(pos));

        setDirty();
        // TODO
    }

    public static AllocationManager get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public record Allocation(BlockPos origin, int chunksX, int chunksZ) {
        public static final Codec<Allocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("origin").forGetter(Allocation::origin),
                ExtraCodecs.POSITIVE_INT.fieldOf("chunks_x").forGetter(Allocation::chunksX),
                ExtraCodecs.POSITIVE_INT.fieldOf("chunks_y").forGetter(Allocation::chunksZ)
        ).apply(instance, Allocation::new));

        public static final StreamCodec<ByteBuf, Allocation> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, Allocation::origin,
                ByteBufCodecs.VAR_INT, Allocation::chunksX,
                ByteBufCodecs.VAR_INT, Allocation::chunksZ,
                Allocation::new
        );
    }
}
