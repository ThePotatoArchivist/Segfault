package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public interface SegfaultModItemIds {
    ResourceKey<Item> POINTER = create("pointer");
    ResourceKey<Item> STRUCT_TEMPLATE = create("struct_template");
    ResourceKey<Item> PORTABLE_DEREFERENCER = create("portable_dereferencer");

    private static ResourceKey<Item> create(String path) {
        return ResourceKey.create(Registries.ITEM, Segfault.id(path));
    }
}
