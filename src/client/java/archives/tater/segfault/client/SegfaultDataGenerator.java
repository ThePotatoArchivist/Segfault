package archives.tater.segfault.client;

import archives.tater.segfault.client.datagen.*;
import archives.tater.segfault.registry.SegfaultModBiomes;
import archives.tater.segfault.registry.SegfaultModDimensionTypes;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import java.util.List;

public class SegfaultDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.DIMENSION_TYPE, SegfaultModDimensionTypes::bootstrap);
		registryBuilder.add(Registries.BIOME, SegfaultModBiomes::bootstrap);
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();

		pack.addProvider(ModBlockTagsProvider::new);
		pack.addProvider(ModBlockLootProvider::new);
		pack.addProvider(ModTimelineTagsProvider::new);
		pack.addProvider(ModDimensionProvider::new);
		pack.addProvider(BootstrapRegistryProvider.createFactory(List.of(
				Registries.DIMENSION_TYPE,
				Registries.BIOME
		)));

		pack.addProvider(ModModelProvider::new);
	}
}
