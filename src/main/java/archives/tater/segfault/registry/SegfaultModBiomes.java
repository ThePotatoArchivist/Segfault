package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;

import static net.minecraft.util.ARGB.vector3fFromRGB24;

public interface SegfaultModBiomes {

    ResourceKey<Biome> MEMORY = create("memory");

    private static ResourceKey<Biome> create(String path) {
        return ResourceKey.create(Registries.BIOME, Segfault.id(path));
    }

    static void bootstrap(BootstrapContext<Biome> context) {
        context.register(MEMORY, OverworldBiomes.baseBiome(0.5f, 0.5f)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, vector3fFromRGB24(0xff0a0a0a))
                .hasPrecipitation(false)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .mobSpawnSettings(MobSpawnSettings.NO_SPAWNS)
                .build());
    }
}
