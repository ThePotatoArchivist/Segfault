package archives.tater.segfault;

import archives.tater.segfault.component.PointerValue;
import archives.tater.segfault.component.StructTemplate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class AllocationManager extends SavedData {
    public static final Codec<AllocationManager> CODEC = MapCodec.unitCodec(new AllocationManager());
    public static final SavedDataType<AllocationManager> TYPE = new SavedDataType<>(Segfault.id("allocations"), AllocationManager::new, CODEC, DataFixTypes.LEVEL);

    public PointerValue allocate(StructTemplate template) {
        setDirty();
        // TODO
        return new PointerValue(new BlockPos(8, 0, 8));
    }

    public void destruct(PointerValue pointer) {
        setDirty();
        // TODO
    }

    public static AllocationManager get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }
}
