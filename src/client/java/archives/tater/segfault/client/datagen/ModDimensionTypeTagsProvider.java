package archives.tater.segfault.client.datagen;

import archives.tater.segfault.registry.SegfaultModDimensionTypeTags;
import archives.tater.segfault.registry.SegfaultModDimensionTypes;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.concurrent.CompletableFuture;

public class ModDimensionTypeTagsProvider extends FabricTagsProvider<DimensionType> {
    public ModDimensionTypeTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.DIMENSION_TYPE, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(SegfaultModDimensionTypeTags.HAS_NO_WEATHER)
                .add(SegfaultModDimensionTypes.MEMORY);
    }
}
