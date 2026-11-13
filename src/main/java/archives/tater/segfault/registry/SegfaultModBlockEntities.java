package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.block.entity.AllocatorBlockEntity;
import archives.tater.segfault.block.entity.DereferencerBlockEntity;
import archives.tater.segfault.block.entity.DestructorBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.debug.DebugStructureInfo;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface SegfaultModBlockEntities {
    BlockEntityType<AllocatorBlockEntity> ALLOCATOR = register("allocator", AllocatorBlockEntity::new, SegfaultModBlocks.ALLOCATOR);
    BlockEntityType<DereferencerBlockEntity> DEREFERENCER = register("dereferencer", DereferencerBlockEntity::new, SegfaultModBlocks.DEREFERENCER);
    BlockEntityType<DestructorBlockEntity> DESTRUCTOR = register("destructor", DestructorBlockEntity::new, SegfaultModBlocks.DESTRUCTOR);

    private static <T extends BlockEntity> BlockEntityType<T> register(String path, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... supportedBlocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Segfault.id(path), FabricBlockEntityTypeBuilder.create(factory, supportedBlocks).build());
    }

    static void init() {

    }
}
