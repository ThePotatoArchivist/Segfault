package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.allocation.DisplayRegion;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.debug.DebugSubscription;

import java.util.Optional;

public interface SegfaultModDebug {
    DebugSubscription<Optional<DisplayRegion>> ALLOCATION = register("allocation", new DebugSubscription<>(ByteBufCodecs.optional(DisplayRegion.STREAM_CODEC)));

    private static <T> DebugSubscription<T> register(String path, DebugSubscription<T> subscription) {
        return Registry.register(BuiltInRegistries.DEBUG_SUBSCRIPTION, Segfault.id(path), subscription);
    }

    static void init() {

    }
}
