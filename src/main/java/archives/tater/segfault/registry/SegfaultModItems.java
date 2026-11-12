package archives.tater.segfault.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public interface SegfaultModItems {

    Item ALLOCATOR = register(SegfaultModBlockItemIds.ALLOCATOR, SegfaultModBlocks.ALLOCATOR);

    private static Item register(BlockItemId id, Block block) {
        return register(id, block, new Item.Properties());
    }

    private static Item register(BlockItemId id, Block block, Item.Properties properties) {
        return register(id, properties1 -> new BlockItem(block, properties1), properties.useBlockDescriptionPrefix());
    }

    private static Item register(BlockItemId id, Function<Item.Properties, Item> item, Item.Properties properties) {
        return register(id.item(), item, properties);
    }

    private static Item register(ResourceKey<Item> key, Function<Item.Properties, Item> item, Item.Properties properties) {
        return Registry.register(BuiltInRegistries.ITEM, key, item.apply(properties.setId(key)));
    }

    static void init() {

    }
}
