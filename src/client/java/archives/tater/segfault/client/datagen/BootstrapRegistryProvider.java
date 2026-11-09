package archives.tater.segfault.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Collection;

public class BootstrapRegistryProvider {
    private BootstrapRegistryProvider() {}

    public static FabricDataGenerator.Pack.RegistryDependentFactory<FabricDynamicRegistryProvider> createFactory(Collection<ResourceKey<? extends Registry<?>>> registryKeys) {
        return (output, registriesFuture) -> new FabricDynamicRegistryProvider(output, registriesFuture) {
            @Override
            protected void configure(HolderLookup.Provider registries, Entries entries) {
                for (var key : registryKeys)
                    entries.addAll(registries.lookupOrThrow(key));
            }

            @Override
            public String getName() {
                return "Dynamic Registries";
            }
        };
    }
}
