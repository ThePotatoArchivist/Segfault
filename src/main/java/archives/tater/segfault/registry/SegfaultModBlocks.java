package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.block.ConstructorBlock;
import archives.tater.segfault.block.DereferencerBlock;
import archives.tater.segfault.block.DestructorBlock;
import archives.tater.segfault.block.StructFrameBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public interface SegfaultModBlocks {

    Block CONSTRUCTOR = register(SegfaultModBlockItemIds.CONSTRUCTOR, ConstructorBlock::new, memoryMachineProperties());
    Block DEREFERENCER = register(SegfaultModBlockItemIds.DEREFERENCER, DereferencerBlock::new, memoryMachineProperties());
    Block DESTRUCTOR = register(SegfaultModBlockItemIds.DESTRUCTOR, DestructorBlock::new, memoryMachineProperties());
    Block STRUCT_FRAME = register(SegfaultModBlockItemIds.STRUCT_FRAME, StructFrameBlock::new, memoryComponentProperties());

    private static Block register(BlockItemId id, Function<BlockBehaviour.Properties, Block> block, BlockBehaviour.Properties properties) {
        return register(id.block(), block, properties);
    }

    private static Block register(ResourceKey<Block> key, Function<BlockBehaviour.Properties, Block> block, BlockBehaviour.Properties properties) {
        return Registry.register(BuiltInRegistries.BLOCK, key, block.apply(properties.setId(key)));
    }

    private static BlockBehaviour.Properties memoryMachineProperties() {
        return memoryComponentProperties()
            .isRedstoneConductor(Blocks::never);
    }

    private static BlockBehaviour.Properties memoryComponentProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(3.5F)
                .sound(SoundType.IRON);
    }

    static void init() {
        BuiltInRegistries.BLOCK.addAlias(Segfault.id("allocator"), BuiltInRegistries.BLOCK.getKey(CONSTRUCTOR));
    }
}
