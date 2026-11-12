package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.references.BlockItemId;

public interface SegfaultModBlockItemIds {

    BlockItemId ALLOCATOR = create("allocator");

    private static BlockItemId create(String path) {
        var id = Segfault.id(path);
        return BlockItemId.create(id, id);
    }
}
