package archives.tater.segfault.menu;

import archives.tater.segfault.block.entity.ConstructorBlockEntity;
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
        return SegfaultUtil.quickMoveStack(this, constructor, 2, player, slotIndex);
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

    @Override
    public boolean moveItemStackTo(ItemStack itemStack, int startSlot, int endSlot, boolean backwards) {
        return super.moveItemStackTo(itemStack, startSlot, endSlot, backwards);
    }
}
