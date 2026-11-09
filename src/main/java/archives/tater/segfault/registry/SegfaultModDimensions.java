package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

import java.util.List;
import java.util.Optional;

public interface SegfaultModDimensions {
    ResourceKey<LevelStem> MEMORY = ResourceKey.create(Registries.LEVEL_STEM, Segfault.id("memory"));

    static void bootstrap(BootstrapContext<LevelStem> context) {
        context.register(MEMORY, new LevelStem(
                context.lookup(Registries.DIMENSION_TYPE).getOrThrow(SegfaultModDimensionTypes.MEMORY),
                new FlatLevelSource(new FlatLevelGeneratorSettings(
                        Optional.empty(),
                        context.lookup(Registries.BIOME).getOrThrow(SegfaultModBiomes.MEMORY),
                        List.of()
                ))
        ));
    }
}