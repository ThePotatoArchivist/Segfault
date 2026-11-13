package archives.tater.segfault.menu;

import archives.tater.segfault.block.entity.AllocatorBlockEntity;
import archives.tater.segfault.registry.SegfaultModComponents;
import archives.tater.segfault.registry.SegfaultModMenus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AllocatorMenu extends AbstractContainerMenu {
    private static final int CONTAINER_SIZE = 3;
    private final Container allocator;

    public AllocatorMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(CONTAINER_SIZE));
    }

    public AllocatorMenu(int containerId, Inventory inventory, Container allocator) {
        super(SegfaultModMenus.ALLOCATOR, containerId);
        this.allocator = allocator;
        checkContainerSize(allocator, CONTAINER_SIZE);
        allocator.startOpen(inventory.player);

        addSlot(new Slot(allocator, AllocatorBlockEntity.SLOT_INPUT, 53, 20) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.has(SegfaultModComponents.POINTER);
            }
        });
        addSlot(new Slot(allocator, AllocatorBlockEntity.SLOT_TEMPLATE, 80, 42) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.has(SegfaultModComponents.STRUCT_TEMPLATE);
            }
        });
        addSlot(new Slot(allocator, AllocatorBlockEntity.SLOT_RESULT, 107, 20) {
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

        if (slotIndex < allocator.getContainerSize()) {
            if (!moveItemStackTo(stack, allocator.getContainerSize(), slots.size(), true))
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
        return allocator.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        allocator.stopOpen(player);
    }
}
