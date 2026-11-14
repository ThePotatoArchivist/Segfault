package archives.tater.segfault.registry;

import archives.tater.segfault.AllocationManager;
import archives.tater.segfault.Segfault;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.debug.DebugSubscription;

import java.util.List;

public interface SegfaultModDebug {
    DebugSubscription<List<AllocationManager.Allocation>> ALLOCATIONS = register("allocations", new DebugSubscription<>(AllocationManager.Allocation.STREAM_CODEC.apply(ByteBufCodecs.list())));

    private static <T> DebugSubscription<T> register(String path, DebugSubscription<T> subscription) {
        return Registry.register(BuiltInRegistries.DEBUG_SUBSCRIPTION, Segfault.id(path), subscription);
    }

    static void init() {

    }
}
