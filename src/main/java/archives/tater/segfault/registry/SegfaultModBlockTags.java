package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public interface SegfaultModBlockTags {

    TagKey<Block> MEMORY_MACHINES = create("memory_machines");

    private static TagKey<Block> create(String path) {
        return TagKey.create(Registries.BLOCK, Segfault.id(path));
    }
}
