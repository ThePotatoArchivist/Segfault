package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.references.BlockItemId;

public interface SegfaultModBlockItemIds {

    BlockItemId ALLOCATOR = create("allocator");
    BlockItemId DEREFERENCER = create("dereferencer");
    BlockItemId DESTRUCTOR = create("destructor");

    private static BlockItemId create(String path) {
        var id = Segfault.id(path);
        return BlockItemId.create(id, id);
    }
}
