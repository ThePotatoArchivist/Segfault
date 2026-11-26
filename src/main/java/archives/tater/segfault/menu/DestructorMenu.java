package archives.tater.segfault.menu;

import archives.tater.segfault.block.entity.DestructorBlockEntity;
import archives.tater.segfault.registry.SegfaultModComponents;
import archives.tater.segfault.registry.SegfaultModMenus;
import archives.tater.segfault.util.SegfaultUtil;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DestructorMenu extends AbstractContainerMenu {
    private static final int CONTAINER_SIZE = 2;
    private final Container destructor;

    public DestructorMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(CONTAINER_SIZE));
    }

    public DestructorMenu(int containerId, Inventory inventory, Container destructor) {
        super(SegfaultModMenus.DESTRUCTOR, containerId);
        this.destructor = destructor;
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
        return SegfaultUtil.quickMoveStack(this, destructor, 1, player, slotIndex);
    }

    @Override
    public boolean stillValid(Player player) {
        return destructor.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        destructor.stopOpen(player);
    }
}
