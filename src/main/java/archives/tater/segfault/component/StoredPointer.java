package archives.tater.segfault.component;

import archives.tater.segfault.registry.SegfaultModComponents;
import archives.tater.segfault.util.SlotReference;

import net.fabricmc.fabric.api.util.EventResult;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;

import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.util.ExtraCodecs.optionalEmptyMap;

public record StoredPointer(Optional<ItemStackTemplate> item) implements TooltipProvider {
    public static final StoredPointer EMPTY = new StoredPointer(Optional.empty());

    public static final Codec<StoredPointer> CODEC = optionalEmptyMap(ItemStackTemplate.CODEC).xmap(StoredPointer::new, StoredPointer::item);
    public static final StreamCodec<RegistryFriendlyByteBuf, StoredPointer> STREAM_CODEC = ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).map(StoredPointer::new, StoredPointer::item);

    public boolean isEmpty() {
        return item.isEmpty();
    }

    public ItemStack itemCopy() {
        return item.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
    }

    public static StoredPointer of(ItemStack stack) {
        return new StoredPointer(Optional.of(ItemStackTemplate.fromNonEmptyStack(stack)));
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        var pointer = itemCopy().get(SegfaultModComponents.POINTER);
        if (pointer == null) return;
        consumer.accept(Component.translatable("item.segfault.pointer.target", pointer.getDescription()).withStyle(ChatFormatting.GOLD));
    }

    public static @Nullable InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        var storedPointer = player.getItemInHand(interactionHand).get(SegfaultModComponents.STORED_POINTER);
        if (storedPointer == null) return null;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;

        var destination = PointerValue.getDestination(serverLevel, storedPointer.itemCopy().get(SegfaultModComponents.POINTER), player);
        if (destination == null) return InteractionResult.FAIL;

        player.teleport(destination);
        return InteractionResult.SUCCESS;
    }

    public static EventResult onItemClickBehavior(ItemStack hoveredItem, Slot hoveredSlot, ItemStack itemHeldByCursor, SlotAccess slotHeldByCursor, ClickAction clickAction, Player player) {
        var hoveredRef = SlotReference.of(hoveredSlot);
        var heldRef = SlotReference.of(slotHeldByCursor);

        if (clickAction == ClickAction.SECONDARY) {
            if (tryRemove(hoveredRef, heldRef) || tryRemove(heldRef, hoveredRef))
                return EventResult.DENY;
        } else {
            if (tryInsert(hoveredRef, heldRef) || tryInsert(heldRef, hoveredRef))
                return EventResult.DENY;
        }

        return EventResult.PASS;
    }

    private static boolean tryInsert(SlotReference source, SlotReference target) {
        var stack = source.get();
        var storedPointer = stack.get(SegfaultModComponents.STORED_POINTER);
        if (storedPointer == null || !storedPointer.isEmpty() || !target.get().has(SegfaultModComponents.POINTER)) return false;

        stack.set(SegfaultModComponents.STORED_POINTER, of(target.split(1)));
        source.setChanged();
        return true;
    }

    private static boolean tryRemove(SlotReference source, SlotReference target) {
        if (!target.isEmpty()) return false;

        var stack = source.get();
        var storedPointer = stack.get(SegfaultModComponents.STORED_POINTER);
        if (storedPointer == null || storedPointer.isEmpty()) return false;

        stack.set(SegfaultModComponents.STORED_POINTER, EMPTY);
        source.setChanged();
        target.set(storedPointer.itemCopy());
        return true;
    }
}
