package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;

public interface SegfaultModEnvironment {

    EnvironmentAttribute<Boolean> CAN_HAVE_WEATHER = register("gameplay/can_have_weather", EnvironmentAttribute.builder(AttributeTypes.BOOLEAN)
            .defaultValue(true)
            .notPositional()
            .syncable()
    );

    private static <T> EnvironmentAttribute<T> register(String path, EnvironmentAttribute.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, Segfault.id(path), builder.build());
    }

    static void init() {

    }
}
