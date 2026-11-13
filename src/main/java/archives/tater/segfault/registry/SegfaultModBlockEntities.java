package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.block.entity.AllocatorBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface SegfaultModBlockEntities {
    BlockEntityType<AllocatorBlockEntity> ALLOCATOR = register("allocator", AllocatorBlockEntity::new, SegfaultModBlocks.ALLOCATOR);

    private static <T extends BlockEntity> BlockEntityType<T> register(String path, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... supportedBlocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Segfault.id(path), FabricBlockEntityTypeBuilder.create(factory, supportedBlocks).build());
    }

    static void init() {

    }
}
