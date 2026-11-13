package archives.tater.segfault.client.datagen;

import archives.tater.segfault.registry.SegfaultModBlockItemIds;
import archives.tater.segfault.registry.SegfaultModBlockTags;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(SegfaultModBlockTags.MEMORY_MACHINES)
                .add(SegfaultModBlockItemIds.ALLOCATOR)
                .add(SegfaultModBlockItemIds.DEREFERENCER);

        builder(SegfaultModBlockTags.PERSISTS_EXITED_STATE)
                .add(SegfaultModBlockItemIds.DEREFERENCER);

        builder(BlockTags.MINEABLE_WITH_PICKAXE).addTag(SegfaultModBlockTags.MEMORY_MACHINES);
        builder(BlockTags.NEEDS_IRON_TOOL).addTag(SegfaultModBlockTags.MEMORY_MACHINES);
        builder(BlockTags.BLOCKS_MOTION_NO_LEAVES).addTag(SegfaultModBlockTags.MEMORY_MACHINES);
    }
}
