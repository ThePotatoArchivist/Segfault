package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.timeline.Timeline;

public interface SegfaultModTimelineTags {

    TagKey<Timeline> IN_MEMORY = create("in_data");

    private static TagKey<Timeline> create(String path) {
        return TagKey.create(Registries.TIMELINE, Segfault.id(path));
    }
}
