package archives.tater.segfault.menu;

import archives.tater.segfault.block.entity.ConstructorBlockEntity;
import archives.tater.segfault.registry.SegfaultModComponents;
import archives.tater.segfault.registry.SegfaultModMenus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ConstructorMenu extends AbstractContainerMenu {
    private static final int CONTAINER_SIZE = 3;
    private final Container constructor;

    public ConstructorMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(CONTAINER_SIZE));
    }

    public ConstructorMenu(int containerId, Inventory inventory, Container constructor) {
        super(SegfaultModMenus.CONSTRUCTOR, containerId);
        this.constructor = constructor;
        checkContainerSize(constructor, CONTAINER_SIZE);
        constructor.startOpen(inventory.player);

        addSlot(new Slot(constructor, ConstructorBlockEntity.SLOT_INPUT, 53, 20) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.has(SegfaultModComponents.POINTER);
            }
        });
        addSlot(new Slot(constructor, ConstructorBlockEntity.SLOT_TEMPLATE, 80, 42) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.has(SegfaultModComponents.STRUCT_TEMPLATE);
            }
        });
        addSlot(new Slot(constructor, ConstructorBlockEntity.SLOT_RESULT, 107, 20) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }
        });

        addStandardInventorySlots(inventory, 8, 73);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        var slot = slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        var stack = slot.getItem();
        var clicked = stack.copy();

        if (slotIndex < constructor.getContainerSize()) {
            if (!moveItemStackTo(stack, constructor.getContainerSize(), slots.size(), true))
                return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 2, false))
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

    @Override
    public boolean stillValid(Player player) {
        return constructor.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        constructor.stopOpen(player);
    }
}
