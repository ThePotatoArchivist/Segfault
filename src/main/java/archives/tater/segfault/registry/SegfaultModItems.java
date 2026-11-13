package archives.tater.segfault.registry;

import archives.tater.segfault.component.PointerValue;

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
    Item DEREFERENCER = register(SegfaultModBlockItemIds.DEREFERENCER, SegfaultModBlocks.DEREFERENCER);

    Item POINTER = register(SegfaultModItemIds.POINTER, new Item.Properties()
            .component(SegfaultModComponents.POINTER, PointerValue.NULL)
    );

    private static Item register(BlockItemId id, Block block) {
        return register(id, block, new Item.Properties());
    }

    private static Item register(BlockItemId id, Block block, Item.Properties properties) {
        return register(id, properties1 -> new BlockItem(block, properties1), properties.useBlockDescriptionPrefix());
    }

    private static Item register(BlockItemId id, Function<Item.Properties, Item> item, Item.Properties properties) {
        return register(id.item(), item, properties);
    }

    private static Item register(ResourceKey<Item> key, Item.Properties properties) {
        return register(key, Item::new, properties);
    }

    private static Item register(ResourceKey<Item> key, Function<Item.Properties, Item> item, Item.Properties properties) {
        return Registry.register(BuiltInRegistries.ITEM, key, item.apply(properties.setId(key)));
    }

    static void init() {

    }
}
