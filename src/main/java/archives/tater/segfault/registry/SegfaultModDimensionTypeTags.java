package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;

public interface SegfaultModDimensionTypeTags {
    TagKey<DimensionType> HAS_NO_WEATHER = create("has_no_weather");

    private static TagKey<DimensionType> create(String path) {
        return TagKey.create(Registries.DIMENSION_TYPE, Segfault.id(path));
    }
}
