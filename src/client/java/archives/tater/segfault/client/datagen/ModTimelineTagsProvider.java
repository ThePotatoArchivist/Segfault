package archives.tater.segfault.client.datagen;

import archives.tater.segfault.registry.SegfaultModTimelineTags;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TimelineTags;
import net.minecraft.world.timeline.Timeline;

import java.util.concurrent.CompletableFuture;

public class ModTimelineTagsProvider extends FabricTagsProvider<Timeline> {
    public ModTimelineTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.TIMELINE, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(SegfaultModTimelineTags.IN_MEMORY)
                .forceAddTag(TimelineTags.UNIVERSAL);
    }
}
