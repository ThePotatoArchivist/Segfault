package archives.tater.segfault.registry;

import archives.tater.segfault.block.AllocatorBlock;
import archives.tater.segfault.block.DereferencerBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public interface SegfaultModBlocks {

    Block ALLOCATOR = register(SegfaultModBlockItemIds.ALLOCATOR, AllocatorBlock::new, memoryMachineProperties());
    Block DEREFERENCER = register(SegfaultModBlockItemIds.DEREFERENCER, DereferencerBlock::new, memoryMachineProperties());

    private static Block register(BlockItemId id, Function<BlockBehaviour.Properties, Block> block, BlockBehaviour.Properties properties) {
        return register(id.block(), block, properties);
    }

    private static Block register(ResourceKey<Block> key, Function<BlockBehaviour.Properties, Block> block, BlockBehaviour.Properties properties) {
        return Registry.register(BuiltInRegistries.BLOCK, key, block.apply(properties.setId(key)));
    }

    private static BlockBehaviour.Properties memoryMachineProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(3.5F)
                .sound(SoundType.IRON);
    }

    static void init() {

    }
}
