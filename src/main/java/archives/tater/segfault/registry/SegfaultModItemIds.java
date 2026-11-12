package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public interface SegfaultModItemIds {
    ResourceKey<Item> POINTER = create("pointer");

    private static ResourceKey<Item> create(String path) {
        return ResourceKey.create(Registries.ITEM, Segfault.id(path));
    }
}
