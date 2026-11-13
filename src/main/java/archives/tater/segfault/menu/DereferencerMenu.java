package archives.tater.segfault.menu;

import archives.tater.segfault.registry.SegfaultModComponents;
import archives.tater.segfault.registry.SegfaultModMenus;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DereferencerMenu extends SingleSlotMenu {
    public DereferencerMenu(int containerId, Inventory inventory) {
        super(SegfaultModMenus.DEREFERENCER, containerId, inventory);
    }

    public DereferencerMenu(int containerId, Inventory inventory, Container container) {
        super(SegfaultModMenus.DEREFERENCER, containerId, inventory, container);
    }

    @Override
    protected Slot createSlot(Container container, int slot, int x, int y) {
        return new Slot(container, slot, x, y) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.has(SegfaultModComponents.POINTER);
            }
        };
    }
}
