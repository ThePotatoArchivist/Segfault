package archives.tater.segfault.menu;

import archives.tater.segfault.registry.SegfaultModMenus;
import archives.tater.segfault.util.SegfaultUtil;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SingleSlotMenu extends AbstractContainerMenu {

    private final Container container;

    public SingleSlotMenu(int containerId, Inventory inventory) {
        this(SegfaultModMenus.SINGLE_SLOT, containerId, inventory);
    }

    protected SingleSlotMenu(MenuType<? extends SingleSlotMenu> menuType, int containerId, Inventory inventory) {
        this(menuType, containerId, inventory, new SimpleContainer(1));
    }

    public SingleSlotMenu(int containerId, Inventory inventory, Container container) {
        this(SegfaultModMenus.SINGLE_SLOT, containerId, inventory, container);
    }

    protected SingleSlotMenu(MenuType<? extends SingleSlotMenu> menuType, int containerId, Inventory inventory, Container container) {
        super(menuType, containerId);
        this.container = container;
        checkContainerSize(container, 1);
        addSlot(createSlot(container, 0, 80, 20));
        addStandardInventorySlots(inventory, 8, 51);
    }

    protected Slot createSlot(Container container, int slot, int x, int y) {
        return new Slot(container, slot, x, y);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return SegfaultUtil.quickMoveStack(this, container, 1, player, slotIndex);
    }
}
