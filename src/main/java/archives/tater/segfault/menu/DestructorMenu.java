package archives.tater.segfault.menu;

import archives.tater.segfault.block.entity.DestructorBlockEntity;
import archives.tater.segfault.registry.SegfaultModComponents;
import archives.tater.segfault.registry.SegfaultModMenus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DestructorMenu extends AbstractContainerMenu {
    private static final int CONTAINER_SIZE = 2;
    private final Container allocator;

    public DestructorMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(CONTAINER_SIZE));
    }

    public DestructorMenu(int containerId, Inventory inventory, Container destructor) {
        super(SegfaultModMenus.DESTRUCTOR, containerId);
        this.allocator = destructor;
        checkContainerSize(destructor, CONTAINER_SIZE);
        destructor.startOpen(inventory.player);

        addSlot(new Slot(destructor, DestructorBlockEntity.SLOT_INPUT, 53, 20) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.has(SegfaultModComponents.POINTER);
            }
        });
        addSlot(new Slot(destructor, DestructorBlockEntity.SLOT_RESULT, 107, 20) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }
        });

        addStandardInventorySlots(inventory, 8, 51);
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
        } else if (!moveItemStackTo(stack, 0, 1, false))
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
