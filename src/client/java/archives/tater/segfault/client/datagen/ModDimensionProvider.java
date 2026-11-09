package archives.tater.segfault.client.datagen;

import archives.tater.segfault.registry.SegfaultModDimensions;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class ModDimensionProvider extends FabricCodecDataProvider<LevelStem> {

    public static final HolderOwner<LevelStem> EMPTY_OWNER = new HolderOwner<>() {

    };

    public ModDimensionProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture, Registries.LEVEL_STEM, LevelStem.CODEC);
    }

    private static <T> HolderLookup.RegistryLookup<T> createEmpty(ResourceKey<? extends Registry<T>> key) {
        return new HolderLookup.RegistryLookup<T>() {
            @Override
            public ResourceKey<? extends Registry<? extends T>> key() {
                return key;
            }

            @Override
            public Lifecycle registryLifecycle() {
                return Lifecycle.stable();
            }

            @Override
            public Stream<Holder.Reference<T>> listElements() {
                return Stream.empty();
            }

            @Override
            public Stream<HolderSet.Named<T>> listTags() {
                return Stream.empty();
            }

            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> id) {
                return Optional.empty();
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> id) {
                return Optional.empty();
            }
        };
    }

    @Override
    protected void configure(BiConsumer<Identifier, LevelStem> provider, HolderLookup.Provider registryLookup) {
        SegfaultModDimensions.bootstrap(new BootstrapContext<>() {
            @Override
            public Holder.Reference<LevelStem> register(ResourceKey<LevelStem> key, LevelStem value) {
                provider.accept(key.identifier(), value);
                return Holder.Reference.createStandAlone(registryLookup.lookup(Registries.LEVEL_STEM).<HolderOwner<LevelStem>>map(Function.identity()).orElse(EMPTY_OWNER), key);
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
                return registryLookup.lookupOrThrow(key);
            }

            @Override
            public <S> Stream<Holder.Reference<S>> listContextElements(ResourceKey<? extends Registry<? extends S>> key) {
                return registryLookup.lookupOrThrow(key).listElements();
            }
        });
    }

    @Override
    public String getName() {
        return "Dimensions";
    }
}
