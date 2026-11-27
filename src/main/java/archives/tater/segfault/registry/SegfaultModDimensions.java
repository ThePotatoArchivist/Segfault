package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public interface SegfaultModDimensions {

    ResourceKey<Level> HEAP = create("heap");

    private static ResourceKey<Level> create(String path) {
        return ResourceKey.create(Registries.DIMENSION, Segfault.id(path));
    }

    static void bootstrap(BiConsumer<ResourceKey<Level>, LevelStem> provider, HolderLookup.Provider registries) {
        provider.accept(HEAP, new LevelStem(
                registries.getOrThrow(SegfaultModDimensionTypes.HEAP),
                new FlatLevelSource(new FlatLevelGeneratorSettings(
                        Optional.empty(),
                        registries.getOrThrow(SegfaultModBiomes.HEAP),
                        List.of()
                ))
        ));
    }
}