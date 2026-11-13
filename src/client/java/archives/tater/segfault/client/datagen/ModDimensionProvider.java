package archives.tater.segfault.client.datagen;

import archives.tater.segfault.registry.SegfaultModDimensions;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ModDimensionProvider extends FabricCodecDataProvider<LevelStem> {

    public ModDimensionProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture, Registries.LEVEL_STEM, LevelStem.CODEC);
    }

    @Override
    protected void configure(BiConsumer<Identifier, LevelStem> provider, HolderLookup.Provider registryLookup) {
        SegfaultModDimensions.bootstrap((key, levelStem) -> provider.accept(key.identifier(), levelStem), registryLookup);
    }
    @Override
    public String getName() {
        return "Dimensions";
    }
}
