package archives.tater.segfault.util;

import archives.tater.segfault.mixin.AbstractContainerMenuAccessor;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static net.minecraft.util.Util.toMap;

public class SegfaultUtil {
    private SegfaultUtil() {}

    public static <T, E> Stream<T> foldCompose(Stream<T> initial, Iterable<E> operations, Function<E, UnaryOperator<Stream<T>>> apply) {
        var stream = initial;
        for (var operation : operations)
            stream = apply.apply(operation).apply(stream);
        return stream;
    }

    public static <T> List<T> withAppended(List<T> list, T value) {
        var result = new ArrayList<T>(list.size() + 1);
        result.addAll(list);
        result.add(value);
        return result;
    }

    public static <D> ExtendedMenuProvider<D> simpleExtendedMenuProvider(MenuConstructor constructor, Component displayName, D data) {
        return new ExtendedMenuProvider<>() {
            @Override
            public D getScreenOpeningData(ServerPlayer player) {
                return data;
            }

            @Override
            public Component getDisplayName() {
                return displayName;
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return constructor.createMenu(containerId, inventory, player);
            }
        };
    }

    public static ItemStack quickMoveStack(AbstractContainerMenu menu, Container container, int inputSlotCount, Player player, int slotIndex) {
        var slot = menu.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        var stack = slot.getItem();
        var clicked = stack.copy();

        if (slotIndex < container.getContainerSize()) {
            if (!((AbstractContainerMenuAccessor) menu).callMoveItemStackTo(stack, container.getContainerSize(), menu.slots.size(), true))
                return ItemStack.EMPTY;
        } else if (!((AbstractContainerMenuAccessor) menu).callMoveItemStackTo(stack, 0, inputSlotCount, false))
            return ItemStack.EMPTY;

        if (stack.isEmpty())
            slot.setByPlayer(ItemStack.EMPTY);
        else
            slot.setChanged();

        if (stack.getCount() == clicked.getCount())
            return ItemStack.EMPTY;

        slot.onTake(player, stack);

        return clicked;
    }

    public static Iterable<BlockPos> betweenClosed(BoundingBox box) {
        return BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
    }

    public static <K, V> Codec<Map<K, V>> entryListMapCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
        return entryCodec(keyCodec, valueCodec).listOf().xmap(
                entries -> entries.stream().collect(toMap()),
                map -> map.entrySet().stream().toList()
        );
    }

    public static <K, V> Codec<Map.Entry<K, V>> entryCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                keyCodec.fieldOf("key").forGetter(Map.Entry::getKey),
                valueCodec.fieldOf("value").forGetter(Map.Entry::getValue)
        ).apply(instance, Map::entry));
    }
}
