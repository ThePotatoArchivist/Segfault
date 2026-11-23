package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.references.BlockItemId;

public interface SegfaultModBlockItemIds {

    BlockItemId CONSTRUCTOR = create("constructor");
    BlockItemId DEREFERENCER = create("dereferencer");
    BlockItemId DESTRUCTOR = create("destructor");
    BlockItemId STRUCT_FRAME = create("struct_frame");

    private static BlockItemId create(String path) {
        var id = Segfault.id(path);
        return BlockItemId.create(id, id);
    }
}
