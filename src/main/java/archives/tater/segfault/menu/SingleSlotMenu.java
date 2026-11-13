package archives.tater.segfault.menu;

import archives.tater.segfault.registry.SegfaultModMenus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SingleSlotMenu extends AbstractContainerMenu {
    private static final int INV_SLOT_START = 1;
    private static final int INV_SLOT_END = INV_SLOT_START + Inventory.INVENTORY_SIZE;

    private final Container container;

    public SingleSlotMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(1));
    }

    public SingleSlotMenu(int containerId, Inventory inventory, Container container) {
        super(SegfaultModMenus.SINGLE_SLOT, containerId);
        this.container = container;
        checkContainerSize(container, 1);
        addSlot(new Slot(container, 0, 80, 20));
        addStandardInventorySlots(inventory, 8, 51);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        var slot = slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        var stack = slot.getItem();
        var clicked = stack.copy();

        if (slotIndex == 0) {
            if (!moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, true))
                return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, INV_SLOT_START, false))
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
}
