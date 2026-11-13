package archives.tater.segfault.util;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface SlotReference {
    ItemStack get();
    void set(ItemStack stack);
    void setChanged();

    default boolean isEmpty() {
        return get().isEmpty();
    }

    default ItemStack split(int amount) {
        var result = get().split(amount);
        setChanged();
        return result;
    }

    static SlotReference of(Slot slot) {
        return new SlotReference() {
            @Override
            public ItemStack get() {
                return slot.getItem();
            }

            @Override
            public void set(ItemStack stack) {
                slot.set(stack);
            }

            @Override
            public void setChanged() {
                slot.setChanged();
            }
        };
    }

    static SlotReference of(SlotAccess access) {
        return new SlotReference() {
            @Override
            public ItemStack get() {
                return access.get();
            }

            @Override
            public void set(ItemStack stack) {
                access.set(stack);
            }

            @Override
            public void setChanged() {
                access.set(access.get());
            }
        };
    }
}
